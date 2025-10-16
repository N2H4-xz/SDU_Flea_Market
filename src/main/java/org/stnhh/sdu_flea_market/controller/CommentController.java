package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.data.po.Comment;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.comment.CommentRequest;
import org.stnhh.sdu_flea_market.data.vo.comment.CommentResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.service.CommentService;
import org.stnhh.sdu_flea_market.utils.ResponseUtil;
import org.stnhh.sdu_flea_market.utils.TokenUtil;

@RestController
@RequestMapping("/products/{productId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Result> createComment(@PathVariable String productId, @RequestBody CommentRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String authorId = extractUserIdFromToken(token);

            // 创建评论
            Comment comment = commentService.createComment(productId, authorId, request);
            return ResponseUtil.build(Result.success(comment, "评论发布成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Result> listComments(
            @PathVariable String productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "newest") String sort) {
        try {
            // 获取商品评论列表
            PageResponse<CommentResponse> response = commentService.listComments(productId, page, limit, sort);
            return ResponseUtil.build(Result.success(response, "获取成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Result> deleteComment(@PathVariable String productId, @PathVariable String commentId, HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String userId = extractUserIdFromToken(token);

            // 删除评论
            commentService.deleteComment(commentId, userId);
            return ResponseUtil.build(Result.ok());
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    private String extractUserIdFromToken(String token) {
        // TODO: 需要实现JWT令牌解析，从令牌中提取用户ID
        return "user_id_from_token";
    }
}

