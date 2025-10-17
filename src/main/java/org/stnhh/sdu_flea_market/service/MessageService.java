package org.stnhh.sdu_flea_market.service;

import org.stnhh.sdu_flea_market.data.vo.message.MessageResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;

public interface MessageService {
    PageResponse<MessageResponse> getMessageHistory(Long userId, Long otherUserId, Integer page, Integer limit);
}

