package com.utils.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.utils.mapper.AirportMapper;
import com.utils.entity.Airport;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportMapper airportMapper;

    // 缓存：用于前端选择器（只保留必要字段，避免全表字段占用内存）
    private List<Map<String, Object>> airportCache;

    // 项目启动时加载
    @PostConstruct
    public void initCache() {
        LambdaQueryWrapper<Airport> wrapper = new LambdaQueryWrapper<>();
        // 只查前端需要的字段：展示用 name + 主键 icao + 可选 iata/city/attr
        wrapper.select(Airport::getIcao, Airport::getName, Airport::getIata, Airport::getCity, Airport::getAttr);

        airportCache = airportMapper.selectList(wrapper).stream()
                .map(airport -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("icao", airport.getIcao());
                    map.put("name", airport.getName());
                    map.put("iata", airport.getIata());
                    map.put("city", airport.getCity());
                    map.put("attr", airport.getAttr());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // 全量返回 name + icao
    public List<Map<String, Object>> getAllAirports() {
        return airportCache;
    }

    // 搜索：根据名称或 ICAO 模糊匹配
    public List<Map<String, Object>> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        String kw = keyword.trim().toLowerCase();
        return airportCache.stream()
                .filter(a -> {
                    Object name = a.get("name");
                    Object icao = a.get("icao");
                    return (name != null && name.toString().toLowerCase().contains(kw))
                            || (icao != null && icao.toString().toLowerCase().contains(kw));
                })
                .collect(Collectors.toList());
    }
}
