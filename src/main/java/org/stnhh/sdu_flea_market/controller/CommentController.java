package org.stnhh.sdu_flea_market.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.annotation.Auth;
import org.stnhh.sdu_flea_market.data.po.Comment;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.comment.CommentRequest;
import org.stnhh.sdu_flea_market.data.vo.comment.CommentResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.service.CommentService;
import org.stnhh.sdu_flea_market.utils.AuthContextUtil;

@RestController
@RequestMapping("/products/{productId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Auth
    @PostMapping
    public ResponseEntity<Result> createComment(@PathVariable Long productId, @RequestBody CommentRequest request) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long authorId = AuthContextUtil.getUserId();

        // 创建留言
        Comment comment = commentService.createComment(productId, authorId, request);
        return Result.success(comment, "留言发布成功");
    }

    @GetMapping
    public ResponseEntity<Result> listComments(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "newest") String sort) {
        // 获取商品评论列表
        PageResponse<CommentResponse> response = commentService.listComments(productId, page, limit, sort);
        return Result.success(response, "获取成功");
    }

    @Auth
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Result> deleteComment(@PathVariable Long productId, @PathVariable Long commentId) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();

        // 删除留言
        commentService.deleteComment(commentId, userId);
        return Result.ok();
    }
}

