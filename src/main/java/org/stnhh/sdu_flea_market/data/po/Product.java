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
@TableName("products")
public class Product {
    @TableId(type = IdType.ASSIGN_UUID)
    private String productId;
    
    private String sellerId;
    private String title;
    private String description;
    private String category; // 电子产品/图书/服装/其他
    private BigDecimal price;
    private String condition; // 全新/九成新/八成新/七成新/较旧
    private String campus;
    private String status; // active/sold/inactive
    private Integer viewCount;
    private Boolean isDeleted;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

