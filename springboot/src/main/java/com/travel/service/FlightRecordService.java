package com.travel.service;

import com.travel.entity.FlightRecord;
import com.travel.mapper.FlightRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightRecordService {

    @Autowired
    private FlightRecordMapper flightRecordMapper;

    public List<FlightRecord> findAll() {
        return flightRecordMapper.selectList(null);
    }
}
