package com.zhqn.platform.controller;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.zhqn.common.UserVO;
import com.zhqn.common.shop.*;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.stubs.MultiStreamObserver;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBodySchema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;

@Path("/platform")
@Slf4j
public class TestController {

    @GrpcClient("shop")
    Greeter greeter;

    @GrpcClient("shop")
    MutinyGreeterGrpc.MutinyGreeterStub greeterStub;

    @Path("/test")
    @GET
    @APIResponse(name = "test", description = "返回结果")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "测试grpc")
    public UserVO test(@RequestBody(description = "请求参数") @RequestBodySchema(TestQuery.class) TestQuery query) {
        long a = 0;
        Uni<HelloReply> helloReplyUni = greeter.sayHello(HelloRequest.newBuilder().setUserType(UserType.U1).setType(0).build());
        HelloReply helloReply = helloReplyUni.await().atMost(Duration.ofSeconds(1));
        log.info("reply:{}", helloReply.toString().replaceAll("\\n", ","));
        return new UserVO();
    }

    @ServerExceptionMapper
    public Response exceptionHandler(Exception e) {
        log.error("全局异常", e);
        return Response.serverError().entity(e.getMessage()).build();
    }

    @Path("/upload")
    @GET
    public String upload() {
        File file = new File("D:\\av\\videos\\国产\\SA国际传媒TWA0008一觉醒来我变女生啦02.mp4");
        try(FileInputStream is = new FileInputStream(file)) {

            Multi<UploadRequest> requestMulti = Multi.createFrom().iterable(() -> new Iterator<UploadRequest>() {

                byte buff[] = new byte[4098];
                int size = 0;
                @Override
                public boolean hasNext() {
                    return size != -1;
                }

                @Override
                public UploadRequest next() {
                    try {
                        this.size = is.read(buff);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (this.size == -1) {
                        return UploadRequest.newBuilder()
                                .setContent(ByteString.EMPTY)
                                .setFilename(file.getName())
                                .build();
                    }
                    return UploadRequest.newBuilder()
                            .setContent(ByteString.copyFrom(buff, 0, size))
                            .setFilename(file.getName())
                            .build();
                }
            });
            UploadResponse response = greeterStub.upload(requestMulti).await().atMost(Duration.ofMinutes(1));
            for (Map.Entry<Descriptors.FieldDescriptor, Object> ff : response.getAllFields().entrySet()) {
                log.info("key:{}, value:{}", ff.getKey(), ff.getValue());
            }
            log.info("status:{},error:{},fileNo:{}", response.getStatus(), response.getError(), response.getFileNo());
            return response.toString();
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
