package com.zhqn.controller;

import com.zhqn.common.grpc.platform.FileService;
import io.netty.util.internal.StringUtil;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/platform")
@Slf4j
public class TestController {

    @Inject
    FileService fileService;

    @ServerExceptionMapper
    public Response exceptionHandler(Exception e) {
        log.error("全局异常", e);
        return Response.serverError().entity(e.getMessage()).build();
    }

    @Path("/upload")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<UploadResponse> upload( UploadQuery query) {
        String filename = query.fileName;

        if (StringUtil.isNullOrEmpty(filename)) {
            UploadResponse response = new UploadResponse();
            response.setSuccess(false);
            response.setError("文件名为空");
            return Uni.createFrom().item(response);
        }
        return fileService.uploadFile(query.getFileName(), query.getFile()).map(item -> {
            UploadResponse response = new UploadResponse();
            response.setFileNo(item.getFileNo());
            response.setSuccess(item.getSuccess());
            response.setError(item.getError());
            return response;
        });
    }
}
