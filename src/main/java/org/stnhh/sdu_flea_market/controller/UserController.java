package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.user.UpdateProfileRequest;
import org.stnhh.sdu_flea_market.data.vo.user.UserProfileResponse;
import org.stnhh.sdu_flea_market.service.UserService;
import org.stnhh.sdu_flea_market.utils.ResponseUtil;
import org.stnhh.sdu_flea_market.utils.TokenUtil;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<Result> getProfile(HttpServletRequest request) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(request);
            String userId = extractUserIdFromToken(token);

            // 获取用户个人资料
            UserProfileResponse profile = userService.getProfile(userId);
            return ResponseUtil.build(Result.success(profile, "获取成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Result> updateProfile(@RequestBody UpdateProfileRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String userId = extractUserIdFromToken(token);

            // 更新用户个人资料
            UserProfileResponse profile = userService.updateProfile(userId, request);
            return ResponseUtil.build(Result.success(profile, "更新成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    private String extractUserIdFromToken(String token) {
        // TODO: 需要实现JWT令牌解析，从令牌中提取用户ID
        return "user_id_from_token";
    }
}

