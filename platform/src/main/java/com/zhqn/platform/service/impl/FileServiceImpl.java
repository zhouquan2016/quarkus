package com.zhqn.platform.service.impl;

import com.zhqn.platform.controller.UploadQuery;
import com.zhqn.platform.props.PlatformProperties;
import com.zhqn.platform.service.FileService;
import io.netty.util.internal.StringUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.UUID;

@ApplicationScoped
public class FileServiceImpl implements FileService {

    @Inject
    PlatformProperties platformProperties;

    @Override
    public String upload(UploadQuery query) {
        try {
            if (StringUtil.isNullOrEmpty(query.getFileName())) {
                throw new RuntimeException("文件名称不能为空");
            }
            if (query.getFileName().contains("/") || query.getFileName().contains("\\")) {
                throw new RuntimeException("文件名称不合法");
            }
            if (query.getFile() == null) {
                throw new RuntimeException("文件为空");
            }
            String fileNo = UUID.randomUUID().toString().replaceAll("-", "");
            File dir = new File(platformProperties.uploadPath(), fileNo);
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new RuntimeException("创建目录失败");
                }
            }
            File file = new File(dir, query.getFileName());
            try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
                byte[] buff = new byte[4098];
                int size;
                while ((size = query.getFile().read(buff)) != -1) {
                    os.write(buff, 0, size);
                }
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
            return fileNo;
        }finally {
            if (query.getFile() != null) {
                try {
                    query.getFile().close();
                } catch (IOException e) {
                }
            }
        }


    }

    @Override
    public Response download(String fileNo) {
        if (StringUtil.isNullOrEmpty(fileNo)) {
            throw new RuntimeException("文件编号为空");
        }
        File dir = new File(platformProperties.uploadPath(), fileNo);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new RuntimeException("文件不存在");
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            throw new RuntimeException("文件不存在");
        }
        File file = files[0];
        StreamingOutput streamingOutput = outputStream -> {
            try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buff = new byte[4098];
                int size;
                while ((size = inputStream.read(buff)) != -1) {
                    outputStream.write(buff, 0, size);
                }
            }finally {
                outputStream.close();
            }
        };
        return Response.ok(streamingOutput)
                .header( "Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" )
                .header("Content-Length", file.length())
                .build();
    }
}
