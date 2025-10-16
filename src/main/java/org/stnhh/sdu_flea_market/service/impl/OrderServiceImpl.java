package org.stnhh.sdu_flea_market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stnhh.sdu_flea_market.data.po.Order;
import org.stnhh.sdu_flea_market.data.po.Product;
import org.stnhh.sdu_flea_market.data.vo.order.OrderRequest;
import org.stnhh.sdu_flea_market.data.vo.order.OrderResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.mapper.OrderMapper;
import org.stnhh.sdu_flea_market.mapper.ProductMapper;
import org.stnhh.sdu_flea_market.service.OrderService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Order createOrder(String buyerId, OrderRequest request) {
        // 验证商品是否存在且未被删除
        Product product = productMapper.selectById(request.getProduct_id());
        if (product == null || product.getIsDeleted()) {
            throw new RuntimeException("商品不存在");
        }

        // 防止用户购买自己的商品
        if (product.getSellerId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己的商品");
        }

        // 创建订单
        Order order = new Order();
        order.setProductId(request.getProduct_id());
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setAmount(product.getPrice());
        order.setStatus("pending_payment");
        order.setPaymentMethod(request.getPayment_method());
        order.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.insert(order);
        return order;
    }

    @Override
    public OrderResponse getOrderDetail(String orderId, String userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // Check if user is buyer or seller
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权限查看此订单");
        }

        return convertToResponse(order);
    }

    @Override
    public PageResponse<OrderResponse> listOrders(String userId, Integer page, Integer limit, String status, String role) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();

        if ("buyer".equals(role)) {
            wrapper.eq("buyer_id", userId);
        } else if ("seller".equals(role)) {
            wrapper.eq("seller_id", userId);
        } else {
            wrapper.and(w -> w.eq("buyer_id", userId).or().eq("seller_id", userId));
        }

        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }

        wrapper.orderByDesc("created_at");

        Page<Order> pageResult = orderMapper.selectPage(new Page<>(page, limit), wrapper);

        List<OrderResponse> items = pageResult.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PageResponse<OrderResponse> response = new PageResponse<>();
        response.setTotal(pageResult.getTotal());
        response.setPage(page);
        response.setLimit(limit);
        response.setItems(items);

        return response;
    }

    @Override
    public OrderResponse updateOrderStatus(String orderId, String userId, String newStatus) {
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单状态转换的合法性
        // 只有买家可以标记为已支付
        if ("paid".equals(newStatus) && !order.getBuyerId().equals(userId)) {
            throw new RuntimeException("只有买家可以标记为已支付");
        }
        // 只有卖家可以标记为已完成
        if ("completed".equals(newStatus) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("只有卖家可以标记为已完成");
        }
        // 只有买家或卖家可以取消订单
        if ("cancelled".equals(newStatus) && !order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权限取消此订单");
        }

        // 验证状态转换的有效性
        String currentStatus = order.getStatus();
        if ("pending_payment".equals(currentStatus) && "paid".equals(newStatus)) {
            // 允许：待支付 -> 已支付
        } else if ("paid".equals(currentStatus) && "completed".equals(newStatus)) {
            // 允许：已支付 -> 已完成
        } else if (("pending_payment".equals(currentStatus) || "paid".equals(currentStatus)) && "cancelled".equals(newStatus)) {
            // 允许：待支付或已支付 -> 已取消
        } else if (!"cancelled".equals(currentStatus) && !"completed".equals(currentStatus)) {
            // 防止已取消或已完成的订单再次更新
            throw new RuntimeException("订单状态不允许转换");
        }

        // 更新订单状态
        order.setStatus(newStatus);
        if ("paid".equals(newStatus)) {
            order.setPaidAt(LocalDateTime.now());
        } else if ("completed".equals(newStatus)) {
            order.setCompletedAt(LocalDateTime.now());
        }
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.updateById(order);
        return convertToResponse(order);
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrder_id(order.getOrderId());
        response.setProduct_id(order.getProductId());
        response.setBuyer_id(order.getBuyerId());
        response.setSeller_id(order.getSellerId());
        response.setAmount(order.getAmount());
        response.setStatus(order.getStatus());
        response.setPayment_method(order.getPaymentMethod());
        response.setCreated_at(order.getCreatedAt());
        response.setPaid_at(order.getPaidAt());
        response.setCompleted_at(order.getCompletedAt());

        // 获取商品标题
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            response.setProduct_title(product.getTitle());
        }

        return response;
    }
}

