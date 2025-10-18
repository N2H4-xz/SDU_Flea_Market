package org.stnhh.sdu_flea_market.exception;

/**
 * 用户名已存在异常
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
    
    public UsernameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

