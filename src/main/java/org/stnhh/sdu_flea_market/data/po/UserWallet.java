package org.stnhh.sdu_flea_market.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_wallets")
public class UserWallet {
    @TableId(type = IdType.ASSIGN_UUID)
    private String walletId;

    private String userId;
    private BigDecimal balance; // 账户余额
    private BigDecimal totalRecharged; // 总充值金额
    private BigDecimal totalSpent; // 总消费金额

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

