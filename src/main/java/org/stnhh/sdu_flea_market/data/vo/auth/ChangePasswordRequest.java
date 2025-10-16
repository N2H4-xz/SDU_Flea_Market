package org.stnhh.sdu_flea_market.data.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    private String old_password;
    private String new_password;
    private String confirm_password;
}

