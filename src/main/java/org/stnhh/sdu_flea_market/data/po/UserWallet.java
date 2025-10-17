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
    @TableId(type = IdType.AUTO)
    private Long uid;

    private Long userId;
    private BigDecimal balance; // 账户余额

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

