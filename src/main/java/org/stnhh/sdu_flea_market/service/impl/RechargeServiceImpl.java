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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RechargeServiceImpl implements RechargeService {

    @Autowired
    private RechargeMapper rechargeMapper;

    @Override
    public Recharge createRecharge(Long userId, RechargeRequest request) {
        Recharge recharge = new Recharge();
        recharge.setUserId(userId);
        recharge.setAmount(request.getAmount());
        recharge.setCreatedAt(LocalDateTime.now());
        recharge.setUpdatedAt(LocalDateTime.now());

        rechargeMapper.insert(recharge);
        return recharge;
    }

    @Override
    public PageResponse<RechargeResponse> getRechargeHistory(Long userId, Integer page, Integer limit, String status) {
        QueryWrapper<Recharge> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }

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

