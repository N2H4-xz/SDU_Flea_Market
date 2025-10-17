package org.stnhh.sdu_flea_market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stnhh.sdu_flea_market.data.po.Message;
import org.stnhh.sdu_flea_market.data.vo.message.MessageResponse;
import org.stnhh.sdu_flea_market.data.vo.PageResponse;
import org.stnhh.sdu_flea_market.mapper.MessageMapper;
import org.stnhh.sdu_flea_market.service.MessageService;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public PageResponse<MessageResponse> getMessageHistory(Long userId, Long otherUserId, Integer page, Integer limit) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.eq("sender_id", userId).eq("recipient_id", otherUserId)
                .or()
                .eq("sender_id", otherUserId).eq("recipient_id", userId))
                .orderByDesc("created_at");

        Page<Message> pageResult = messageMapper.selectPage(new Page<>(page, limit), wrapper);

        List<MessageResponse> items = pageResult.getRecords().stream().map(message -> {
            MessageResponse response = new MessageResponse();
            response.setMessage_id(message.getUid());
            response.setSender_id(message.getSenderId());
            response.setRecipient_id(message.getRecipientId());
            response.setContent(message.getContent());
            response.setCreated_at(message.getCreatedAt());
            return response;
        }).collect(Collectors.toList());

        PageResponse<MessageResponse> response = new PageResponse<>();
        response.setTotal(pageResult.getTotal());
        response.setPage(page);
        response.setLimit(limit);
        response.setItems(items);

        return response;
    }
}

