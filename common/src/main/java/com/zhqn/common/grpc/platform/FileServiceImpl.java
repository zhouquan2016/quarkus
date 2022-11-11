package com.zhqn.common.grpc.platform;

import com.zhqn.common.platform.UploadResponse;
import io.netty.util.internal.StringUtil;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;

@ApplicationScoped
@Slf4j
public class FileServiceImpl implements FileService{

    @GrpcClient("platform")
    com.zhqn.common.platform.FileService fileService;

    private Uni<UploadResponse> uploadError(String error) {
        return Uni.createFrom().item(UploadResponse.newBuilder().setSuccess(false).setError(error).build());
    }
    @Override
    public Uni<UploadResponse> uploadFile(String fileName, InputStream is) {
        if (StringUtil.isNullOrEmpty(fileName)) {
            return uploadError("文件名为空");
        }
        if (is == null) {
            return uploadError("文件流为空");
        }
        try {
            return fileService.upload(Multi.createFrom().iterable(new FileUploadIterator(fileName, is)))
                    .onFailure().recoverWithUni(throwable -> {
                        log.error("grpc upload file:{},error", fileName, throwable);
                        return uploadError("上传失败");
                    })
                    .onTermination().invoke(() -> {
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    });
        }catch (Exception e) {
            e.printStackTrace();
            return uploadError("系统异常");
        }

    }


    @Override
    public Uni<UploadResponse> uploadFile(File file) {
        try (FileInputStream is = new FileInputStream(file)) {
            return uploadFile(file.getName(), is);
        } catch (FileNotFoundException e) {
            return uploadError("文件不存在");
        } catch (IOException e) {
            return uploadError("文件读取失败:" + e.getMessage());
        }
    }
}
