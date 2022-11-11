package com.zhqn.common.grpc.platform;

import com.zhqn.common.platform.UploadResponse;
import io.smallrye.mutiny.Uni;

import java.io.File;
import java.io.InputStream;

public interface FileService {

    /**
     * 异步的上传文件,<strong>需要客户端自己关闭文件流</strong>
     * @param fileName
     * @param is 文件流不会关闭
     * @return 异步结果
     */
    Uni<UploadResponse> uploadFile(String fileName, InputStream is);

    /**
     * 上传文件
     * @param file
     * @return
     */
    Uni<UploadResponse> uploadFile(File file);
}
