package com.utils.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.utils.entity.TrainStation;
import com.utils.mapper.TrainStationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TrainStationService {
    private static final int SEARCH_LIMIT = 20;
    private final TrainStationMapper trainStationMapper;

    public List<Map<String, Object>> getAllStations() {
        return trainStationMapper.selectList(baseQuery()).stream().map(this::toMap).toList();
    }

    public List<Map<String, Object>> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();
        String value = keyword.trim();
        LambdaQueryWrapper<TrainStation> query = baseQuery()
                .and(wrapper -> wrapper
                        .like(TrainStation::getName, value)
                        .or().like(TrainStation::getCode, value.toUpperCase())
                        .or().like(TrainStation::getCity, value))
                .last("LIMIT " + SEARCH_LIMIT);
        return trainStationMapper.selectList(query).stream().map(this::toMap).toList();
    }

    private LambdaQueryWrapper<TrainStation> baseQuery() {
        return new LambdaQueryWrapper<TrainStation>().select(
                TrainStation::getName, TrainStation::getCode, TrainStation::getCity,
                TrainStation::getProvince, TrainStation::getLongitude, TrainStation::getLatitude
        );
    }

    private Map<String, Object> toMap(TrainStation station) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", station.getName());
        result.put("code", station.getCode());
        result.put("city", station.getCity());
        result.put("province", station.getProvince());
        result.put("longitude", station.getLongitude());
        result.put("latitude", station.getLatitude());
        return result;
    }
}
