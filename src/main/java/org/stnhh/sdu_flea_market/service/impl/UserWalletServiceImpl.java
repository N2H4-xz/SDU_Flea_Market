package org.stnhh.sdu_flea_market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stnhh.sdu_flea_market.data.po.UserWallet;
import org.stnhh.sdu_flea_market.data.vo.wallet.WalletResponse;
import org.stnhh.sdu_flea_market.exception.InsufficientBalanceException;
import org.stnhh.sdu_flea_market.exception.InvalidParameterException;
import org.stnhh.sdu_flea_market.exception.ResourceNotFoundException;
import org.stnhh.sdu_flea_market.exception.UnauthorizedException;
import org.stnhh.sdu_flea_market.mapper.UserWalletMapper;
import org.stnhh.sdu_flea_market.service.UserWalletService;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserWalletServiceImpl implements UserWalletService {

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Override
    public WalletResponse getWallet(Long userId) {
        UserWallet wallet = getUserWalletByUserId(userId);
        if (wallet == null) {
            wallet = initializeWallet(userId);
        }

        WalletResponse response = new WalletResponse();
        response.setWallet_id(wallet.getUid());
        response.setUser_id(wallet.getUserId());
        response.setBalance(wallet.getBalance());
        response.setCreated_at(wallet.getCreatedAt());
        response.setUpdated_at(wallet.getUpdatedAt());
        return response;
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        UserWallet wallet = getUserWalletByUserId(userId);
        if (wallet == null) {
            wallet = initializeWallet(userId);
        }
        return wallet.getBalance();
    }

    @Override
    public void addBalance(Long userId, Long requestUserId, BigDecimal amount) {
        // 权限检查：只能操作自己的余额
        if (!userId.equals(requestUserId)) {
            throw new UnauthorizedException("无权限");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidParameterException("充值金额必须大于0");
        }

        UserWallet wallet = getUserWalletByUserId(userId);
        if (wallet == null) {
            wallet = initializeWallet(userId);
        }

        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        userWalletMapper.updateById(wallet);
    }

    @Override
    public void deductBalance(Long userId, Long requestUserId, BigDecimal amount) {
        // 权限检查：只能操作自己的余额
        if (!userId.equals(requestUserId)) {
            throw new UnauthorizedException("无权限");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidParameterException("扣款金额必须大于0");
        }

        UserWallet wallet = getUserWalletByUserId(userId);
        if (wallet == null) {
            throw new ResourceNotFoundException("用户钱包不存在");
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("余额不足");
        }

        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        userWalletMapper.updateById(wallet);
    }

    @Override
    public UserWallet initializeWallet(Long userId) {
        UserWallet wallet = new UserWallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        userWalletMapper.insert(wallet);
        return wallet;
    }

    /**
     * 根据用户ID获取钱包信息
     */
    private UserWallet getUserWalletByUserId(Long userId) {
        QueryWrapper<UserWallet> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return userWalletMapper.selectOne(wrapper);
    }
}

