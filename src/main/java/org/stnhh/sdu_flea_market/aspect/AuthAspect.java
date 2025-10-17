package org.stnhh.sdu_flea_market.aspect;

import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.utils.JWTUtil;
import org.stnhh.sdu_flea_market.utils.ResponseUtil;
import org.stnhh.sdu_flea_market.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
@Aspect
public class AuthAspect {
    @Autowired
    private JWTUtil jwtUtil;

    @Around("@annotation(org.stnhh.sdu_flea_market.annotation.Auth)") // 拦截带有 @Auth 注解的方法
    public Object verifyToken(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 获取 HTTP 请求中的 Token
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String token = TokenUtil.extractToken(request);

            // 校验 Token 的有效性

            String userId = jwtUtil.getUserId(token, JWTUtil.SECRET_KEY);
            request.setAttribute("userId", userId); // 将 userId 添加到请求上下文
        } catch (Exception e) {
            return Result.error(403, String.valueOf(e.getMessage()));
        }

        // 继续执行目标方法
        return joinPoint.proceed();
    }
}
