package com.zhqn.platform.service;

import com.zhqn.platform.controller.UploadQuery;

import javax.ws.rs.core.Response;

public interface FileService {

    String upload(UploadQuery query);

    Response download(String fileNo);
}
