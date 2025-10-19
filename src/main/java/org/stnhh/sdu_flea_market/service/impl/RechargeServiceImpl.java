package org.stnhh.sdu_flea_market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stnhh.sdu_flea_market.data.po.Recharge;
import org.stnhh.sdu_flea_market.data.vo.recharge.RechargeRequest;
import org.stnhh.sdu_flea_market.data.vo.recharge.RechargeResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.mapper.RechargeMapper;
import org.stnhh.sdu_flea_market.service.RechargeService;
import org.stnhh.sdu_flea_market.service.UserWalletService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RechargeServiceImpl implements RechargeService {

    @Autowired
    private RechargeMapper rechargeMapper;

    @Autowired
    private UserWalletService userWalletService;

    @Override
    public Recharge createRecharge(Long userId, RechargeRequest request) {
        Recharge recharge = new Recharge();
        recharge.setUserId(userId);
        recharge.setAmount(request.getAmount());
        recharge.setCreatedAt(LocalDateTime.now());
        recharge.setUpdatedAt(LocalDateTime.now());

        rechargeMapper.insert(recharge);

        // ✅ 充值成功后，增加用户余额
        userWalletService.addBalance(userId, userId, request.getAmount());

        return recharge;
    }

    @Override
    public PageResponse<RechargeResponse> getRechargeHistory(Long userId, Integer page, Integer limit, String status) {
        QueryWrapper<Recharge> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        // 注意：Recharge 表中没有 status 字段，status 参数被忽略
        // 如果需要按状态筛选，请在后续版本中添加 status 字段

        wrapper.orderByDesc("created_at");

        Page<Recharge> pageResult = rechargeMapper.selectPage(new Page<>(page, limit), wrapper);

        List<RechargeResponse> items = pageResult.getRecords().stream().map(recharge -> {
            RechargeResponse response = new RechargeResponse();
            response.setRecharge_id(recharge.getUid());
            response.setAmount(recharge.getAmount());
            response.setCreated_at(recharge.getCreatedAt());
            return response;
        }).collect(Collectors.toList());

        PageResponse<RechargeResponse> response = new PageResponse<>();
        response.setTotal(pageResult.getTotal());
        response.setPage(page);
        response.setLimit(limit);
        response.setItems(items);

        return response;
    }
}

