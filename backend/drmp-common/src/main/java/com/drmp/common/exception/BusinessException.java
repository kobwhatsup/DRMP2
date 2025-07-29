package com.drmp.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 
 * @author DRMP Team
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 错误消息
     */
    private final String message;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
    
    /**
     * 参数校验异常
     */
    public static BusinessException validateError(String message) {
        return new BusinessException(400, message);
    }
    
    /**
     * 未授权异常
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message != null ? message : "未授权访问");
    }
    
    /**
     * 禁止访问异常
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(403, message != null ? message : "禁止访问");
    }
    
    /**
     * 资源不存在异常
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(404, message != null ? message : "资源不存在");
    }
    
    /**
     * 业务逻辑异常
     */
    public static BusinessException business(String message) {
        return new BusinessException(500, message);
    }
}