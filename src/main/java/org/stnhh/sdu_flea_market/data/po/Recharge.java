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
@TableName("recharges")
public class Recharge {
    @TableId(type = IdType.AUTO)
    private Long uid;

    private Long userId;
    private BigDecimal amount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

