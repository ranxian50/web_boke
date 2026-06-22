package com.example.blog.common;

import lombok.Data;

/**
 * 统一 API 响应体
 * @param <T> 数据类型
 */
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 成功响应（带数据） */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /** 成功响应（无数据） */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /** 失败响应 */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /** 参数错误 400 */
    public static <T> Result<T> badRequest(String msg) {
        return new Result<>(400, msg, null);
    }

    /** 未认证 401 */
    public static <T> Result<T> unauthorized(String msg) {
        return new Result<>(401, msg, null);
    }

    /** 无权限 403 */
    public static <T> Result<T> forbidden(String msg) {
        return new Result<>(403, msg, null);
    }

    /** 未找到 404 */
    public static <T> Result<T> notFound(String msg) {
        return new Result<>(404, msg, null);
    }
}
