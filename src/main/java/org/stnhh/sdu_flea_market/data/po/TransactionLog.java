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
@TableName("transaction_logs")
public class TransactionLog {
    @TableId(type = IdType.ASSIGN_UUID)
    private String logId;
    
    private String userId;
    private String type; // recharge/purchase/refund/withdrawal
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String relatedId; // order_id or recharge_id
    private String description;
    
    private LocalDateTime createdAt;
}

