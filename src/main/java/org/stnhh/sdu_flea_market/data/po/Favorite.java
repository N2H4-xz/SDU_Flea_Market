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
@TableName("favorites")
public class Favorite {
    @TableId(type = IdType.ASSIGN_UUID)
    private String favoriteId;
    
    private String userId;
    private String productId;
    
    private LocalDateTime createdAt;
}

