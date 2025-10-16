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
    private String order_id;
    private String product_id;
    private String buyer_id;
    private String seller_id;
    private BigDecimal amount;
    private String status;
    private String payment_method;
    private LocalDateTime created_at;
    private LocalDateTime paid_at;
    private LocalDateTime completed_at;
}

