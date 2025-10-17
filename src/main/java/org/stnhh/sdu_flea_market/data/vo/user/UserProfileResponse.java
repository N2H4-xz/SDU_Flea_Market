package org.stnhh.sdu_flea_market.data.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long user_id;
    private String username;
    private String avatar;
    private String nickname;
    private String campus;
    private String major;
    private String phone;
    private String wechat;
    private String bio;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}

