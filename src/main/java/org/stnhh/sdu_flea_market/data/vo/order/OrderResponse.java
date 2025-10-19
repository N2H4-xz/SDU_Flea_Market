package org.stnhh.sdu_flea_market.data.vo.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long order_id;
    private Long product_id;
    private String product_title; // 商品标题（用于列表显示）
    private String product_image; // 商品照片（缩略图）
    private Long buyer_id;
    private Long seller_id;
    private BigDecimal amount;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime paid_at;
    private LocalDateTime completed_at;
}

