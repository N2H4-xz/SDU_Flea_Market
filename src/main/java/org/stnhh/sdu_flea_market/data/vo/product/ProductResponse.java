package org.stnhh.sdu_flea_market.data.vo.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long product_id;
    private String title;
    private String description;
    private BigDecimal price;
    private String condition;
    private String category;
    private String campus;
    private List<String> images;
    private SellerInfo seller;
    private Integer status;
    private Integer view_count;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerInfo {
        private Long user_id;
        private String nickname;
        private String avatar;
        private String campus;
        private String phone;
        private String wechat;
    }
}

