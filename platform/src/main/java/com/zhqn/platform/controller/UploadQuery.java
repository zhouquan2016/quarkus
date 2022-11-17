package com.zhqn.platform.controller;

import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

@Data
public class UploadQuery {

    @Schema(description = "文件")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @FormParam("file")
    private InputStream file;

    @Schema(description = "文件名称")
    @PartType(MediaType.TEXT_PLAIN)
    @FormParam("fileName")
    private String fileName;
}
