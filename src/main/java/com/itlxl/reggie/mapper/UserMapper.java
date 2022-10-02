package com.itlxl.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itlxl.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
