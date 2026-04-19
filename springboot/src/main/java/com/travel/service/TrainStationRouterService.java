package com.travel.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.TrainStationRecord;
import com.travel.mapper.TrainStationRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainStationRouterService extends ServiceImpl<TrainStationRecordMapper, TrainStationRecord> {

    public List<TrainStationRecord> listByTrainId(Long trainId) {
        return list(Wrappers.lambdaQuery(TrainStationRecord.class)
                .eq(TrainStationRecord::getTrainId, trainId)
                .orderByAsc(TrainStationRecord::getStationOrder));
    }

    public void removeByTrainId(Long trainId) {
        if (trainId == null) return;
        remove(Wrappers.lambdaQuery(TrainStationRecord.class).eq(TrainStationRecord::getTrainId, trainId));
    }

    @Transactional
    public void saveBatch(Long trainId, Long userId, List<TrainStationRecord> list) {
        removeByTrainId(trainId);

        if (list == null || list.isEmpty()) return;

        for (int i = 0; i < list.size(); i++) {
            TrainStationRecord st = list.get(i);
            st.setTrainId(trainId);
            if (st.getUserId() == null) {
                st.setUserId(userId);
            }
            st.setStationOrder(i + 1);
        }
        saveBatch(list);
    }

    @Transactional
    public void saveBatch(Long trainId, List<TrainStationRecord> list) {
    // 兼容旧调用：不传 userId 时仅设置 trainId + stationOrder
    removeByTrainId(trainId);

        if (list == null || list.isEmpty()) return;

        for (int i = 0; i < list.size(); i++) {
            TrainStationRecord st = list.get(i);
            st.setTrainId(trainId);
            st.setStationOrder(i + 1);
        }
        saveBatch(list);
    }
}