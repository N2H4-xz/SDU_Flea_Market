package org.stnhh.sdu_flea_market.data.vo.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    private Long product_id;
    private String title;
    private BigDecimal price;
    private String condition;
    private String campus;
    private String category;
    private String thumbnail;
    private Long seller_id;
    private String seller_nickname;
    private Integer status;
    private LocalDateTime created_at;
}

