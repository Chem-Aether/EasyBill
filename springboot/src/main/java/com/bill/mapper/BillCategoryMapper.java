package com.bill.mapper;

import com.bill.entity.BillCategory;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BillCategoryMapper {
    @Select("SELECT * FROM bill_category WHERE is_deleted = 0 ORDER BY type, cate_id")
    List<BillCategory> findAllActive();

    @Select("SELECT * FROM bill_category WHERE cate_id = #{cateId}")
    BillCategory selectById(String cateId);

    @Insert("INSERT INTO bill_category(cate_id, class_name, parent_cate, level, icon, type, is_deleted) " +
            "VALUES(#{cate_id}, #{class_name}, #{parent_cate}, #{level}, #{icon}, #{type}, 0)")
    int insert(BillCategory category);

    @Update("UPDATE bill_category SET class_name = #{class_name}, parent_cate = #{parent_cate}, level = #{level}, icon = #{icon}, type = #{type}, update_time = CURRENT_TIMESTAMP WHERE cate_id = #{cate_id}")
    int update(BillCategory category);

    @Update("UPDATE bill_category SET is_deleted = 1, update_time = CURRENT_TIMESTAMP WHERE cate_id = #{cateId}")
    int softDelete(String cateId);
}
