package com.zhqn.common.grpc.platform;

import io.smallrye.mutiny.Uni;
import lombok.Data;

@Data
public class BaseResponse<T> {
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 数据
     */
    private T data;

    public static  BaseResponse fail(String error) {
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }

    public static <T> Uni<BaseResponse<T>> failUni(String error) {
        return Uni.createFrom().item(BaseResponse.fail(error));
    }

    public static <T> BaseResponse success(T data) {
        BaseResponse<T> response = new BaseResponse();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> Uni<BaseResponse<T>> successUni(T data) {
        return Uni.createFrom().item(BaseResponse.success(data));
    }
}
