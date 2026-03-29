package com.easybill.mapper;

import com.easybill.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user")
    List<User> findAll();

    @Select("SELECT * FROM user WHERE account = #{account}")
    User selectByAccount(String account);

    @Insert("insert into user(account, password, user_name, role) " +
            "values(#{account}, #{password}, #{user_name}, #{role})")
    int insert(User user);
}
