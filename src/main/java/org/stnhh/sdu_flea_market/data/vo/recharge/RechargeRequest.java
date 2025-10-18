package org.stnhh.sdu_flea_market.data.vo.recharge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRequest {
    private BigDecimal amount;
}

