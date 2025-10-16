package org.stnhh.sdu_flea_market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.stnhh.sdu_flea_market.data.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

