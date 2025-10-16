package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.User;
import org.stnhh.sdu_flea_market.data.vo.auth.LoginResponse;
import org.stnhh.sdu_flea_market.data.vo.user.UserProfileResponse;
import org.stnhh.sdu_flea_market.data.vo.user.UpdateProfileRequest;

public interface UserService {
    User register(String username, String email, String password);
    LoginResponse login(String email, String password);
    User getUserById(String userId);
    UserProfileResponse getProfile(String userId);
    UserProfileResponse updateProfile(String userId, UpdateProfileRequest request);
    void changePassword(String userId, String oldPassword, String newPassword);
    void logout(String token);
}

