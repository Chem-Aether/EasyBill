package com.travel.service;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.mapserver.GeoReferenceClient;
import com.mapserver.GeoReferenceClient.GeoRegion;
import com.travel.entity.FootSpot;
import com.travel.mapper.FootSpotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;


@Service
@DS("travel")
public class FootSpotService {

    @Autowired
    private FootSpotMapper footSpotMapper;

    @Autowired
    private GeoReferenceClient geoReferenceClient;

    public List<FootSpot> findAll() {
        return footSpotMapper.selectList(null).stream()
                .sorted(Comparator.comparing(FootSpot::getVisitTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> findAllWithRegion(Integer year) {
        List<FootSpot> spots = year == null ? findAll() : footSpotMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<FootSpot>lambdaQuery()
                        .ge(FootSpot::getVisitTime, java.time.LocalDate.of(year, 1, 1))
                        .lt(FootSpot::getVisitTime, java.time.LocalDate.of(year + 1, 1, 1))
                        .orderByDesc(FootSpot::getVisitTime));
        Map<String, GeoRegion> regions = geoReferenceClient.regionsByCode(spots.stream()
                .map(FootSpot::getAdcode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        return spots.stream().map(spot -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("spotId", spot.getSpotId());
            row.put("userId", spot.getUserId());
            row.put("adcode", spot.getAdcode());
            GeoRegion region = regions.get(spot.getAdcode());
            row.put("regionName", region == null || region.fullName() == null ? "未知地区" : region.fullName());
            row.put("spotName", spot.getSpotName());
            row.put("spotType", spot.getSpotType());
            row.put("visitType", spot.getVisitType());
            row.put("visitTime", spot.getVisitTime());
            row.put("longitude", spot.getLongitude());
            row.put("latitude", spot.getLatitude());
            row.put("travelNote", spot.getTravelNote());
            row.put("imageUrl", spot.getImageUrl());
            return row;
        }).collect(Collectors.toList());
    }

    public FootSpot add(FootSpot spot) {
        validate(spot);
        spot.setSpotId(null);
        if (spot.getUserId() == null) {
            spot.setUserId(1L);
        }
        footSpotMapper.insert(spot);
        return spot;
    }

    public FootSpot update(Long id, FootSpot spot) {
        if (id == null || footSpotMapper.selectById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "足迹记录不存在");
        }
        validate(spot);
        spot.setSpotId(id);
        footSpotMapper.updateById(spot);
        return footSpotMapper.selectById(id);
    }

    public void delete(Long id) {
        if (footSpotMapper.deleteById(id) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "足迹记录不存在");
        }
    }

    private void validate(FootSpot spot) {
        if (spot == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "足迹数据不能为空");
        if (spot.getSpotName() == null || spot.getSpotName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "地点名称不能为空");
        }
        if (spot.getSpotType() == null || spot.getSpotType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "地点类型不能为空");
        }
        if (!"transit".equals(spot.getVisitType())) {
            spot.setVisitType("travel");
        }
        boolean hasLongitude = spot.getLongitude() != null;
        boolean hasLatitude = spot.getLatitude() != null;
        if (hasLongitude != hasLatitude) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "经纬度必须同时填写或同时留空");
        }
        if (!hasLongitude) {
            if (spot.getAdcode() == null || spot.getAdcode().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "未定位地点必须保留所属行政区");
            }
            return;
        }
        if (spot.getLongitude().doubleValue() < -180 || spot.getLongitude().doubleValue() > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "经度必须在 -180 到 180 之间");
        }
        if (spot.getLatitude().doubleValue() < -90 || spot.getLatitude().doubleValue() > 90) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "纬度必须在 -90 到 90 之间");
        }
        GeoReferenceClient.GeoLocation location = geoReferenceClient.reverseGeocode(
                spot.getLongitude().doubleValue(), spot.getLatitude().doubleValue());
        if (location == null || location.district() == null || location.district().code() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该坐标未匹配到区县级行政区");
        }
        spot.setAdcode(location.district().code());
    }

}
