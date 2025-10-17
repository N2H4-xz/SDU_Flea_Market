package org.stnhh.sdu_flea_market.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.annotation.Auth;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.message.MessageResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.service.MessageService;
import org.stnhh.sdu_flea_market.utils.AuthContextUtil;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Auth
    @GetMapping("/{userId}")
    public ResponseEntity<Result> getMessageHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer limit) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long currentUserId = AuthContextUtil.getUserId();

        // 获取消息历史记录
        PageResponse<MessageResponse> response = messageService.getMessageHistory(currentUserId, userId, page, limit);
        return Result.success(response, "获取成功");
    }
}

