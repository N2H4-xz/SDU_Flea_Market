package org.stnhh.sdu_flea_market.controller;

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
import org.stnhh.sdu_flea_market.utils.AuthContextUtil;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Auth
    @PostMapping
    public ResponseEntity<Result> addFavorite(@RequestBody FavoriteRequest request) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();

        // 添加收藏
        Favorite favorite = favoriteService.addFavorite(userId, request.getProduct_id());
        return Result.success(favorite, "收藏成功");
    }

    @Auth
    @DeleteMapping("/{productId}")
    public ResponseEntity<Result> removeFavorite(@PathVariable Long productId) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();

        // 删除收藏
        favoriteService.removeFavorite(userId, productId);
        return Result.ok();
    }

    @Auth
    @GetMapping
    public ResponseEntity<Result> listFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();

        // 获取用户的收藏列表
        PageResponse<FavoriteResponse> response = favoriteService.listFavorites(userId, page, limit);
        return Result.success(response, "获取成功");
    }
}

