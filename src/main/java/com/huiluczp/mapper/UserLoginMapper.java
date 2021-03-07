package com.huiluczp.mapper;

import com.huiluczp.bean.UserLogin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserLoginMapper {
    @Select("select id, user_id as userId, user_password as userPassword from user_login where user_id = #{userId}")
    public UserLogin getUserLoginInfo(String userId);
}
