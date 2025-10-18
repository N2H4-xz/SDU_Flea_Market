package org.stnhh.sdu_flea_market.exception;

/**
 * 无权限异常
 * 用于表示用户没有权限执行某个操作
 */
public class UnauthorizedException extends BusinessException {
    
    public UnauthorizedException(String message) {
        super(403, message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(403, message, cause);
    }
}

