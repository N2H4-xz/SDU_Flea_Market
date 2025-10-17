package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import org.stnhh.sdu_flea_market.utils.ResponseUtil;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Auth
    @PostMapping
    public ResponseEntity<Result> createOrder(@RequestBody OrderRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String buyerId = (String) httpRequest.getAttribute("userId");

            // 创建订单
            Order order = orderService.createOrder(buyerId, request);
            return ResponseUtil.build(Result.success(order, "订单创建成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    @Auth
    @GetMapping
    public ResponseEntity<Result> listOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "buyer") String role,
            HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String userId = (String) httpRequest.getAttribute("userId");

            // 获取订单列表
            PageResponse<OrderResponse> response = orderService.listOrders(userId, page, limit, status, role);
            return ResponseUtil.build(Result.success(response, "获取成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }

    @Auth
    @GetMapping("/{orderId}")
    public ResponseEntity<Result> getOrderDetail(@PathVariable String orderId, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String userId = (String) httpRequest.getAttribute("userId");

            // 获取订单详情
            OrderResponse response = orderService.getOrderDetail(orderId, userId);
            return ResponseUtil.build(Result.success(response, "获取成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(404, e.getMessage()));
        }
    }

    @Auth
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Result> updateOrderStatus(@PathVariable String orderId, @RequestBody OrderStatusUpdateRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String userId = (String) httpRequest.getAttribute("userId");

            // 更新订单状态
            OrderResponse response = orderService.updateOrderStatus(orderId, userId, request.getStatus());
            return ResponseUtil.build(Result.success(response, "订单状态更新成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, e.getMessage()));
        }
    }
}

