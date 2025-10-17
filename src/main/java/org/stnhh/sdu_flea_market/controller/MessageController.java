package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.message.MessageResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.service.MessageService;
import org.stnhh.sdu_flea_market.utils.ResponseUtil;
import org.stnhh.sdu_flea_market.utils.TokenUtil;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/{userId}")
    public ResponseEntity<Result> getMessageHistory(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer limit,
            HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String currentUserId = extractUserIdFromToken(token);

            // 获取消息历史记录
            PageResponse<MessageResponse> response = messageService.getMessageHistory(currentUserId, userId, page, limit);
            return Result.success(response, "获取成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    private String extractUserIdFromToken(String token) {
        // 使用TokenUtil中的方法提取用户ID
        return TokenUtil.extractUserIdFromToken(token);
    }
}

