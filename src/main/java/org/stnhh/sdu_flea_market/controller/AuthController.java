package org.stnhh.sdu_flea_market.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.annotation.Auth;
import org.stnhh.sdu_flea_market.data.po.User;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.auth.ChangePasswordRequest;
import org.stnhh.sdu_flea_market.data.vo.auth.LoginRequest;
import org.stnhh.sdu_flea_market.data.vo.auth.LoginResponse;
import org.stnhh.sdu_flea_market.data.vo.auth.RegisterRequest;
import org.stnhh.sdu_flea_market.service.UserService;
import org.stnhh.sdu_flea_market.utils.AuthContextUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Result> register(@RequestBody RegisterRequest request) {
        // 验证两次输入的密码是否一致
        if (!request.getPassword().equals(request.getConfirm_password())) {
            return Result.error(400, "两次密码不一致");
        }

        // 调用服务进行用户注册，异常由 GlobalExceptionHandler 处理
        User user = userService.register(request.getUsername(), request.getPassword());
        return Result.success(user, "注册成功");
    }

    @PostMapping("/login")
    public ResponseEntity<Result> login(@RequestBody LoginRequest request) {
        // 调用服务进行用户登录（支持用户名或邮箱登录）
        LoginResponse response = userService.login(request.getUsername(),  request.getPassword());
        return Result.success(response, "登录成功");
    }

    @Auth
    @PostMapping("/logout")
    public ResponseEntity<Result> logout() {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();
        // 调用服务进行登出
        userService.logout(userId);
        return Result.ok();
    }

    @Auth
    @PostMapping("/change-password")
    public ResponseEntity<Result> changePassword(@RequestBody ChangePasswordRequest request) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();

        // 验证两次输入的新密码是否一致
        if (!request.getNew_password().equals(request.getConfirm_password())) {
            return Result.error(400, "两次新密码不一致");
        }

        // 调用服务修改密码
        userService.changePassword(userId, request.getOld_password(), request.getNew_password());
        return Result.ok();
    }


}

