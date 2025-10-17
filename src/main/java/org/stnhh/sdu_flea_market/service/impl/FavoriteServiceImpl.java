package org.stnhh.sdu_flea_market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stnhh.sdu_flea_market.data.po.Favorite;
import org.stnhh.sdu_flea_market.data.po.Product;
import org.stnhh.sdu_flea_market.data.vo.favorite.FavoriteResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.mapper.FavoriteMapper;
import org.stnhh.sdu_flea_market.mapper.ProductMapper;
import org.stnhh.sdu_flea_market.service.FavoriteService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Favorite addFavorite(Long userId, Long productId) {
        // 验证商品是否存在且未被删除
        Product product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted()) {
            throw new RuntimeException("商品不存在");
        }

        // 检查是否已经收藏过此商品
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("product_id", productId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("已收藏此商品");
        }

        // 创建收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreatedAt(LocalDateTime.now());

        favoriteMapper.insert(favorite);
        return favorite;
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        // 检查收藏是否存在
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("product_id", productId);
        if (favoriteMapper.selectCount(wrapper) == 0) {
            throw new RuntimeException("收藏不存在");
        }

        // 删除收藏记录
        favoriteMapper.delete(wrapper);
    }

    @Override
    public PageResponse<FavoriteResponse> listFavorites(Long userId, Integer page, Integer limit) {
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("created_at");

        Page<Favorite> pageResult = favoriteMapper.selectPage(new Page<>(page, limit), wrapper);

        List<FavoriteResponse> items = pageResult.getRecords().stream().map(favorite -> {
            FavoriteResponse response = new FavoriteResponse();
            response.setFavorite_id(favorite.getUid());
            response.setProduct_id(favorite.getProductId());
            response.setCreated_at(favorite.getCreatedAt());
            return response;
        }).collect(Collectors.toList());

        PageResponse<FavoriteResponse> response = new PageResponse<>();
        response.setTotal(pageResult.getTotal());
        response.setPage(page);
        response.setLimit(limit);
        response.setItems(items);

        return response;
    }
}

