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

@CrossOrigin
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Auth
    @PostMapping
    public ResponseEntity<Result> createOrder(@RequestBody OrderRequest request) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long buyerId = AuthContextUtil.getUserId();

        // 创建订单
        Order order = orderService.createOrder(buyerId, request);
        return Result.success(order, "订单创建成功");
    }

    @Auth
    @GetMapping
    public ResponseEntity<Result> listOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "buyer") String role) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();

        // 获取订单列表
        PageResponse<OrderResponse> response = orderService.listOrders(userId, page, limit, status, role);
        return Result.success(response, "获取成功");
    }

    @Auth
    @GetMapping("/{orderId}")
    public ResponseEntity<Result> getOrderDetail(@PathVariable Long orderId) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();

        // 获取订单详情
        OrderResponse response = orderService.getOrderDetail(orderId, userId);
        return Result.success(response, "获取成功");
    }

    @Auth
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Result> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateRequest request) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long userId = AuthContextUtil.getUserId();

        // 更新订单状态
        OrderResponse response = orderService.updateOrderStatus(orderId, userId, request.getStatus());
        return Result.success(response, "订单状态更新成功");
    }
}

