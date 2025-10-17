package org.stnhh.sdu_flea_market.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.annotation.Auth;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.user.UpdateProfileRequest;
import org.stnhh.sdu_flea_market.data.vo.user.UserProfileResponse;
import org.stnhh.sdu_flea_market.service.UserService;
import org.stnhh.sdu_flea_market.utils.AuthContextUtil;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Auth
    @GetMapping("/profile")
    public ResponseEntity<Result> getProfile() {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();

        // 获取用户个人资料
        UserProfileResponse profile = userService.getProfile(userId);
        return Result.success(profile, "获取成功");
    }

    @Auth
    @PutMapping("/profile")
    public ResponseEntity<Result> updateProfile(@RequestBody UpdateProfileRequest request) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();

        // 更新用户个人资料
        UserProfileResponse profile = userService.updateProfile(userId, request);
        return Result.success(profile, "更新成功");
    }
}

