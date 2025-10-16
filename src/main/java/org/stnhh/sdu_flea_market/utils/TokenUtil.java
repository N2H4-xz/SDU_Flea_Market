package org.stnhh.sdu_flea_market.utils;

import jakarta.servlet.http.HttpServletRequest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenUtil {

    // JWT密钥（应该从配置文件读取）
    private static final String SECRET_KEY = "STPlayTableSecretKey";

    /**
     * 从请求头中提取JWT令牌
     */
    public static String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        // 校验是否提供 Authorization 头
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            throw new IllegalArgumentException("token未提供");
        }

        // 校验格式并提取 token
        if (authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // 去掉 "Bearer " 前缀
        } else {
            throw new IllegalArgumentException("token格式错误");
        }
    }

    /**
     * 从JWT令牌中提取用户ID
     */
    public static String extractUserIdFromToken(String token) {
        try {
            // 验证并解析 Token
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);

            // 提取用户 ID
            return decodedJWT.getClaim("user_id").asString();

        } catch (TokenExpiredException e) {
            // Token 过期
            throw new RuntimeException("Token 已过期，请重新登录");

        } catch (JWTVerificationException e) {
            // Token 验证失败（签名无效等）
            throw new RuntimeException("Token 无效，请检查后重试");

        } catch (Exception e) {
            // 其他异常
            throw new RuntimeException("解析 Token 时发生错误，请稍后重试");
        }
    }
}
