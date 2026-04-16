package com.travel.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.TrainRecord;
import com.travel.mapper.TrainRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;


@Service
public class TrainRecordService {

    // 直接注入 mapper
    @Autowired
    private TrainRecordMapper trainRecordMapper;

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
        return trainRecordMapper.selectById(id);
    }

    // ===================== 查询全部 =====================
    public List<TrainRecord> getAll() {
        return trainRecordMapper.selectList(null);
    }
}