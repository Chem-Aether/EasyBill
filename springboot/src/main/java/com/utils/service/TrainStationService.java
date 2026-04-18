package com.utils.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.utils.entity.TrainStation;
import com.utils.mapper.TrainStationMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainStationService {

    private final TrainStationMapper trainStationMapper;

    // 缓存：只存 name + code
    private List<Map<String, Object>> stationCache;

    // 项目启动时加载
    @PostConstruct
    public void initCache() {
        LambdaQueryWrapper<TrainStation> wrapper = new LambdaQueryWrapper<>();
        // 只查 name 和 code！！！
        wrapper.select(TrainStation::getName, TrainStation::getCode);

        // 只封装 name + code，没有任何 null 字段
        stationCache = trainStationMapper.selectList(wrapper).stream()
                .map(station -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", station.getName());
                    map.put("code", station.getCode());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // 全量返回 name + code
    public List<Map<String, Object>> getAllStations() {
        return stationCache;
    }

    // 搜索：根据名称模糊匹配
    public List<Map<String, Object>> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        String kw = keyword.trim().toLowerCase();
        return stationCache.stream()
                .filter(station -> station.get("name").toString().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }
}