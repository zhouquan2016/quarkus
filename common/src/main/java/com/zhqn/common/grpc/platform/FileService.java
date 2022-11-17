package com.zhqn.common.grpc.platform;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;

public interface FileService {

    /**
     * 异步的上传文件,<strong>需要客户端自己关闭文件流</strong>
     * @param fileName
     * @param is 文件流不会关闭
     * @return 异步结果
     */
    Uni<BaseResponse<String>> uploadFile(String fileName, InputStream is);

    /**
     * 上传文件
     * @param file
     * @return
     */
    Uni<BaseResponse<String>> uploadFile(File file);

    /**
     * 异步地下载文件
     * @param fileNo 文件编码
     * @return 文件
     */
    File downloadFile(String fileNo) ;

    Response downloadFile2(String fileNo);
}
