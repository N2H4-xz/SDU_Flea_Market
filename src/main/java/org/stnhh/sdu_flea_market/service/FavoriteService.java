package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.Favorite;
import org.stnhh.sdu_flea_market.data.vo.favorite.FavoriteResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;

public interface FavoriteService {
    Favorite addFavorite(String userId, String productId);
    void removeFavorite(String userId, String productId);
    PageResponse<FavoriteResponse> listFavorites(String userId, Integer page, Integer limit);
}

