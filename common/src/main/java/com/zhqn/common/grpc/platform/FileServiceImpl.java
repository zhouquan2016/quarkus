package com.zhqn.common.grpc.platform;

import com.zhqn.common.platform.DownloadRequest;
import com.zhqn.common.platform.DownloadResponse;
import com.zhqn.common.platform.FileServiceGrpc;
import com.zhqn.common.platform.UploadResponse;
import io.netty.util.internal.StringUtil;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.Iterator;

@ApplicationScoped
@Slf4j
public class FileServiceImpl implements FileService{

    @GrpcClient("platform")
    com.zhqn.common.platform.FileService fileService;

    @GrpcClient("platform")
    FileServiceGrpc.FileServiceBlockingStub fileServiceBlockingStub;

    @Override
    public Uni<BaseResponse<String>> uploadFile(String fileName, InputStream is) {
        if (StringUtil.isNullOrEmpty(fileName)) {
            return BaseResponse.failUni("文件名为空");
        }
        if (is == null) {
            return BaseResponse.failUni("文件流为空");
        }
        try {
            return fileService.upload(Multi.createFrom().iterable(new FileUploadIterator(fileName, is)))
                    .onFailure().recoverWithUni(throwable -> {
                        log.error("grpc upload file:{},error", fileName, throwable);
                        return Uni.createFrom().item(UploadResponse.newBuilder().setSuccess(false).setError("上传失败").build());
                    })
                    .onTermination().invoke(() -> {
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    }).onItem().transform(uploadResponse -> {
                        BaseResponse<String> response = new BaseResponse<>();
                        response.setSuccess(uploadResponse.getSuccess());
                        response.setData(uploadResponse.getFileNo());
                        response.setError(uploadResponse.getError());
                        return response;
                    });
        }catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.failUni("系统异常");
        }

    }


    @Override
    public Uni<BaseResponse<String>> uploadFile(File file) {
        try (FileInputStream is = new FileInputStream(file)) {
            return uploadFile(file.getName(), is);
        } catch (FileNotFoundException e) {
            return BaseResponse.failUni("文件不存在");
        } catch (IOException e) {
            return BaseResponse.failUni("文件读取失败:" + e.getMessage());
        }
    }

    @Override
    public File downloadFile(String fileNo) {
        if (StringUtil.isNullOrEmpty(fileNo)) {
            throw new RuntimeException("文件不存在");
        }
        try {
            File out = File.createTempFile(fileNo, ".tmp");
            try (FileOutputStream os  = new FileOutputStream(out)) {
                Iterator<DownloadResponse> responseIterator = fileServiceBlockingStub.download(DownloadRequest.newBuilder().setFileNo(fileNo).build());
                while (responseIterator.hasNext()) {
                    responseIterator.next().getContents().writeTo(os);
                }
            }
            return out;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public Uni<File> downloadFile1(String fileNo) {
        if (StringUtil.isNullOrEmpty(fileNo)) {
            throw new RuntimeException("文件不存在");
        }
        try {
            File out = File.createTempFile(fileNo, ".tmp");
            out.deleteOnExit();
            FileOutputStream os  = new FileOutputStream(out);
            return fileService.download(DownloadRequest.newBuilder().setFileNo(fileNo).build()).onItem().invoke(Unchecked.consumer(downloadResponse -> {
                try {
                    downloadResponse.getContents().writeTo(os);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })).onCompletion().continueWith().toUni().replaceWith(out).onTermination().invoke(() -> {
                try {
                    os.close();
                } catch (IOException e) {
                }
//                out.delete();
            });
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response downloadFile2(String fileNo) {
        if (StringUtil.isNullOrEmpty(fileNo)) {
            throw new RuntimeException("文件不存在");
        }
        StreamingOutput streamingOutput = output -> {
            Iterator<DownloadResponse> responseIterator = fileServiceBlockingStub.download(DownloadRequest.newBuilder().setFileNo(fileNo).build());
            while (responseIterator.hasNext()) {
                responseIterator.next().getContents().writeTo(output);
            }
            output.close();
        };
        return Response.ok(streamingOutput)
                .header( "Content-Disposition", "attachment; filename=\"" + fileNo + "\"" )
//                .header("Content-Length", out.length())
                .build();
    }
}
