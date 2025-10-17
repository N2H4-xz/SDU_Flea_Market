package org.stnhh.sdu_flea_market.data.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long user_id;
    private String username;
    private String token;
    private Integer expires_in;
}

