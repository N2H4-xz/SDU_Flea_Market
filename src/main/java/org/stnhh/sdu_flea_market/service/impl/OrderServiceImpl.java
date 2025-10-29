package org.stnhh.sdu_flea_market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stnhh.sdu_flea_market.data.po.Order;
import org.stnhh.sdu_flea_market.data.po.Product;
import org.stnhh.sdu_flea_market.data.po.ProductImage;
import org.stnhh.sdu_flea_market.data.vo.order.OrderRequest;
import org.stnhh.sdu_flea_market.data.vo.order.OrderResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.exception.BusinessConflictException;
import org.stnhh.sdu_flea_market.exception.ResourceNotFoundException;
import org.stnhh.sdu_flea_market.exception.UnauthorizedException;
import org.stnhh.sdu_flea_market.mapper.OrderMapper;
import org.stnhh.sdu_flea_market.mapper.ProductMapper;
import org.stnhh.sdu_flea_market.mapper.ProductImageMapper;
import org.stnhh.sdu_flea_market.service.OrderService;
import org.stnhh.sdu_flea_market.service.UserWalletService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private UserWalletService userWalletService;

    @Override
    public Order createOrder(Long buyerId, OrderRequest request) {
        // 验证商品是否存在且未被删除
        Product product = productMapper.selectById(request.getProduct_id());
        if (product == null || product.getIsDeleted()) {
            throw new ResourceNotFoundException("商品不存在");
        }

        // 防止用户购买自己的商品
        if (product.getSellerId().equals(buyerId)) {
            throw new BusinessConflictException("不能购买自己的商品");
        }

        // 检查同一个人对同一件商品是否已有正在进行的订单
        QueryWrapper<Order> existingOrderWrapper = new QueryWrapper<>();
        existingOrderWrapper.eq("buyer_id", buyerId)
                .eq("product_id", request.getProduct_id())
                .in("order_status", "pending_payment", "paid");

        long existingOrderCount = orderMapper.selectCount(existingOrderWrapper);
        if (existingOrderCount > 0) {
            throw new BusinessConflictException("该商品已有正在进行的订单，请先完成或取消之前的订单");
        }

        // 创建订单
        Order order = new Order();
        order.setProductId(request.getProduct_id());
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setAmount(product.getPrice());
        order.setOrderStatus("pending_payment");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.insert(order);

        return order;
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("订单不存在");
        }

        // Check if user is buyer or seller
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new UnauthorizedException("无权限");
        }

        return convertToResponse(order);
    }

    @Override
    public PageResponse<OrderResponse> listOrders(Long userId, Integer page, Integer limit, String status, String role) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();

        if ("buyer".equals(role)) {
            wrapper.eq("buyer_id", userId);
        } else if ("seller".equals(role)) {
            wrapper.eq("seller_id", userId);
        } else {
            wrapper.and(w -> w.eq("buyer_id", userId).or().eq("seller_id", userId));
        }

        if (status != null && !status.isEmpty()) {
            wrapper.eq("order_status", status);
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
    public OrderResponse updateOrderStatus(Long orderId, Long userId, String newStatus) {
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("订单不存在");
        }

        // 验证订单状态转换的合法性
        // 只有买家可以标记为已支付
        if ("paid".equals(newStatus) && !order.getBuyerId().equals(userId)) {
            throw new UnauthorizedException("无权限");
        }
        // 只有卖家可以标记为已完成
        if ("completed".equals(newStatus) && !order.getSellerId().equals(userId)) {
            throw new UnauthorizedException("无权限");
        }
        // 只有买家或卖家可以取消订单
        if ("cancelled".equals(newStatus) && !order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new UnauthorizedException("无权限");
        }

        // 验证状态转换的有效性
        String currentStatus = order.getOrderStatus();
        if ("pending_payment".equals(currentStatus) && "completed".equals(newStatus)) {
            // 允许：待支付 -> 已完成
        } else if (("pending_payment".equals(currentStatus)) && "cancelled".equals(newStatus)) {
            // 允许：待支付或已支付 -> 已取消
        } else if (!"cancelled".equals(currentStatus) && !"completed".equals(currentStatus)) {
            // 防止已取消或已完成的订单再次更新
            throw new RuntimeException("订单状态不允许转换");
        }

        // 更新订单状态
        order.setOrderStatus(newStatus);
        if ("completed".equals(newStatus)) {
            order.setPaidAt(LocalDateTime.now());
            order.setCompletedAt(LocalDateTime.now());

            // ✅ 卖家接受订单时的处理
            // 1. 检查买家余额是否充足，如果充足则扣钱
            userWalletService.deductBalance(order.getBuyerId(), order.getBuyerId(), order.getAmount());

            // 2. 给卖家钱包转钱
            userWalletService.addBalance(order.getSellerId(), order.getSellerId(), order.getAmount());

            // 3. 更新商品状态为已卖出
            Product product = productMapper.selectById(order.getProductId());
            if (product != null) {
                product.setProductStatus(1); // 1 = sold（已卖出）
                product.setUpdatedAt(LocalDateTime.now());
                productMapper.updateById(product);
            }

            // 4. 取消其他针对同一商品的订单
            cancelOtherOrdersForProduct(order.getProductId(), order.getUid());
        }
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.updateById(order);
        return convertToResponse(order);
    }

    /**
     * 取消所有针对同一商品的其他订单
     */
    private void cancelOtherOrdersForProduct(Long productId, Long acceptedOrderId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId)
                .ne("uid", acceptedOrderId)
                .in("order_status", "pending_payment", "paid");

        List<Order> ordersToCancel = orderMapper.selectList(wrapper);
        for (Order order : ordersToCancel) {
            order.setOrderStatus("cancelled");
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrder_id(order.getUid());
        response.setProduct_id(order.getProductId());
        response.setBuyer_id(order.getBuyerId());
        response.setSeller_id(order.getSellerId());
        response.setAmount(order.getAmount());
        response.setStatus(order.getOrderStatus());
        response.setCreated_at(order.getCreatedAt());
        response.setPaid_at(order.getPaidAt());
        response.setCompleted_at(order.getCompletedAt());

        // 获取商品标题和照片
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            response.setProduct_title(product.getTitle());

            // 获取商品缩略图
            QueryWrapper<ProductImage> imageWrapper = new QueryWrapper<>();
            imageWrapper.eq("product_id", order.getProductId()).eq("is_thumbnail", true);
            ProductImage thumbnail = productImageMapper.selectOne(imageWrapper);

            // ✅ 如果有缩略图，加上 URL 前缀；否则返回默认图片
            if (thumbnail != null && thumbnail.getImageUrl() != null && !thumbnail.getImageUrl().isEmpty()) {
                response.setProduct_image("http://154.36.178.147:15634/" + thumbnail.getImageUrl());
            } else {
                response.setProduct_image("http://154.36.178.147:15634/defaultProduct.jpg");
            }
        } else {
            // 商品不存在时，返回默认图片
            response.setProduct_image("http://154.36.178.147:15634/defaultProduct.jpg");
        }

        return response;
    }
}

