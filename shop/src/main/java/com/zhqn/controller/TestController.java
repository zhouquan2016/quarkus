package com.zhqn.controller;

import com.zhqn.common.grpc.platform.FileService;
import io.netty.util.internal.StringUtil;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.common.util.StreamUtil;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.jboss.resteasy.reactive.server.core.StreamingUtil;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UploadResponse> upload( UploadQuery query) {
        System.out.println("-------------测试-------------");
        String filename = query.fileName;

        if (StringUtil.isNullOrEmpty(filename)) {
            UploadResponse response = new UploadResponse();
            response.setSuccess(false);
            response.setError("文件名为空");
            return Uni.createFrom().item(response);
        }
        return fileService.uploadFile(query.getFileName(), query.getFile()).map(item -> {
            UploadResponse response = new UploadResponse();
            response.setFileNo(item.getData());
            response.setSuccess(item.isSuccess());
            response.setError(item.getError());
            return response;
        });
    }

    @Path("/download")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@QueryParam("fileNo") String fileNo) {
        File out = fileService.downloadFile(fileNo);

        StreamingOutput streamingOutput = outputStream -> {
            try(FileInputStream inputStream = new FileInputStream(out)) {
                byte[] buff = new byte[4098];
                int size;
                while ((size = inputStream.read(buff)) != -1) {
                    outputStream.write(buff, 0, size);
                }
            }finally {
                out.delete();
                outputStream.close();
            }
        };
        return Response.ok(streamingOutput)
                .header( "Content-Disposition", "attachment; filename=\"" + out .getName() + "\"" )
                .header("Content-Length", out.length())
                .build();
    }

    @Path("/download2")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download2(@QueryParam("fileNo") String fileNo) {
        return fileService.downloadFile2(fileNo);
    }

}
