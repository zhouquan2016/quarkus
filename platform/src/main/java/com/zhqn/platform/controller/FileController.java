package com.zhqn.platform.controller;

import com.zhqn.platform.service.FileService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/file")
@Produces(MediaType.APPLICATION_JSON)
public class FileController {

    @Inject
    FileService fileService;

    @Inject
    List<String> names;

    @Path("/upload")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String upload(UploadQuery query) {
        return fileService.upload(query);
    }

    @Path("/download")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    public Uni<Response> download(@QueryParam("fileNo") String fileNo) {
        return Uni.createFrom().item(fileService.download(fileNo));
    }

    @Path("/getName")
    @GET
    public List<String> getName() {
        return names;
    }
}
