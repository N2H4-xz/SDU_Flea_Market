package org.stnhh.sdu_flea_market.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stnhh.sdu_flea_market.annotation.Auth;
import org.stnhh.sdu_flea_market.data.po.Product;
import org.stnhh.sdu_flea_market.data.vo.Result;
import org.stnhh.sdu_flea_market.data.vo.product.ProductRequest;
import org.stnhh.sdu_flea_market.data.vo.product.ProductResponse;
import org.stnhh.sdu_flea_market.data.vo.product.ProductListResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.service.ProductService;
import org.stnhh.sdu_flea_market.utils.AuthContextUtil;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Auth
    @PostMapping
    public ResponseEntity<Result> createProduct(@RequestBody ProductRequest request) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long sellerId = AuthContextUtil.getUserId();

        // 创建商品
        Product product = productService.createProduct(sellerId, request);
        return Result.success(product, "商品发布成功");
    }

    @GetMapping
    public ResponseEntity<Result> listProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String campus,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(required = false) String condition) {
        // 获取商品列表
        PageResponse<ProductListResponse> response = productService.listProducts(page, limit, keyword, category, campus, sort, condition);
        return Result.success(response, "获取成功");
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Result> getProductDetail(@PathVariable Long productId) {
        // 获取商品详情
        ProductResponse response = productService.getProductDetail(productId);
        return Result.success(response, "获取成功");
    }

    @Auth
    @PutMapping("/{productId}")
    public ResponseEntity<Result> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest request) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long sellerId = AuthContextUtil.getUserId();

        // 更新商品信息
        ProductResponse response = productService.updateProduct(productId, sellerId, request);
        return Result.success(response, "商品更新成功");
    }

    @Auth
    @DeleteMapping("/{productId}")
    public ResponseEntity<Result> deleteProduct(@PathVariable Long productId) {
        // 从请求上下文中获取userId（由AuthAspect设置）
        Long sellerId = AuthContextUtil.getUserId();

        // 删除商品
        productService.deleteProduct(productId, sellerId);
        return Result.ok();
    }
}

