package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.User;
import org.stnhh.sdu_flea_market.data.vo.auth.LoginResponse;
import org.stnhh.sdu_flea_market.data.vo.user.UserProfileResponse;
import org.stnhh.sdu_flea_market.data.vo.user.UpdateProfileRequest;

public interface UserService {
    User register(String username, String email, String password);
    LoginResponse login(String username, String email, String password);
    User getUserById(Long userId);
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
    void changePassword(Long userId, String oldPassword, String newPassword);
    void logout(Long userId);
}

