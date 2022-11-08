package com.zhqn.platform.controller;


import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

@Data
public class TestQuery {

    @Schema(required = true, description = "主键")
    private Long id;

}
