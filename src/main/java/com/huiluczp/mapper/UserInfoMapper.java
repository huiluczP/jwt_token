package com.huiluczp.mapper;

import com.huiluczp.bean.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserInfoMapper {
    @Select("select id, user_id as userId, user_name as userName, user_phone as userPhone, user_birthday as userBirthday" +
            " from user_info where user_id = #{userId}")
    public UserInfo getUserInfo(String userId);
}
