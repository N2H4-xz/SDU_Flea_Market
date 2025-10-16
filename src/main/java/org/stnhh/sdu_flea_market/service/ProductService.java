package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.Product;
import org.stnhh.sdu_flea_market.data.vo.product.ProductRequest;
import org.stnhh.sdu_flea_market.data.vo.product.ProductResponse;
import org.stnhh.sdu_flea_market.data.vo.product.ProductListResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;

public interface ProductService {
    Product createProduct(String sellerId, ProductRequest request);
    ProductResponse getProductDetail(String productId);
    PageResponse<ProductListResponse> listProducts(Integer page, Integer limit, String keyword, 
                                                    String category, String campus, String sort, String condition);
    ProductResponse updateProduct(String productId, String sellerId, ProductRequest request);
    void deleteProduct(String productId, String sellerId);
}

