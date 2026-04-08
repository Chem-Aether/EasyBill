package com.travel.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.TrainRecord;
import com.travel.mapper.TrainRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainRecordService  extends ServiceImpl<TrainRecordMapper, TrainRecord> {

    @Autowired
    private TrainRecordMapper trainRecordMapper;

    public List<TrainRecord> findAll() {
        return trainRecordMapper.selectList(null);
    }


    public List<Map<String, Object>> listTrainFormatted() {
        List<TrainRecord> trainList = trainRecordMapper.selectList(null);

        List<Map<String, Object>> resultList = new ArrayList<>();

        for (TrainRecord t : trainList) {
            Map<String, Object> item = new HashMap<>();
            Map<String, Object> more = new HashMap<>();

            item.put("Number", t.getTrainNo());
            item.put("From", t.getStartStation());
            item.put("To", t.getEndStation());
            item.put("time", calculateDuration(t.getDepartureDatetime(), t.getArrivalDatetime()));

            more.put("发车时间", formatTime(t.getDepartureDatetime()));
            more.put("达到时间", formatTime(t.getArrivalDatetime()));
            more.put("铁路类型", t.getTrainType());
            more.put("车型", t.getTrainModel());
            more.put("里程/km", t.getMileageKm() == null ? "" : t.getMileageKm().toString());

            item.put("more", more);
            resultList.add(item);
        }


        return  resultList;
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) return "";
        return time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    // 计算时长 hh:mm
    private String calculateDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return "";
        long minutes = ChronoUnit.MINUTES.between(start, end);
        long h = minutes / 60;
        long m = minutes % 60;
        return h + "h" + m + "min";
    }
}
