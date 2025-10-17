package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.Order;
import org.stnhh.sdu_flea_market.data.vo.order.OrderRequest;
import org.stnhh.sdu_flea_market.data.vo.order.OrderResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;

public interface OrderService {
    Order createOrder(Long buyerId, OrderRequest request);
    OrderResponse getOrderDetail(Long orderId, Long userId);
    PageResponse<OrderResponse> listOrders(Long userId, Integer page, Integer limit, String status, String role);
    OrderResponse updateOrderStatus(Long orderId, Long userId, String newStatus);
}

