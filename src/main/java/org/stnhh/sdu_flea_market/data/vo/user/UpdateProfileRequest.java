package org.stnhh.sdu_flea_market.data.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String nickname;
    private MultipartFile avatar;  // ✅ 改为 MultipartFile
    private String campus;
    private String major;
    private String phone;
    private String wechat;
    private String bio;
}

