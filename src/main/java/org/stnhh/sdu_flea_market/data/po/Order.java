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
@TableName("orders")
public class Order {
    @TableId(type = IdType.ASSIGN_UUID)
    private String orderId;
    
    private String productId;
    private String buyerId;
    private String sellerId;
    private BigDecimal amount;
    private String status; // pending_payment/paid/completed/cancelled
    private String paymentMethod; // online/offline
    private Integer quantity;
    
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
}

