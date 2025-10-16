package org.stnhh.sdu_flea_market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.stnhh.sdu_flea_market.data.po.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}

