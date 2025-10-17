package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.po.Comment;
import org.stnhh.sdu_flea_market.data.vo.comment.CommentRequest;
import org.stnhh.sdu_flea_market.data.vo.comment.CommentResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;

public interface CommentService {
    Comment createComment(Long productId, Long authorId, CommentRequest request);
    PageResponse<CommentResponse> listComments(Long productId, Integer page, Integer limit, String sort);
    void deleteComment(Long commentId, Long userId);
}

