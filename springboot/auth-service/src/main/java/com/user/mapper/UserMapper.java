package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.entity.User;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE account = #{account}")
    User selectByAccount(String account);

    @Update("UPDATE user SET password = #{password} WHERE account = #{account}")
    int updatePasswordByAccount(String account, String password);
}
