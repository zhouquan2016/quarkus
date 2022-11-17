package com.zhqn.platform.gprc;

import com.google.protobuf.ByteString;
import com.zhqn.common.platform.*;
import com.zhqn.platform.props.PlatformProperties;
import io.grpc.stub.StreamObserver;
import io.netty.util.internal.StringUtil;
import io.quarkus.grpc.GrpcService;
import org.graalvm.nativeimage.c.struct.SizeOf;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.*;
import java.util.UUID;

@GrpcService
public class FileServiceGrpcImpl extends FileServiceGrpc.FileServiceImplBase {

    @Inject
    PlatformProperties platformProperties;

    @PostConstruct
    void init() {
        File dir = new File(platformProperties.uploadPath());
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException(dir.getAbsolutePath() + ":mkdirs fail!");
            }
        }
    }
    @Override
    public StreamObserver<UploadRequest> upload(StreamObserver<UploadResponse> responseObserver) {
        return new StreamObserver<>() {
            private FileOutputStream outputStream;

            File uploadFile;

            String fileNo;

            @Override
            public void onNext(UploadRequest uploadRequest) {
                try {
                    if (outputStream == null) {
                        if (StringUtil.isNullOrEmpty(uploadRequest.getFilename())) {
                            throw new RuntimeException("文件名为空");
                        }
                        try {
                            fileNo = UUID.randomUUID().toString().replaceAll("-", "");
                            File uploadDir = new File(platformProperties.uploadPath(), fileNo);
                            if (!uploadDir.exists()) {
                                if (!uploadDir.mkdirs()) {
                                    throw new RuntimeException("创建目录失败");
                                }
                            }
                            uploadFile = new File(uploadDir, uploadRequest.getFilename());
                            outputStream = new FileOutputStream(uploadFile);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (uploadRequest.hasContents() && uploadRequest.getContents().size() > 0) {
                        try {
                            uploadRequest.getContents().writeTo(outputStream);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (Exception e) {
                    //发生任何异常，关闭文件流，删除文件
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ex) {
                        }
                    }
                    if (uploadFile != null) {
                        uploadFile.delete();
                        uploadFile.getParentFile().delete();
                    }
                    throw e;
                }

            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onNext(UploadResponse.newBuilder().setSuccess(false).setError(throwable.getMessage()).build());
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                try {
                    responseObserver.onNext(UploadResponse.newBuilder().setSuccess(true).setFileNo(fileNo).build());
                    responseObserver.onCompleted();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ex) {
                        }
                    }
                }


            }
        };
    }

    @Override
    public void download(DownloadRequest request, StreamObserver<DownloadResponse> responseObserver) {
        FileInputStream is = null;
        try {
            if (StringUtil.isNullOrEmpty(request.getFileNo())) {
                throw new RuntimeException("文件编码为空");
            }
            File dir = new File(platformProperties.uploadPath(), request.getFileNo());
            if (!dir.exists() || !dir.isDirectory()) {
                throw new RuntimeException("文件不存在");
            }
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                throw new RuntimeException("文件不存在");
            }
            if (files.length > 1) {
                throw new RuntimeException("文件存在多个");
            }
            is = new FileInputStream(files[0]);
            byte[] buff = new byte[4098];
            int size;
            int totalSize = 0;
            while ((size = is.read(buff)) != -1) {
                responseObserver.onNext(DownloadResponse.newBuilder().setContents(ByteString.copyFrom(buff, 0, size)).build());
                totalSize += size;
                System.out.println(totalSize / 1024 / 1024 + "m");
            }
        }catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            responseObserver.onCompleted();
        }

    }
}
