package com.bill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bill.entity.BillCategory;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface BillCategoryMapper extends BaseMapper<BillCategory> {
    @Select("SELECT * FROM bill_category WHERE is_deleted = 0 ORDER BY type, cate_id")
    List<BillCategory> findAllActive();

    @Update("UPDATE bill_category SET is_deleted = 1, update_time = CURRENT_TIMESTAMP WHERE cate_id = #{cateId}")
    int softDelete(String cateId);
}
