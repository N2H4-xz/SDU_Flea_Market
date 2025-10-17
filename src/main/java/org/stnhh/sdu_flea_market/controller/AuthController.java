package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.data.po.User;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.auth.ChangePasswordRequest;
import org.stnhh.sdu_flea_market.data.vo.auth.LoginRequest;
import org.stnhh.sdu_flea_market.data.vo.auth.LoginResponse;
import org.stnhh.sdu_flea_market.data.vo.auth.RegisterRequest;
import org.stnhh.sdu_flea_market.service.UserService;
import org.stnhh.sdu_flea_market.utils.ResponseUtil;
import org.stnhh.sdu_flea_market.utils.TokenUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Result> register(@RequestBody RegisterRequest request) {
        try {
            // 验证两次输入的密码是否一致
            if (!request.getPassword().equals(request.getConfirm_password())) {
                return Result.error(400, "两次密码不一致");
            }

            // 调用服务进行用户注册
            User user = userService.register(request.getUsername(), request.getEmail(), request.getPassword());

            return Result.success(user, "注册成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Result> login(@RequestBody LoginRequest request) {
        try {
            // 调用服务进行用户登录
            LoginResponse response = userService.login(request.getEmail(), request.getPassword());
            return Result.success(response, "登录成功");
        } catch (Exception e) {
            return Result.error(401, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Result> logout(HttpServletRequest request) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(request);
            // 调用服务进行登出
            userService.logout(token);
            return Result.ok();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Result> changePassword(@RequestBody ChangePasswordRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            // 从令牌中提取用户ID
            String userId = extractUserIdFromToken(token);

            // 验证两次输入的新密码是否一致
            if (!request.getNew_password().equals(request.getConfirm_password())) {
                return Result.error(400, "两次新密码不一致");
            }

            // 调用服务修改密码
            userService.changePassword(userId, request.getOld_password(), request.getNew_password());
            return Result.ok();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    private String extractUserIdFromToken(String token) {
        // 使用TokenUtil中的方法提取用户ID
        return TokenUtil.extractUserIdFromToken(token);
    }
}

