package com.bill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bill.entity.BillDetail;
import com.bill.entity.CategoryStatistic;
import com.bill.entity.BillRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BillRecordMapper extends BaseMapper<BillRecord> {
    @Select({
            "<script>",
            "SELECT * FROM v_bill_detail WHERE user_id = #{userId}",
            "<if test='payType != null'> AND pay_type = #{payType}</if>",
            "<if test='accountId != null'> AND (out_account_id = #{accountId} OR in_account_id = #{accountId})</if>",
            "<if test='cateId != null and cateId != \"\"'> AND cate_id = #{cateId}</if>",
            "<if test='keyword != null and keyword != \"\"'> AND (counterparty_name LIKE CONCAT('%', #{keyword}, '%') OR commodity LIKE CONCAT('%', #{keyword}, '%') OR remark LIKE CONCAT('%', #{keyword}, '%'))</if>",
            "<if test='startTime != null and startTime != \"\"'> AND bill_time &gt;= #{startTime}</if>",
            "<if test='endTime != null and endTime != \"\"'> AND bill_time &lt;= #{endTime}</if>",
            "ORDER BY bill_time DESC",
            "</script>"
    })
    List<BillDetail> queryBillDetails(@Param("userId") Integer userId,
                                      @Param("payType") Integer payType,
                                      @Param("accountId") Integer accountId,
                                      @Param("cateId") String cateId,
                                      @Param("keyword") String keyword,
                                      @Param("startTime") String startTime,
                                      @Param("endTime") String endTime);

    @Select({
            "<script>",
            "SELECT d.cate_id, c.class_name AS category_name, c.type AS category_type, SUM(d.amount) AS total_amount",
            "FROM v_bill_detail d LEFT JOIN bill_category c ON d.cate_id = c.cate_id",
            "WHERE d.user_id = #{userId}",
            "<if test='payType != null'> AND d.pay_type = #{payType}</if>",
            "<if test='startTime != null and startTime != \"\"'> AND d.bill_time &gt;= #{startTime}</if>",
            "<if test='endTime != null and endTime != \"\"'> AND d.bill_time &lt;= #{endTime}</if>",
            "GROUP BY d.cate_id, c.class_name, c.type",
            "ORDER BY total_amount DESC",
            "</script>"
    })
    List<CategoryStatistic> categoryStatistics(@Param("userId") Integer userId,
                                               @Param("payType") Integer payType,
                                               @Param("startTime") String startTime,
                                               @Param("endTime") String endTime);
}
