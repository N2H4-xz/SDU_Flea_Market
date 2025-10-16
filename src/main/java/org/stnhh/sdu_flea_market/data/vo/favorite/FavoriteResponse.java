package org.stnhh.sdu_flea_market.data.vo.favorite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private String favorite_id;
    private String product_id;
    private LocalDateTime created_at;
}

