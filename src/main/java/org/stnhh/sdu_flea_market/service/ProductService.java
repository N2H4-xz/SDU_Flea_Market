package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.Product;
import org.stnhh.sdu_flea_market.data.vo.product.ProductRequest;
import org.stnhh.sdu_flea_market.data.vo.product.ProductResponse;
import org.stnhh.sdu_flea_market.data.vo.product.ProductListResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;

public interface ProductService {
    Product createProduct(Long sellerId, ProductRequest request);
    ProductResponse getProductDetail(Long productId);
    PageResponse<ProductListResponse> listProducts(Integer page, Integer limit, String keyword,
                                                    String category, String campus, String sort, String condition, Long sellerId);
    ProductResponse updateProduct(Long productId, Long sellerId, ProductRequest request);
    void deleteProduct(Long productId, Long sellerId);
}

