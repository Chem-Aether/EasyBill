package com.travel.service;

import com.travel.dto.FlightRecordQueryDTO;
import com.travel.entity.FlightRecord;
import com.travel.mapper.FlightRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.util.StringUtils;

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
    // 管理页更常用“包含匹配”，提升搜索体验
    wrapper.like(StringUtils.hasText(dto.getFlightNo()), FlightRecord::getFlightNo, dto.getFlightNo());
    wrapper.like(StringUtils.hasText(dto.getAircraftReg()), FlightRecord::getAircraftReg, dto.getAircraftReg());
    wrapper.like(StringUtils.hasText(dto.getAircraftType()), FlightRecord::getAircraftType, dto.getAircraftType());
    // 优先用 ICAO 查询（你现在前端查询传的是 ICAO）
    wrapper.like(StringUtils.hasText(dto.getDepartureIcao()), FlightRecord::getDepartureIcao, dto.getDepartureIcao());
    wrapper.like(StringUtils.hasText(dto.getArrivalIcao()), FlightRecord::getArrivalIcao, dto.getArrivalIcao());

    // 兼容：仍支持按中文机场名模糊查
    wrapper.like(StringUtils.hasText(dto.getDepartureAirport()), FlightRecord::getDepartureAirport, dto.getDepartureAirport());
    wrapper.like(StringUtils.hasText(dto.getArrivalAirport()), FlightRecord::getArrivalAirport, dto.getArrivalAirport());
    wrapper.like(StringUtils.hasText(dto.getStopoverAirport()), FlightRecord::getStopoverAirport, dto.getStopoverAirport());

        // 起飞时间范围
        wrapper.ge(dto.getTakeoffTimeStart() != null, FlightRecord::getTakeoffTime, dto.getTakeoffTimeStart());
        wrapper.le(dto.getTakeoffTimeEnd() != null, FlightRecord::getTakeoffTime, dto.getTakeoffTimeEnd());

        // 排序（按起飞时间倒序）
        wrapper.orderByDesc(FlightRecord::getTakeoffTime);

    // 永远分页：不传分页参数则使用默认值，避免全量查询
    long pageNum = (dto.getPageNum() == null || dto.getPageNum() < 1) ? 1L : dto.getPageNum().longValue();
    long pageSize = (dto.getPageSize() == null || dto.getPageSize() < 1) ? 10L : dto.getPageSize().longValue();
    pageSize = Math.min(pageSize, 100L);

    Page<FlightRecord> page = new Page<>(pageNum, pageSize);
    return flightRecordMapper.selectPage(page, wrapper);
    }

    /**
     * 单个新增
     */
    public boolean save(FlightRecord flightRecord) {
        return flightRecordMapper.insert(flightRecord) > 0;
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
        FlightRecord record = flightRecordMapper.selectById(id);
        if (record == null) {
            return false;
        }
        return flightRecordMapper.deleteById(id) > 0;
    }
}
