package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.UserWallet;
import org.stnhh.sdu_flea_market.data.vo.wallet.WalletResponse;
import java.math.BigDecimal;

public interface UserWalletService {
    /**
     * 获取用户钱包信息
     */
    WalletResponse getWallet(Long userId);

    /**
     * 获取用户余额
     */
    BigDecimal getBalance(Long userId);

    /**
     * 增加用户余额（需要权限检查）
     */
    void addBalance(Long userId, Long requestUserId, BigDecimal amount);

    /**
     * 扣除用户余额（需要权限检查）
     */
    void deductBalance(Long userId, Long requestUserId, BigDecimal amount);

    /**
     * 初始化用户钱包
     */
    UserWallet initializeWallet(Long userId);
}

