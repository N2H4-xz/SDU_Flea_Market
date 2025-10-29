package org.stnhh.sdu_flea_market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stnhh.sdu_flea_market.data.po.Product;
import org.stnhh.sdu_flea_market.data.po.ProductImage;
import org.stnhh.sdu_flea_market.data.po.User;
import org.stnhh.sdu_flea_market.data.vo.product.ProductRequest;
import org.stnhh.sdu_flea_market.data.vo.product.ProductResponse;
import org.stnhh.sdu_flea_market.data.vo.product.ProductListResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.exception.ResourceNotFoundException;
import org.stnhh.sdu_flea_market.exception.UnauthorizedException;
import org.stnhh.sdu_flea_market.exception.InvalidParameterException;
import org.stnhh.sdu_flea_market.mapper.ProductMapper;
import org.stnhh.sdu_flea_market.mapper.ProductImageMapper;
import org.stnhh.sdu_flea_market.mapper.UserMapper;
import org.stnhh.sdu_flea_market.service.ProductService;
import org.stnhh.sdu_flea_market.utils.FileUploadUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Product createProduct(Long sellerId, ProductRequest request) {
        Product product = new Product();
        product.setSellerId(sellerId);
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setItemCondition(request.getCondition());
        product.setCampus(request.getCampus());
        product.setProductStatus(0); // 0=active
        product.setViewCount(0);
        product.setIsDeleted(false);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productMapper.insert(product);

        // 处理图片上传
        if (request.getImages() != null && request.getImages().length > 0) {
            try {
                List<String> fileNames = FileUploadUtil.uploadFiles(request.getImages());

                // 保存图片信息到数据库
                for (int i = 0; i < fileNames.size(); i++) {
                    ProductImage image = new ProductImage();
                    image.setProductId(product.getUid());
                    image.setImageUrl(fileNames.get(i)); // 保存文件名
                    image.setIsThumbnail(i == 0); // 第一张图片作为缩略图
                    image.setSortOrder(i);
                    image.setCreatedAt(LocalDateTime.now());
                    productImageMapper.insert(image);
                }
            } catch (IOException e) {
                throw new RuntimeException("图片上传失败: " + e.getMessage());
            }
        }

        return product;
    }

    @Override
    public ProductResponse getProductDetail(Long productId) {
        // 查询商品信息
        Product product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted() || product.getProductStatus() != 0) {
            throw new ResourceNotFoundException("商品不存在");
        }

        // 增加浏览次数
        product.setViewCount(product.getViewCount() + 1);
        productMapper.updateById(product);

        // 获取商品图片列表
        QueryWrapper<ProductImage> imageWrapper = new QueryWrapper<>();
        imageWrapper.eq("product_id", productId).orderByAsc("sort_order");

        List<ProductImage> images = productImageMapper.selectList(imageWrapper);

        // ✅ 为每张图片加上 URL 前缀，如果没有图片则返回默认图片
        List<String> imageUrls = images.stream().map(img -> {
            if (img.getImageUrl() != null && !img.getImageUrl().isEmpty()) {
                return "http://154.36.178.147:15634/" + img.getImageUrl();
            } else {
                return "http://154.36.178.147:15634/defaultProduct.png";
            }
        }).collect(Collectors.toList());

        // 如果没有图片，添加默认图片
        if (imageUrls.isEmpty()) {
            imageUrls.add("http://154.36.178.147:15634/defaultProduct.png");
        }

        // 获取卖家信息
        User seller = userMapper.selectById(product.getSellerId());

        // 构建商品详情响应
        ProductResponse response = new ProductResponse();
        response.setProduct_id(product.getUid());
        response.setTitle(product.getTitle());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setCondition(product.getItemCondition());
        response.setCategory(product.getCategory());
        response.setCampus(product.getCampus());
        response.setImages(imageUrls);
        response.setStatus(product.getProductStatus());
        response.setView_count(product.getViewCount());
        response.setCreated_at(product.getCreatedAt());
        response.setUpdated_at(product.getUpdatedAt());

        // 设置卖家信息
        if (seller != null) {
            ProductResponse.SellerInfo sellerInfo = new ProductResponse.SellerInfo();
            sellerInfo.setUser_id(seller.getUid());
            sellerInfo.setNickname(seller.getNickname());

            // ✅ 如果有头像，加上 URL 前缀；否则返回默认头像
            if (seller.getAvatar() != null && !seller.getAvatar().isEmpty()) {
                sellerInfo.setAvatar("http://154.36.178.147:15634/" + seller.getAvatar());
            } else {
                sellerInfo.setAvatar("http://154.36.178.147:15634/defaultProduct.png");
            }

            sellerInfo.setCampus(seller.getCampus());
            sellerInfo.setPhone(seller.getPhone());
            sellerInfo.setWechat(seller.getWechat());
            response.setSeller(sellerInfo);
        }

        return response;
    }

    @Override
    public PageResponse<ProductListResponse> listProducts(Integer page, Integer limit, String keyword,
                                                          String category, String campus, String sort, String condition, Long sellerId) {
        // 构建查询条件：只查询未删除且状态为活跃的商品
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", false).eq("product_status", 0);

        // 根据关键词搜索商品标题或描述
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("title", keyword).or().like("description", keyword));
        }
        // 按分类筛选
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        // 按校区筛选
        if (campus != null && !campus.isEmpty()) {
            wrapper.eq("campus", campus);
        }
        // 按商品状况筛选
        if (condition != null && !condition.isEmpty()) {
            wrapper.eq("item_condition", condition);
        }
        // ✅ 按卖方ID筛选
        if (sellerId != null) {
            wrapper.eq("seller_id", sellerId);
        }

        // 处理排序逻辑
        if ("price_asc".equals(sort)) {
            wrapper.orderByAsc("price");
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc("price");
        } else {
            // 默认按最新发布排序
            wrapper.orderByDesc("created_at");
        }

        // 执行分页查询
        Page<Product> pageResult = productMapper.selectPage(new Page<>(page, limit), wrapper);

        // 转换为响应对象列表
        List<ProductListResponse> items = pageResult.getRecords().stream().map(product -> {
            ProductListResponse item = new ProductListResponse();
            item.setProduct_id(product.getUid());
            item.setTitle(product.getTitle());
            item.setPrice(product.getPrice());
            item.setCondition(product.getItemCondition());
            item.setCampus(product.getCampus());
            item.setCategory(product.getCategory());
            item.setStatus(product.getProductStatus());
            item.setCreated_at(product.getCreatedAt());

            // 获取商品缩略图
            QueryWrapper<ProductImage> imageWrapper = new QueryWrapper<>();
            imageWrapper.eq("product_id", product.getUid()).eq("is_thumbnail", true);
            ProductImage thumbnail = productImageMapper.selectOne(imageWrapper);

            // ✅ 如果有缩略图，加上 URL 前缀；否则返回默认图片
            if (thumbnail != null && thumbnail.getImageUrl() != null && !thumbnail.getImageUrl().isEmpty()) {
                item.setThumbnail("http://154.36.178.147:15634/" + thumbnail.getImageUrl());
            } else {
                item.setThumbnail("http://154.36.178.147:15634/defaultProduct.png");
            }

            // 获取卖家昵称
            User seller = userMapper.selectById(product.getSellerId());
            if (seller != null) {
                item.setSeller_id(seller.getUid());
                item.setSeller_nickname(seller.getNickname());
            }

            return item;
        }).collect(Collectors.toList());

        // 构建分页响应
        PageResponse<ProductListResponse> response = new PageResponse<>();
        response.setTotal(pageResult.getTotal());
        response.setPage(page);
        response.setLimit(limit);
        response.setItems(items);

        return response;
    }

    @Override
    public ProductResponse updateProduct(Long productId, Long sellerId, ProductRequest request) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("商品不存在");
        }
        if (!product.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("无权限");
        }

        if (request.getTitle() != null) product.setTitle(request.getTitle());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCondition() != null) product.setItemCondition(request.getCondition());
        if (request.getCampus() != null) product.setCampus(request.getCampus());

        // 处理图片更新
        if (request.getImages() != null && request.getImages().length > 0) {
            try {
                // 删除旧图片
                QueryWrapper<ProductImage> oldImageWrapper = new QueryWrapper<>();
                oldImageWrapper.eq("product_id", productId);
                List<ProductImage> oldImages = productImageMapper.selectList(oldImageWrapper);
                for (ProductImage oldImage : oldImages) {
                    FileUploadUtil.deleteFile(oldImage.getImageUrl());
                }
                productImageMapper.delete(oldImageWrapper);

                // 上传新图片
                List<String> fileNames = FileUploadUtil.uploadFiles(request.getImages());

                // 保存新图片信息到数据库
                for (int i = 0; i < fileNames.size(); i++) {
                    ProductImage image = new ProductImage();
                    image.setProductId(productId);
                    image.setImageUrl(fileNames.get(i)); // 保存文件名
                    image.setIsThumbnail(i == 0); // 第一张图片作为缩略图
                    image.setSortOrder(i);
                    image.setCreatedAt(LocalDateTime.now());
                    productImageMapper.insert(image);
                }
            } catch (IOException e) {
                throw new RuntimeException("图片上传失败: " + e.getMessage());
            }
        }

        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);

        return getProductDetail(productId);
    }

    @Override
    public void deleteProduct(Long productId, Long sellerId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("商品不存在");
        }
        if (!product.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("无权限");
        }

        product.setIsDeleted(true);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
    }
}

