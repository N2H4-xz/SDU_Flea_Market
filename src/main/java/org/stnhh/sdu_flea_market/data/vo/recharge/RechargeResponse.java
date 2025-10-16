package org.stnhh.sdu_flea_market.data.vo.recharge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeResponse {
    private String recharge_id;
    private BigDecimal amount;
    private String status;
    private String payment_method;
    private String payment_url;
    private LocalDateTime created_at;
    private LocalDateTime completed_at;
}

