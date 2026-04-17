package com.travel.service;

import com.travel.dto.FlightRecordQueryDTO;
import com.travel.entity.FlightRecord;
import com.travel.mapper.FlightRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class FlightRecordService {

    @Autowired
    private FlightRecordMapper flightRecordMapper;

    /**
     * 查询全部机票记录
     */
    public Object list(FlightRecordQueryDTO dto) {
        LambdaQueryWrapper<FlightRecord> wrapper = Wrappers.lambdaQuery();

        // 多条件动态拼接
        wrapper.eq(StringUtils.hasText(dto.getFlightNo()), FlightRecord::getFlightNo, dto.getFlightNo());
        wrapper.eq(StringUtils.hasText(dto.getAircraftReg()), FlightRecord::getAircraftReg, dto.getAircraftReg());
        wrapper.eq(StringUtils.hasText(dto.getAircraftType()), FlightRecord::getAircraftType, dto.getAircraftType());
        wrapper.eq(StringUtils.hasText(dto.getDepartureAirport()), FlightRecord::getDepartureAirport, dto.getDepartureAirport());
        wrapper.eq(StringUtils.hasText(dto.getArrivalAirport()), FlightRecord::getArrivalAirport, dto.getArrivalAirport());
        wrapper.eq(StringUtils.hasText(dto.getStopoverAirport()), FlightRecord::getStopoverAirport, dto.getStopoverAirport());

        // 起飞时间范围
        wrapper.ge(dto.getTakeoffTimeStart() != null, FlightRecord::getTakeoffTime, dto.getTakeoffTimeStart());
        wrapper.le(dto.getTakeoffTimeEnd() != null, FlightRecord::getTakeoffTime, dto.getTakeoffTimeEnd());

        // 排序（按起飞时间倒序）
        wrapper.orderByDesc(FlightRecord::getTakeoffTime);

        // 是否分页
        if (dto.getPageNum() != null && dto.getPageSize() != null) {
            Page<FlightRecord> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return flightRecordMapper.selectPage(page, wrapper);
        }

        // 不分页，返回全部
        return flightRecordMapper.selectList(wrapper);
    }

    /**
     * 单个新增
     */
    public boolean save(FlightRecord flightRecord) {
        return flightRecordMapper.insert(flightRecord) > 0;
    }

    /**
     * 批量新增
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<FlightRecord> list) {
        // MyBatis-Plus 批量插入
        for (FlightRecord record : list) {
            flightRecordMapper.insert(record);
        }
        return true;
    }
    
    /**
     * 根据ID修改
     */
    public boolean updateById(FlightRecord flightRecord) {
        return flightRecordMapper.updateById(flightRecord) > 0;
    }

    /**
     * 根据ID删除
     */
    public boolean removeById(Long id) {
        return flightRecordMapper.deleteById(id) > 0;
    }
}
