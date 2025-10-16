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
    @TableId(type = IdType.ASSIGN_UUID)
    private String rechargeId;
    
    private String userId;
    private BigDecimal amount;
    private String status; // pending/completed/failed
    private String paymentMethod; // alipay/wechat/card
    private String paymentUrl;
    
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
}

