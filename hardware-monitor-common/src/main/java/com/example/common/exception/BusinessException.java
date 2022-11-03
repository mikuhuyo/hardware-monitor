package com.example.common.exception;

/**
 * 自定义业务异常
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public class BusinessException extends RuntimeException {
    private Integer errorCode;
    private final String message;

    public BusinessException(Integer errorCode, String message) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
