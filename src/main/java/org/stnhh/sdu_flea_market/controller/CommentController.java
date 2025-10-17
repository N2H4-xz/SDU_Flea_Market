package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import org.stnhh.sdu_flea_market.utils.ResponseUtil;

@RestController
@RequestMapping("/products/{productId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Auth
    @PostMapping
    public ResponseEntity<Result> createComment(@PathVariable String productId, @RequestBody CommentRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String authorId = (String) httpRequest.getAttribute("userId");

            // 创建留言
            Comment comment = commentService.createComment(productId, authorId, request);
            return Result.success(comment, "留言发布成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
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
            return Result.success(response, "获取成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Auth
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Result> deleteComment(@PathVariable String productId, @PathVariable String commentId, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String userId = (String) httpRequest.getAttribute("userId");

            // 删除留言
            commentService.deleteComment(commentId, userId);
            return Result.ok();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}

