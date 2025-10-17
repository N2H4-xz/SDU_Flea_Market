package org.stnhh.sdu_flea_market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.stnhh.sdu_flea_market.data.po.Recharge;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RechargeMapper extends BaseMapper<Recharge> {
    void getAmountByUid(Long userId);
}

