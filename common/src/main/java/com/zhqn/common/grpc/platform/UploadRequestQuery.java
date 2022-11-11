package com.zhqn.common.grpc.platform;

import lombok.Data;

import java.io.InputStream;

@Data
public class UploadRequestQuery {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 输入流
     */
    private InputStream is;
}
