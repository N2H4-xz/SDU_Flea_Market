package org.stnhh.sdu_flea_market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.stnhh.sdu_flea_market.data.po.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}

