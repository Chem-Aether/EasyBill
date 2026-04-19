package com.travel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.dto.TrainRecordQueryDTO;
import com.travel.entity.TrainRecord;
import com.travel.entity.TrainStationRecord;
import com.travel.mapper.TrainRecordMapper;
import com.travel.mapper.TrainStationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrainRecordService {

    @Autowired
    private TrainRecordMapper trainRecordMapper;

    @Autowired
    private TrainStationRecordMapper trainStationRecordMapper;

    public Object getTrainWithStationList(TrainRecordQueryDTO dto) {
        // 拼接查询条件
        LambdaQueryWrapper<TrainRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(StringUtils.hasText(dto.getTrainNo()), TrainRecord::getTrainNo, dto.getTrainNo());
        wrapper.eq(StringUtils.hasText(dto.getStartStation()), TrainRecord::getStartStation, dto.getStartStation());
        wrapper.eq(StringUtils.hasText(dto.getEndStation()), TrainRecord::getEndStation, dto.getEndStation());
        wrapper.ge(dto.getDepartureDatetimeStart() != null, TrainRecord::getDepartureDatetime, dto.getDepartureDatetimeStart());
        wrapper.le(dto.getDepartureDatetimeEnd() != null, TrainRecord::getDepartureDatetime, dto.getDepartureDatetimeEnd());
        wrapper.orderByDesc(TrainRecord::getDepartureDatetime);

        // 分页
        if (dto.getPageNum() != null && dto.getPageSize() != null) {
            // 分页查询
            Page<TrainRecord> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            IPage<TrainRecord> pageResult = trainRecordMapper.selectPage(page, wrapper);

            // 组装途经站
            assembleStationList(pageResult.getRecords());
            return pageResult; // 返回分页对象
        } else {
            // 不分页，查全部
            List<TrainRecord> list = trainRecordMapper.selectList(wrapper);
            assembleStationList(list);
            return list;
        }
    }
    // 查询列车途径站
    private void assembleStationList(List<TrainRecord> trainList) {
        if (trainList == null || trainList.isEmpty()) {
            return;
        }

        // 批量查站点
        List<Long> trainIds = trainList.stream()
                .map(TrainRecord::getTrainId)
                .collect(Collectors.toList());

        List<TrainStationRecord> stationList = trainStationRecordMapper.selectList(
                Wrappers.lambdaQuery(TrainStationRecord.class)
                        .in(TrainStationRecord::getTrainId, trainIds)
                        .orderByAsc(TrainStationRecord::getStationOrder)
        );

        // 分组
        Map<Long, List<TrainStationRecord>> stationMap = stationList.stream()
                .collect(Collectors.groupingBy(TrainStationRecord::getTrainId));

    }

    // ===================== 新增 =====================
    public int add(TrainRecord record) {
        return trainRecordMapper.insert(record);
    }

    // ===================== 根据ID删除 =====================
    public int deleteById(Long id) {
        return trainRecordMapper.deleteById(id);
    }

    // ===================== 根据ID修改 =====================
    public int updateById(TrainRecord record) {
        return trainRecordMapper.updateById(record);
    }

    // ===================== 根据ID查询 =====================
    public TrainRecord getById(Long id) {
        TrainRecord record = trainRecordMapper.selectById(id);
        if (record == null) {
            return null;
        }

        LambdaQueryWrapper<TrainStationRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TrainStationRecord::getTrainId, id);
        wrapper.orderByAsc(TrainStationRecord::getStationOrder);
        List<TrainStationRecord> stationList = trainStationRecordMapper.selectList(wrapper);

        return record;
    }
}