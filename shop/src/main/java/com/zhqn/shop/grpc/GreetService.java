package com.zhqn.shop.grpc;

import com.zhqn.common.shop.*;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@GrpcService
@Slf4j
public class GreetService extends GreeterGrpc.GreeterImplBase {

//    @Override
    public Uni<HelloReply> sayHello(HelloRequest request) {
        log.info("request:{}", request.toString());
        return Uni.createFrom().item(HelloReply.newBuilder().setMessage("hello").setResultCode(100).build());
    }

    private UploadResponse transferError(Throwable e) {
        return UploadResponse.newBuilder().setStatus(-1).setError(e.getMessage()).build();
    }
//    @Override
    public Uni<UploadResponse> upload(Multi<UploadRequest> request) {
        File dir = new File("d:/quarkus/upload");
        if (dir.exists()) {
            if (!dir.mkdir()) {
                return Uni.createFrom().item(UploadResponse.newBuilder().setStatus(-1).setError("upload dir not exists").build());
            }
        }
        AtomicReference<FileOutputStream> osRef = new AtomicReference<>();
        AtomicReference<Exception> errorRef = new AtomicReference<>();
        request.onItem().invoke(uploadRequest -> {

            try {
                osRef.compareAndSet(null, new FileOutputStream(new File(dir, uploadRequest.getFilename())));
                FileOutputStream os = osRef.get();
                uploadRequest.getContent().writeTo(os);

            } catch (Exception e) {
                errorRef.set(e);
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }).onCompletion().invoke(() -> {
            FileOutputStream os = osRef.get();
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).toUni();
        return null;
    }

    @Override
    public StreamObserver<UploadRequest> upload(StreamObserver<UploadResponse> responseObserver) {
        return new StreamObserver<UploadRequest>() {

            private File dir = new File("d:/testupload");

            private File file;

            private FileOutputStream os;

            private void close() {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onNext(UploadRequest uploadRequest) {
                try {
                    if (file == null) {
                        file = new File(dir, uploadRequest.getFilename());
                        os = new FileOutputStream(file);
                    }

                    uploadRequest.getContent().writeTo(os);
                } catch (Exception e) {
                    e.printStackTrace();
                    onError(e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                close();
                responseObserver.onNext(UploadResponse.newBuilder().setStatus(-1).setError(throwable.getMessage()).build());
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                close();
                responseObserver.onNext(UploadResponse.newBuilder().setStatus(1).setFileNo(file.getAbsolutePath()).build());
                responseObserver.onCompleted();
            }
        };
    }
}
