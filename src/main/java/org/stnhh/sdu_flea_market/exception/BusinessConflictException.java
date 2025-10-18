package org.stnhh.sdu_flea_market.exception;

/**
 * 业务冲突异常
 * 用于表示业务操作中的冲突情况，如重复操作、状态冲突等
 */
public class BusinessConflictException extends BusinessException {
    
    public BusinessConflictException(String message) {
        super(409, message);
    }
    
    public BusinessConflictException(String message, Throwable cause) {
        super(409, message, cause);
    }
}

