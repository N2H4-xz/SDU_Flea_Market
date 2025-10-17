package org.stnhh.sdu_flea_market.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("product_images")
public class ProductImage {
    @TableId(type = IdType.AUTO)
    private Long uid;

    private Long productId;
    private String imageUrl;
    private Boolean isThumbnail;
    private Integer sortOrder;
    
    private LocalDateTime createdAt;
}

