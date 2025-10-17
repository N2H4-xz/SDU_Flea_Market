package org.stnhh.sdu_flea_market.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.stnhh.sdu_flea_market.cache.IGlobalCache;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


@Component
public class JWTUtil {
    @Autowired
    private IGlobalCache redis;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-secret-key}")
    private String refreshSecretKey;

    public static String SECRET_KEY;
    public static String REFRESH_SECRET_KEY;

    // 初始化静态变量
    @PostConstruct
    public void init() {
        SECRET_KEY = secretKey;
        REFRESH_SECRET_KEY = refreshSecretKey;
    }

    public static final int EXPIRE_TIME = 1800;//Token过期时间
    public static final int REFRESH_EXPIRE_TIME = 2 * 60 *60;//RefreshToken过期时间

    //生成token - 支持 Long 类型的 userId
    public String getToken(Long accountId, int expireTime, String key) {
        try {
            //获取时间，设置token过期时间
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.SECOND, expireTime);

            //JWT添加payload
            JWTCreator.Builder builder = JWT.create();
            builder.withClaim("user_id", accountId);

            //JWT过期时间 + signature
            String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(key));
            redis.set(token, "1", expireTime);
            return token;
        } catch (Exception e) {
            throw new RuntimeException("生成Token失败");
        }
    }


    //返回token内容
    public static DecodedJWT getTokenInfo(String token, String key) throws UnsupportedEncodingException {
        return JWT.require(Algorithm.HMAC256(key)).build().verify(token);
    }

    // 获取用户 ID - 返回 Long 类型
    public Long getUserId(String token, String key) {
        try {
            if (redis.get(token) == null) {
                throw new IllegalArgumentException();
            }
            // 验证并解析 Token
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(key))
                    .build()
                    .verify(token);

            // 提取用户 ID
            return decodedJWT.getClaim("user_id").asLong();

        } catch (TokenExpiredException e) {
            // Token 过期
            throw new RuntimeException("Token 已过期，请重新登录");

        } catch (JWTVerificationException e) {
            // Token 验证失败（签名无效等）
            throw new RuntimeException("Token 无效，请检查后重试");

        }catch (IllegalArgumentException e) {
            // Token 无效
            throw new RuntimeException("Token 无效，请检查后重试");

        } catch (Exception e) {
            // 其他异常
            throw new RuntimeException("解析 Token 时发生错误，请稍后重试");
        }
    }
}
