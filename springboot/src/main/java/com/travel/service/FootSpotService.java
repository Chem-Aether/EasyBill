package com.travel.service;

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

    public List<Map<String, Object>> findAllWithRegion() {
        List<FootSpot> spots = findAll();
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
            row.put("visitTime", spot.getVisitTime());
            row.put("longitude", spot.getLongitude());
            row.put("latitude", spot.getLatitude());
            row.put("address", spot.getAddress());
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

    public Map<String, Integer> statsProvinceCityDistrictCount() {
        // 1. 查询所有足迹（只查 adcode 即可）
        List<FootSpot> list = footSpotMapper.selectList(null);

        // 2. 统计 省（前2位）
        long provinceCount = list.stream()
                .map(FootSpot::getAdcode)
                .filter(code -> code != null && code.length() >= 6)
                .map(code -> code.substring(0, 2))
                .distinct().count();

        // 3. 统计 市（前4位）
        long cityCount = list.stream()
                .map(FootSpot::getAdcode)
                .filter(code -> code != null && code.length() >= 6)
                .map(this::getCityCode)
                .distinct().count();

        // 4. 统计 区/县（完整6位）
        long districtCount = list.stream()
                .map(FootSpot::getAdcode)
                .filter(code -> code != null && code.length() >= 6)
                .distinct().count();

        // 5. 返回结果
        Map<String, Integer> result = new HashMap<>();
        result.put("province", (int) provinceCount); // 省数量
        result.put("city", (int) cityCount);         // 市数量
        result.put("area", (int) districtCount);     // 区数量

        return result;
    }

    public List<String> getVisitedCityCodes() {
        List<FootSpot> list = footSpotMapper.selectList(null);
        if (list.isEmpty()) return new ArrayList<>();
        return list.stream()
                .map(FootSpot::getAdcode)
                .filter(Objects::nonNull)
                .map(this::getCityCode)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private String getCityCode(String adcode) {
        if (adcode == null || adcode.length() < 6) {
            return adcode;
        }

        // 直辖市：11、31、12、50 → 取前 2 位
        String first2 = adcode.substring(0, 2);
        if (first2.equals("11") || first2.equals("31") ||
                first2.equals("12") || first2.equals("50")) {
            return first2;
        }

        // 普通市 → 取前 4 位
        return adcode.substring(0, 4);
    }

    private void validate(FootSpot spot) {
        if (spot == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "足迹数据不能为空");
        if (spot.getSpotName() == null || spot.getSpotName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "地点名称不能为空");
        }
        if (spot.getSpotType() == null || spot.getSpotType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "地点类型不能为空");
        }
        if (spot.getLongitude() == null || spot.getLatitude() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请在地图上选择地点");
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
