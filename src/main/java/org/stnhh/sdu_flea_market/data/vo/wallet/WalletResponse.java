package org.stnhh.sdu_flea_market.data.vo.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Long wallet_id;
    private Long user_id;
    private BigDecimal balance;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}

