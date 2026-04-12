package com.travel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.entity.FootSpot;
import com.travel.mapper.FootSpotMapper;
import com.utils.entity.RegionCode;
import com.utils.mapper.RegionCodeMapper;
import com.utils.service.RegionCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class FootSpotService {

    @Autowired
    private FootSpotMapper footSpotMapper;

    @Autowired
    private RegionCodeMapper regionCodeMapper;

    public List<FootSpot> findAll() {
        return footSpotMapper.selectList(null);
    }

    public Map<String, Integer> statsProvinceCityDistrictCount() {
        // 1. 查询所有足迹（只查 adcode 即可）
        List<FootSpot> list = footSpotMapper.selectList(null);

        // 2. 统计 省（前2位）
        long provinceCount = list.stream()
                .map(spot -> spot.getAdcode().substring(0, 2))
                .distinct().count();

        // 3. 统计 市（前4位）
        long cityCount = list.stream()
                .map(spot -> spot.getAdcode().substring(0, 4))
                .distinct().count();

        // 4. 统计 区/县（完整6位）
        long districtCount = list.stream()
                .map(FootSpot::getAdcode)
                .distinct().count();

        // 5. 返回结果
        Map<String, Integer> result = new HashMap<>();
        result.put("province", (int) provinceCount); // 省数量
        result.put("city", (int) cityCount);         // 市数量
        result.put("area", (int) districtCount);     // 区数量

        return result;
    }

    public List<String> getVisitedCities() {
        // 1. 获取所有足迹
        List<FootSpot> list = footSpotMapper.selectList(null);
        if (list.isEmpty()) return new ArrayList<>();

        // 2. 提取所有 adcode，并取出【城市级别的 code】
        Set<String> cityCodeSet = list.stream()
                .map(FootSpot::getAdcode)
                .map(this::getCityCode) // 核心：自动获取市级code
                .collect(Collectors.toSet());

        // 3. 根据城市code批量查询名称
        List<RegionCode> cityList = regionCodeMapper.selectBatchIds(cityCodeSet);

        // 4. 返回名称
        return cityList.stream()
                .map(RegionCode::getName)
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
}
