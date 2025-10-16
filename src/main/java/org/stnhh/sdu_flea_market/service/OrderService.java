package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.Order;
import org.stnhh.sdu_flea_market.data.vo.order.OrderRequest;
import org.stnhh.sdu_flea_market.data.vo.order.OrderResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;

public interface OrderService {
    Order createOrder(String buyerId, OrderRequest request);
    OrderResponse getOrderDetail(String orderId, String userId);
    PageResponse<OrderResponse> listOrders(String userId, Integer page, Integer limit, String status, String role);
    OrderResponse updateOrderStatus(String orderId, String userId, String newStatus);
}

