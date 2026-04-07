package com.bill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bill.entity.Account;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

public interface AccountMapper extends BaseMapper<Account> {
    @Select("SELECT * FROM account WHERE user_id = #{userId} OR user_id = 0 ORDER BY id")
    List<Account> findByUserId(Integer userId);

    @Update("UPDATE account SET balance = #{balance}, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateBalance(@Param("id") Integer id, @Param("balance") BigDecimal balance);
}
