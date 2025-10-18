package org.stnhh.sdu_flea_market.exception;

/**
 * 参数无效异常
 * 用于表示请求参数无效或不符合要求
 */
public class InvalidParameterException extends BusinessException {
    
    public InvalidParameterException(String message) {
        super(400, message);
    }
    
    public InvalidParameterException(String message, Throwable cause) {
        super(400, message, cause);
    }
}

