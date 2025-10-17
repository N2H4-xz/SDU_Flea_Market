package org.stnhh.sdu_flea_market.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.annotation.Auth;
import org.stnhh.sdu_flea_market.data.po.Order;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.order.OrderRequest;
import org.stnhh.sdu_flea_market.data.vo.order.OrderResponse;
import org.stnhh.sdu_flea_market.data.vo.order.OrderStatusUpdateRequest;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.service.OrderService;
import org.stnhh.sdu_flea_market.utils.AuthContextUtil;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Auth
    @PostMapping
    public ResponseEntity<Result> createOrder(@RequestBody OrderRequest request) {
        try {
            // 从请求上下文中获取userId（由AuthAspect设置）
            String buyerId = AuthContextUtil.getUserId();

            // 创建订单
            Order order = orderService.createOrder(buyerId, request);
            return Result.success(order, "订单创建成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Auth
    @GetMapping
    public ResponseEntity<Result> listOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "buyer") String role) {
        try {
            // 从请求上下文中获取userId（由AuthAspect设置）
            String userId = AuthContextUtil.getUserId();

            // 获取订单列表
            PageResponse<OrderResponse> response = orderService.listOrders(userId, page, limit, status, role);
            return Result.success(response, "获取成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Auth
    @GetMapping("/{orderId}")
    public ResponseEntity<Result> getOrderDetail(@PathVariable String orderId) {
        try {
            // 从请求上下文中获取userId（由AuthAspect设置）
            String userId = AuthContextUtil.getUserId();

            // 获取订单详情
            OrderResponse response = orderService.getOrderDetail(orderId, userId);
            return Result.success(response, "获取成功");
        } catch (Exception e) {
            return Result.error(404, e.getMessage());
        }
    }

    @Auth
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Result> updateOrderStatus(@PathVariable String orderId, @RequestBody OrderStatusUpdateRequest request) {
        try {
            // 从请求上下文中获取userId（由AuthAspect设置）
            String userId = AuthContextUtil.getUserId();

            // 更新订单状态
            OrderResponse response = orderService.updateOrderStatus(orderId, userId, request.getStatus());
            return Result.success(response, "订单状态更新成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}

