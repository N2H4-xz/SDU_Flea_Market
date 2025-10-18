package org.stnhh.sdu_flea_market.config;

import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.exception.*;
import org.stnhh.sdu_flea_market.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 捕获业务异常基类
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result> handleBusinessException(BusinessException ex) {
        log.warn("业务异常: code={}, message={}", ex.getCode(), ex.getMessage());
        return Result.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 捕获资源不存在异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Result> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("资源不存在: {}", ex.getMessage());
        return Result.error(404, ex.getMessage());
    }

    /**
     * 捕获无权限异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Result> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("无权限操作: {}", ex.getMessage());
        return Result.error(403, ex.getMessage());
    }

    /**
     * 捕获余额不足异常
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Result> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        log.warn("余额不足: {}", ex.getMessage());
        return Result.error(400, ex.getMessage());
    }

    /**
     * 捕获参数无效异常
     */
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<Result> handleInvalidParameterException(InvalidParameterException ex) {
        log.warn("参数无效: {}", ex.getMessage());
        return Result.error(400, ex.getMessage());
    }

    /**
     * 捕获业务冲突异常
     */
    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<Result> handleBusinessConflictException(BusinessConflictException ex) {
        log.warn("业务冲突: {}", ex.getMessage());
        return Result.error(409, ex.getMessage());
    }

    /**
     * 捕获用户名已存在异常
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Result> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        log.warn("用户注册失败: 用户名已存在, error={}", ex.getMessage());
        return Result.error(400, "用户名存在");
    }

    /**
     * 捕获 Redis 连接失败异常
     */
    @ExceptionHandler(org.springframework.data.redis.RedisConnectionFailureException.class)
    public ResponseEntity<Result> handleRedisException(Exception ex) {
        log.error("Redis 连接异常: {}", ex.getMessage(), ex);
        return Result.error(500, "Redis 服务不可用，请稍后再试");
    }

    // 捕获 NullPointerException
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Result> handleNullPointerException(NullPointerException ex) {
        log.error("空指针异常: {}", ex.getMessage(), ex);
        return Result.error(500, "系统内部错误，请稍后再试");
    }

    // 捕获 IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("参数非法: {}", ex.getMessage(), ex);
        return Result.error(400, "非法参数: " + ex.getMessage());
    }


    // 捕获外键约束失败异常
    @ExceptionHandler(java.sql.SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Result> handleSQLIntegrityConstraintViolationException(java.sql.SQLIntegrityConstraintViolationException ex) {
        log.error("外键约束失败: {}", ex.getMessage(), ex);
        return Result.error(400, "操作失败，可能由于关联数据不存在或已被删除");
    }

    // 捕获主键冲突
    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public ResponseEntity<Result> handleDuplicateKeyException(org.springframework.dao.DuplicateKeyException ex) {
        log.error("主键冲突: {}", ex.getMessage(), ex);
        return Result.error(400, "数据冲突，相关记录已存在");
    }

    // 捕获数据类型不匹配
    @ExceptionHandler(java.sql.SQLDataException.class)
    public ResponseEntity<Result> handleSQLDataException(java.sql.SQLDataException ex) {
        log.error("数据类型不匹配: {}", ex.getMessage(), ex);
        return Result.error(400, "数据类型错误，请检查输入");
    }

    // 捕获结果集为空
    @ExceptionHandler(org.springframework.dao.EmptyResultDataAccessException.class)
    public ResponseEntity<Result> handleEmptyResultDataAccessException(org.springframework.dao.EmptyResultDataAccessException ex) {
        log.error("结果集为空: {}", ex.getMessage(), ex);
        return Result.error(404, "未找到相关数据");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> handleValidationException(MethodArgumentNotValidException ex) {
        // 获取第一个字段错误信息
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = (fieldError != null) ? fieldError.getDefaultMessage() : "参数校验失败";

        log.error("参数校验异常: {}", message, ex);

        return Result.error(400, message);
    }

    // 捕获通用数据访问异常
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<Result> handleDataAccessException(org.springframework.dao.DataAccessException ex) {
        log.error("数据访问异常: {}", ex.getMessage(), ex);

        // 检查是否包含特定的根本原因
        Throwable cause = ex.getCause();
        if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
            log.error("外键约束失败: {}", cause.getMessage(), cause);
            return Result.error(400, "操作失败，可能由于关联数据不存在或已被删除");
        }

        if (cause instanceof java.sql.SQLDataException) {
            log.error("数据类型错误: {}", cause.getMessage(), cause);
            return Result.error(400, "数据类型错误，请检查输入");
        }

        // 如果不是特定异常，返回通用数据库错误
        return Result.error(500, "数据库操作失败，请稍后再试");
    }

    /**
     * 捕获参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("参数类型不匹配: 参数名={}, 期望类型={}, 异常信息={}",
                ex.getName(), ex.getRequiredType(), ex.getMessage(), ex);
        return Result.error(400, "参数类型不匹配: " + ex.getName());
    }


    /**
     * 捕获所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleGenericException(Exception ex) {
        log.error("未知异常捕获: {}", ex.getMessage(), ex);
        return Result.error(500, "服务器内部错误，请稍后再试");
    }
}
