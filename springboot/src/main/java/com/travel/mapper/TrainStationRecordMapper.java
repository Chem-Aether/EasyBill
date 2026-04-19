package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.TrainStationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface TrainStationRecordMapper extends BaseMapper<TrainStationRecord> {

    @Select({
	    "<script>",
	    "select train_id as trainId, count(1) as cnt",
	    "from train_station",
	    "where train_id in",
	    "<foreach collection='trainIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>",
	    "group by train_id",
	    "</script>"
    })
    List<Map<String, Object>> countByTrainIds(@Param("trainIds") List<Long> trainIds);

}