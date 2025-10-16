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
@TableName("users")
public class User {
    @TableId(type = IdType.ASSIGN_UUID)
    private String userId;
    
    private String username;
    private String email;
    private String passwordHash;
    private String avatar;
    private String nickname;
    private String campus;
    private String major;
    private String phone;
    private String wechat;
    private String bio;
    private String status; // active/inactive/banned
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

