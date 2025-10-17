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
    @TableId(type = IdType.AUTO)
    private Long uid;

    private Long sellerId;
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private String itemCondition;
    private String campus;
    private Integer productStatus; // 0=active, 1=sold, 2=inactive, 3=deleted, 4=reserved
    private Integer viewCount;
    private Boolean isDeleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

