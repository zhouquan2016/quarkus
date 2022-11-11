package com.zhqn.platform.gprc;

import com.zhqn.common.platform.FileServiceGrpc;
import com.zhqn.common.platform.UploadRequest;
import com.zhqn.common.platform.UploadResponse;
import com.zhqn.platform.props.PlatformProperties;
import io.grpc.stub.StreamObserver;
import io.netty.util.internal.StringUtil;
import io.quarkus.grpc.GrpcService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
        return new StreamObserver<UploadRequest>() {
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
                    }else if (uploadRequest.hasContents() && uploadRequest.getContents().size() > 0){
                        try {
                            uploadRequest.getContents().writeTo(outputStream);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }catch (Exception e){
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
                }finally {
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

}
