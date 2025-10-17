package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.data.po.Favorite;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.favorite.FavoriteRequest;
import org.stnhh.sdu_flea_market.data.vo.favorite.FavoriteResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.service.FavoriteService;
import org.stnhh.sdu_flea_market.utils.ResponseUtil;
import org.stnhh.sdu_flea_market.utils.TokenUtil;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<Result> addFavorite(@RequestBody FavoriteRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String userId = extractUserIdFromToken(token);

            // 添加收藏
            Favorite favorite = favoriteService.addFavorite(userId, request.getProduct_id());
            return Result.success(favorite, "收藏成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Result> removeFavorite(@PathVariable String productId, HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String userId = extractUserIdFromToken(token);

            // 删除收藏
            favoriteService.removeFavorite(userId, productId);
            return Result.ok();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Result> listFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String userId = extractUserIdFromToken(token);

            // 获取用户的收藏列表
            PageResponse<FavoriteResponse> response = favoriteService.listFavorites(userId, page, limit);
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

