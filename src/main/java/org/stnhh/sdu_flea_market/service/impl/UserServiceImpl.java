package org.stnhh.sdu_flea_market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stnhh.sdu_flea_market.data.po.User;
import org.stnhh.sdu_flea_market.data.vo.auth.LoginResponse;
import org.stnhh.sdu_flea_market.data.vo.user.UserProfileResponse;
import org.stnhh.sdu_flea_market.data.vo.user.UpdateProfileRequest;
import org.stnhh.sdu_flea_market.mapper.UserMapper;
import org.stnhh.sdu_flea_market.service.UserService;
import org.stnhh.sdu_flea_market.utils.JWTUtil;
import org.stnhh.sdu_flea_market.cache.IGlobalCache;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private IGlobalCache cache;

    @Override
    public User register(String username, String email, String password) {
        // 验证用户名、邮箱和密码不为空
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        // 检查用户名或邮箱是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username).or().eq("email", email);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名或邮箱已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        return user;
    }

    @Override
    public LoginResponse login(String username, String email, String password) {
        // 优先使用用户名登录，如果没有提供用户名则使用邮箱
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (username != null && !username.trim().isEmpty()) {
            wrapper.eq("username", username);
        } else if (email != null && !email.trim().isEmpty()) {
            wrapper.eq("email", email);
        } else {
            throw new RuntimeException("用户名或邮箱不能为空");
        }

        User user = userMapper.selectOne(wrapper);

        // 验证用户存在且密码正确
        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new RuntimeException("用户名/邮箱或密码错误");
        }

        // 生成JWT令牌
        String token = jwtUtil.getToken(user.getUid(), JWTUtil.EXPIRE_TIME, "STPlayTableSecretKey");

        // 构建登录响应
        LoginResponse response = new LoginResponse();
        response.setUser_id(user.getUid());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setToken(token);
        response.setExpires_in(JWTUtil.EXPIRE_TIME);

        return response;
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserProfileResponse response = new UserProfileResponse();
        response.setUser_id(user.getUid());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setNickname(user.getNickname());
        response.setCampus(user.getCampus());
        response.setMajor(user.getMajor());
        response.setPhone(user.getPhone());
        response.setWechat(user.getWechat());
        response.setBio(user.getBio());
        response.setCreated_at(user.getCreatedAt());
        response.setUpdated_at(user.getUpdatedAt());

        return response;
    }

    @Override
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        if (request.getCampus() != null) user.setCampus(request.getCampus());
        if (request.getMajor() != null) user.setMajor(request.getMajor());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getWechat() != null) user.setWechat(request.getWechat());
        if (request.getBio() != null) user.setBio(request.getBio());

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        return getProfile(userId);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!BCrypt.checkpw(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("旧密码错误");
        }

        user.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void logout(Long userId) {
        // 登出逻辑：可以在这里清除用户的 token 或其他会话信息
        // 由于 token 已经由 AuthAspect 处理，这里可以是空实现或记录日志
    }
}

