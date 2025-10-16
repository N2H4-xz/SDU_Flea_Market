package org.stnhh.sdu_flea_market.data.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String nickname;
    private String avatar;
    private String campus;
    private String major;
    private String phone;
    private String wechat;
    private String bio;
}

