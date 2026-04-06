package com.bill.mapper;

import com.bill.entity.Account;
import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountMapper {
    @Select("SELECT * FROM account WHERE user_id = #{userId} OR user_id = 0 ORDER BY id")
    List<Account> findByUserId(Integer userId);

    @Select("SELECT * FROM account WHERE id = #{id}")
    Account selectById(Integer id);

    @Update("UPDATE account SET balance = #{balance}, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateBalance(@Param("id") Integer id, @Param("balance") BigDecimal balance);
}
