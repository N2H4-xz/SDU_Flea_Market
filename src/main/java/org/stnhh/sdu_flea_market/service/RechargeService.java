package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.Recharge;
import org.stnhh.sdu_flea_market.data.vo.recharge.RechargeRequest;
import org.stnhh.sdu_flea_market.data.vo.recharge.RechargeResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;

public interface RechargeService {
    Recharge createRecharge(String userId, RechargeRequest request);
    PageResponse<RechargeResponse> getRechargeHistory(String userId, Integer page, Integer limit, String status);
}

