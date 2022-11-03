package com.example.backend.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.admin.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
