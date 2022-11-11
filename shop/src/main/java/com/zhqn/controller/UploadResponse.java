package com.zhqn.controller;

import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@Schema(description = "上传文件结果")
public class UploadResponse {
    @Schema(description = "错误信息")
    private String error;
    @Schema(description = "上传是否成功")
    private boolean success;
    @Schema(description = "文件编号")
    private String fileNo;
}
