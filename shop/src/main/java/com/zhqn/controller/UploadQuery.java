package com.zhqn.controller;

import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

@Data
public class UploadQuery {
    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @Schema(description = "文件数据", required = true)
    public InputStream file;

    @FormParam("fileName")
    @Schema(description = "文件名", required = true)
    @PartType(MediaType.TEXT_PLAIN)
    public String fileName;
}
