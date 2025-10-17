package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.annotation.Auth;
import org.stnhh.sdu_flea_market.data.po.Recharge;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.recharge.RechargeRequest;
import org.stnhh.sdu_flea_market.data.vo.recharge.RechargeResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.service.RechargeService;
import org.stnhh.sdu_flea_market.utils.ResponseUtil;

@RestController
@RequestMapping("/recharge")
public class RechargeController {

    @Autowired
    private RechargeService rechargeService;

    @Auth
    @PostMapping
    public ResponseEntity<Result> createRecharge(@RequestBody RechargeRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String userId = (String) httpRequest.getAttribute("userId");

            // 创建充值订单
            Recharge recharge = rechargeService.createRecharge(userId, request);
            return Result.success(recharge, "充值订单创建成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Auth
    @GetMapping("/history")
    public ResponseEntity<Result> getRechargeHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String status,
            HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String userId = (String) httpRequest.getAttribute("userId");

            // 获取充值历史记录
            PageResponse<RechargeResponse> response = rechargeService.getRechargeHistory(userId, page, limit, status);
            return Result.success(response, "获取成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}

