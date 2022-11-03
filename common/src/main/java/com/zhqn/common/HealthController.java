package com.zhqn.common;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/health")
public class HealthController {

    @Path("/")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "ok";
    }
}
