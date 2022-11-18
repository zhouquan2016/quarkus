package com.zhqn.platform.controller;

import com.zhqn.platform.domain.PersonEntity;
import com.zhqn.platform.mapper.PersonMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/person")
@Produces(MediaType.APPLICATION_JSON)
public class PersonController {

    @Inject
    PersonMapper personMapper;

    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Long save(PersonEntity person) {
        int ret = personMapper.insert(person);
        if (ret <= 0) {
            throw new RuntimeException("新增失败");
        }
        return person.getId();
    }
}
