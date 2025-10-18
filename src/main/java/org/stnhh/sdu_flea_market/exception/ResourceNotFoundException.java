package org.stnhh.sdu_flea_market.exception;

/**
 * 资源不存在异常
 * 用于表示请求的资源不存在
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(404, message, cause);
    }
}

