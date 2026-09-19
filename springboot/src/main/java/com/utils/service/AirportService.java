package com.utils.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.utils.entity.Airport;
import com.utils.mapper.AirportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AirportService {
    private static final int SEARCH_LIMIT = 20;
    private final AirportMapper airportMapper;

    public List<Map<String, Object>> getAllAirports() {
        return airportMapper.selectList(baseQuery()).stream().map(this::toMap).toList();
    }

    public List<Map<String, Object>> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();
        String value = keyword.trim();
        LambdaQueryWrapper<Airport> query = baseQuery()
                .and(wrapper -> wrapper
                        .like(Airport::getName, value)
                        .or().like(Airport::getIcao, value.toUpperCase())
                        .or().like(Airport::getIata, value.toUpperCase())
                        .or().like(Airport::getCity, value))
                .last("LIMIT " + SEARCH_LIMIT);
        return airportMapper.selectList(query).stream().map(this::toMap).toList();
    }

    private LambdaQueryWrapper<Airport> baseQuery() {
        return new LambdaQueryWrapper<Airport>().select(
                Airport::getIcao, Airport::getIata, Airport::getName,
                Airport::getCity, Airport::getAttr,
                Airport::getLongitude, Airport::getLatitude
        );
    }

    private Map<String, Object> toMap(Airport airport) {
        Map<String, Object> result = new HashMap<>();
        result.put("icao", airport.getIcao());
        result.put("iata", airport.getIata());
        result.put("name", airport.getName());
        result.put("city", airport.getCity());
        result.put("attr", airport.getAttr());
        result.put("longitude", airport.getLongitude());
        result.put("latitude", airport.getLatitude());
        return result;
    }
}
