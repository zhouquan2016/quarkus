package com.zhqn.platform.controller;

import com.zhqn.common.UserVO;
import com.zhqn.common.shop.Greeter;
import com.zhqn.common.shop.HelloReply;
import com.zhqn.common.shop.HelloRequest;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("/platform")
@Slf4j
public class TestController {

    @GrpcClient("shop")
    Greeter greeter;

    @Path("/test")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserVO test() {
        Uni<HelloReply> helloReplyUni = greeter.sayHello(HelloRequest.newBuilder().build());
        HelloReply helloReply = helloReplyUni.await().atMost(Duration.ofMinutes(1));
        log.info(helloReply.toString());
        return new UserVO();
    }
}
