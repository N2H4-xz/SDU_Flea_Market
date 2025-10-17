package org.stnhh.sdu_flea_market.data.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String username; // 用户名登录
    private String email;    // 邮箱登录（可选）
    private String password;
}

