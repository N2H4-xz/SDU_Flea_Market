package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.Favorite;
import org.stnhh.sdu_flea_market.data.vo.favorite.FavoriteResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;

public interface FavoriteService {
    Favorite addFavorite(Long userId, Long productId);
    void removeFavorite(Long userId, Long productId);
    PageResponse<FavoriteResponse> listFavorites(Long userId, Integer page, Integer limit);
}

