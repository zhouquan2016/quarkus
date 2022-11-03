package com.zhqn;

import com.zhqn.common.UserVO;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Path("/hello1")
@Slf4j
public class ExampleResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/x")
    public UserVO hello() {
        log.info("xxx");
        UserVO userVO = new UserVO();
        userVO.setName("xx");
        userVO.setDate(new Date());
        userVO.setLocalDate(LocalDate.now());
        userVO.setLocalDateTime(LocalDateTime.now());
        return userVO;
    }
}