package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.data.po.Order;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.order.OrderRequest;
import org.stnhh.sdu_flea_market.data.vo.order.OrderResponse;
import org.stnhh.sdu_flea_market.data.vo.order.OrderStatusUpdateRequest;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.service.OrderService;
import org.stnhh.sdu_flea_market.utils.ResponseUtil;
import org.stnhh.sdu_flea_market.utils.TokenUtil;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Result> createOrder(@RequestBody OrderRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String buyerId = extractUserIdFromToken(token);

            // 创建订单
            Order order = orderService.createOrder(buyerId, request);
            return ResponseUtil.build(Result.success(order, "订单创建成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Result> listOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "buyer") String role,
            HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String userId = extractUserIdFromToken(token);

            // 获取订单列表
            PageResponse<OrderResponse> response = orderService.listOrders(userId, page, limit, status, role);
            return ResponseUtil.build(Result.success(response, "获取成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Result> getOrderDetail(@PathVariable String orderId, HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String userId = extractUserIdFromToken(token);

            // 获取订单详情
            OrderResponse response = orderService.getOrderDetail(orderId, userId);
            return ResponseUtil.build(Result.success(response, "获取成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(404, e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Result> updateOrderStatus(@PathVariable String orderId, @RequestBody OrderStatusUpdateRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求头中提取JWT令牌
            String token = TokenUtil.extractToken(httpRequest);
            String userId = extractUserIdFromToken(token);

            // 更新订单状态
            OrderResponse response = orderService.updateOrderStatus(orderId, userId, request.getStatus());
            return ResponseUtil.build(Result.success(response, "订单状态更新成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    private String extractUserIdFromToken(String token) {
        // TODO: 需要实现JWT令牌解析，从令牌中提取用户ID
        return "user_id_from_token";
    }
}

