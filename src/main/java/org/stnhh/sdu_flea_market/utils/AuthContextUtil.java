package org.stnhh.sdu_flea_market.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 认证上下文工具类
 * 用于从当前请求上下文中获取认证信息（如 userId）
 */
public class AuthContextUtil {

    /**
     * 从当前请求上下文中获取 userId
     * 该 userId 由 AuthAspect 在请求处理前设置
     *
     * @return userId Long 类型
     * @throws RuntimeException 如果无法获取 userId
     */
    public static Long getUserId() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                    RequestContextHolder.getRequestAttributes())).getRequest();
            Long userId = (Long) request.getAttribute("userId");

            if (userId == null) {
                throw new RuntimeException("userId not found in request context");
            }

            return userId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get userId from request context: " + e.getMessage());
        }
    }

    /**
     * 从当前请求上下文中获取 HttpServletRequest
     *
     * @return HttpServletRequest 对象
     * @throws RuntimeException 如果无法获取 request
     */
    public static HttpServletRequest getRequest() {
        try {
            return ((ServletRequestAttributes) Objects.requireNonNull(
                    RequestContextHolder.getRequestAttributes())).getRequest();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get request from context: " + e.getMessage());
        }
    }
}

