package org.stnhh.sdu_flea_market.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import org.stnhh.sdu_flea_market.utils.ResponseUtil;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Auth
    @PostMapping
    public ResponseEntity<Result> createProduct(@RequestBody ProductRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String sellerId = (String) httpRequest.getAttribute("userId");

            // 创建商品
            Product product = productService.createProduct(sellerId, request);
            return Result.success(product, "商品发布成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
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
        try {
            // 获取商品列表
            PageResponse<ProductListResponse> response = productService.listProducts(page, limit, keyword, category, campus, sort, condition);
            return Result.success(response, "获取成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Result> getProductDetail(@PathVariable String productId) {
        try {
            // 获取商品详情
            ProductResponse response = productService.getProductDetail(productId);
            return Result.success(response, "获取成功");
        } catch (Exception e) {
            return Result.error(404, e.getMessage());
        }
    }

    @Auth
    @PutMapping("/{productId}")
    public ResponseEntity<Result> updateProduct(@PathVariable String productId, @RequestBody ProductRequest request, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String sellerId = (String) httpRequest.getAttribute("userId");

            // 更新商品信息
            ProductResponse response = productService.updateProduct(productId, sellerId, request);
            return Result.success(response, "商品更新成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Auth
    @DeleteMapping("/{productId}")
    public ResponseEntity<Result> deleteProduct(@PathVariable String productId, HttpServletRequest httpRequest) {
        try {
            // 从请求属性中获取userId（由AuthAspect设置）
            String sellerId = (String) httpRequest.getAttribute("userId");

            // 删除商品
            productService.deleteProduct(productId, sellerId);
            return Result.ok();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}

