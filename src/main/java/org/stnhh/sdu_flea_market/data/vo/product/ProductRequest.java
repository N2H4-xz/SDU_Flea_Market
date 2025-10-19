package org.stnhh.sdu_flea_market.data.vo.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private String condition;
    private String campus;
    private MultipartFile[] images; // 商品图片
}

