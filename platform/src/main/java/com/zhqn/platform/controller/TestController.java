package com.zhqn.platform.controller;

import com.zhqn.common.UserVO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/platform")
public class TestController {

    @Path("/test")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserVO test() {
        return new UserVO();
    }
}
