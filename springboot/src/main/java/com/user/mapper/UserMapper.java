package com.user.mapper;

import com.user.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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

    @Update("UPDATE user SET password = #{password} WHERE account = #{account}")
    int updatePasswordByAccount(String account, String password);
}
