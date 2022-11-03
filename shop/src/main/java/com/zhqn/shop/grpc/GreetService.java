package com.zhqn.shop.grpc;

import com.zhqn.common.shop.Greeter;
import com.zhqn.common.shop.HelloReply;
import com.zhqn.common.shop.HelloRequest;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

@GrpcService
@Slf4j
public class GreetService implements Greeter {

    @Override
    public Uni<HelloReply> sayHello(HelloRequest request) {
        log.info("hello request:{}", request.toString());
        return Uni.createFrom().item(HelloReply.newBuilder().setMessage("hello").setResultCode(100).build());
    }
}
