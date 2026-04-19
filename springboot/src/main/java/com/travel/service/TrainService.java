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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TrainService {

    @Autowired
    private TrainRecordMapper trainRecordMapper;

    @Autowired
    private TrainStationRecordMapper trainStationRecordMapper;

    // ====================== 列表 ======================
    public Object list(TrainRecordQueryDTO dto) {
        LambdaQueryWrapper<TrainRecord> qw = Wrappers.lambdaQuery();

        qw.eq(StringUtils.hasText(dto.getTrainNo()), TrainRecord::getTrainNo, dto.getTrainNo());
        qw.eq(StringUtils.hasText(dto.getStartStation()), TrainRecord::getStartStation, dto.getStartStation());
        qw.eq(StringUtils.hasText(dto.getEndStation()), TrainRecord::getEndStation, dto.getEndStation());
        qw.ge(dto.getDepartureDatetimeStart() != null, TrainRecord::getDepartureDatetime, dto.getDepartureDatetimeStart());
        qw.le(dto.getDepartureDatetimeEnd() != null, TrainRecord::getDepartureDatetime, dto.getDepartureDatetimeEnd());
        qw.orderByDesc(TrainRecord::getDepartureDatetime);

        // 分页
        if (dto.getPageNum() != null && dto.getPageSize() != null) {
            Page<TrainRecord> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            IPage<TrainRecord> pageResult = trainRecordMapper.selectPage(page, qw);
            fillStationCount(pageResult.getRecords());
            return pageResult;
        }

        // 不分页
        List<TrainRecord> list = trainRecordMapper.selectList(qw);
        fillStationCount(list);
        return list;
    }

    // ====================== 单条详情 ======================
    public TrainRecord get(Long trainId) {
        TrainRecord train = trainRecordMapper.selectById(trainId);
        if (train == null) return null;

        // 赋值途径站数量
        Long count = trainStationRecordMapper.selectCount(
                Wrappers.lambdaQuery(TrainStationRecord.class)
                        .eq(TrainStationRecord::getTrainId, trainId)
        );
        train.setStationCount(count.intValue());
        return train;
    }

    // ====================== 新增 ======================
    public int insert(TrainRecord record) {
        return trainRecordMapper.insert(record);
    }

    // ====================== 修改 ======================
    public int update(TrainRecord record) {
        return trainRecordMapper.updateById(record);
    }

    // ====================== 删除 ======================
    @Transactional
    public int delete(Long trainId) {
        // 先删子表
        trainStationRecordMapper.delete(
                Wrappers.lambdaQuery(TrainStationRecord.class)
                        .eq(TrainStationRecord::getTrainId, trainId)
        );
        // 再删主表
        return trainRecordMapper.deleteById(trainId);
    }

    // ====================== 批量赋值途经站数 ======================
    private void fillStationCount(List<TrainRecord> list) {
        for (TrainRecord record : list) {
            Long cnt = trainStationRecordMapper.selectCount(
                    Wrappers.lambdaQuery(TrainStationRecord.class)
                            .eq(TrainStationRecord::getTrainId, record.getTrainId())
            );
            record.setStationCount(cnt.intValue());
        }
    }
}