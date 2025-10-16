package org.stnhh.sdu_flea_market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stnhh.sdu_flea_market.data.po.Comment;
import org.stnhh.sdu_flea_market.data.po.Product;
import org.stnhh.sdu_flea_market.data.po.User;
import org.stnhh.sdu_flea_market.data.vo.comment.CommentRequest;
import org.stnhh.sdu_flea_market.data.vo.comment.CommentResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.mapper.CommentMapper;
import org.stnhh.sdu_flea_market.mapper.ProductMapper;
import org.stnhh.sdu_flea_market.mapper.UserMapper;
import org.stnhh.sdu_flea_market.service.CommentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Comment createComment(String productId, String authorId, CommentRequest request) {
        // 验证商品是否存在且未被删除
        Product product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted()) {
            throw new RuntimeException("商品不存在");
        }

        // 验证留言内容不为空
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("留言内容不能为空");
        }

        // 创建留言
        Comment comment = new Comment();
        comment.setProductId(productId);
        comment.setAuthorId(authorId);
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        commentMapper.insert(comment);
        return comment;
    }

    @Override
    public PageResponse<CommentResponse> listComments(String productId, Integer page, Integer limit, String sort) {
        // 验证商品是否存在且未被删除
        Product product = productMapper.selectById(productId);
        if (product == null || product.getIsDeleted()) {
            throw new RuntimeException("商品不存在");
        }

        // 构建查询条件
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId);

        // 处理排序逻辑
        if ("oldest".equals(sort)) {
            // 按最早发布排序
            wrapper.orderByAsc("created_at");
        } else {
            // 默认按最新发布排序
            wrapper.orderByDesc("created_at");
        }

        // 执行分页查询
        Page<Comment> pageResult = commentMapper.selectPage(new Page<>(page, limit), wrapper);

        // 转换为响应对象列表
        List<CommentResponse> items = pageResult.getRecords().stream().map(comment -> {
            CommentResponse response = new CommentResponse();
            response.setComment_id(comment.getCommentId());
            response.setProduct_id(comment.getProductId());
            response.setContent(comment.getContent());
            response.setCreated_at(comment.getCreatedAt());

            // 获取评论作者信息
            User author = userMapper.selectById(comment.getAuthorId());
            if (author != null) {
                CommentResponse.AuthorInfo authorInfo = new CommentResponse.AuthorInfo();
                authorInfo.setUser_id(author.getUserId());
                authorInfo.setNickname(author.getNickname());
                authorInfo.setAvatar(author.getAvatar());
                response.setAuthor(authorInfo);
            }

            return response;
        }).collect(Collectors.toList());

        // 构建分页响应
        PageResponse<CommentResponse> response = new PageResponse<>();
        response.setTotal(pageResult.getTotal());
        response.setPage(page);
        response.setLimit(limit);
        response.setItems(items);

        return response;
    }

    @Override
    public void deleteComment(String commentId, String userId) {
        // 查询留言
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("留言不存在");
        }

        // 验证用户权限：只有留言作者可以删除
        if (!comment.getAuthorId().equals(userId)) {
            throw new RuntimeException("无权限删除此留言");
        }

        // 删除留言
        commentMapper.deleteById(commentId);
    }
}

