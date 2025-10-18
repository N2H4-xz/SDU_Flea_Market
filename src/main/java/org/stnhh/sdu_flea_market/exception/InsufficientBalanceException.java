package org.stnhh.sdu_flea_market.exception;

/**
 * 余额不足异常
 * 用于表示用户余额不足以完成操作
 */
public class InsufficientBalanceException extends BusinessException {
    
    public InsufficientBalanceException(String message) {
        super(400, message);
    }
    
    public InsufficientBalanceException(String message, Throwable cause) {
        super(400, message, cause);
    }
}

