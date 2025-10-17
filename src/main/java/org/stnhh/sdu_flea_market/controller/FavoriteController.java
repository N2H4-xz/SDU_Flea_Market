package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.annotation.Auth;
import org.stnhh.sdu_flea_market.data.po.Favorite;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.favorite.FavoriteRequest;
import org.stnhh.sdu_flea_market.data.vo.favorite.FavoriteResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.service.FavoriteService;
import org.stnhh.sdu_flea_market.utils.ResponseUtil;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Auth
    @PostMapping
    public ResponseEntity<Result> addFavorite(@RequestBody FavoriteRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String userId = (String) httpRequest.getAttribute("userId");

            // 添加收藏
            Favorite favorite = favoriteService.addFavorite(userId, request.getProduct_id());
            return ResponseUtil.build(Result.success(favorite, "收藏成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    @Auth
    @DeleteMapping("/{productId}")
    public ResponseEntity<Result> removeFavorite(@PathVariable String productId, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String userId = (String) httpRequest.getAttribute("userId");

            // 删除收藏
            favoriteService.removeFavorite(userId, productId);
            return ResponseUtil.build(Result.ok());
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    @Auth
    @GetMapping
    public ResponseEntity<Result> listFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String userId = (String) httpRequest.getAttribute("userId");

            // 获取用户的收藏列表
            PageResponse<FavoriteResponse> response = favoriteService.listFavorites(userId, page, limit);
            return ResponseUtil.build(Result.success(response, "获取成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }
}

