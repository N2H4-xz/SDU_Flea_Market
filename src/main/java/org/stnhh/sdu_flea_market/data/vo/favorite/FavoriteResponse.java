package org.stnhh.sdu_flea_market.data.vo.favorite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private Long favorite_id;
    private Long product_id;
    private Boolean is_deleted; // 商品是否被删除
    private LocalDateTime created_at;
}

