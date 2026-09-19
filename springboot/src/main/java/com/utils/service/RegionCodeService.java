package com.utils.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.utils.entity.RegionCode;
import com.utils.mapper.RegionCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class RegionCodeService {

    @Autowired
    private RegionCodeMapper regionCodeMapper;

    /**
     * 根据行政编码查询地区信息
     */
    public RegionCode getByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        // 根据主键 code 查询
        return regionCodeMapper.selectById(code);
    }

    public List<Map<String, Object>> search(String keyword, Integer level) {
        String text = keyword == null ? "" : keyword.trim();
        if (text.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<RegionCode> query = new LambdaQueryWrapper<RegionCode>()
                .like(RegionCode::getName, text)
                .orderByAsc(RegionCode::getCode)
                .last("LIMIT 20");
        if (level != null) {
            query.eq(RegionCode::getLevel, level);
        }

        return regionCodeMapper.selectList(query).stream().map(region -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("code", region.getCode());
            item.put("name", region.getName());
            item.put("level", region.getLevel());
            item.put("type", region.getType());
            item.put("parentCode", region.getParentCode());
            item.put("fullName", getFullRegionName(region.getCode()));
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 根据行政编码查询全名
     */
    public String getFullRegionName(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "";
        }

        RegionCode area = regionCodeMapper.selectById(code);
        if (area == null) {
            return "未知地区";
        }

        // 最终名称
        StringBuilder fullName = new StringBuilder();

        // 递归向上查省、市、区
        buildFullName(area, fullName);

        return fullName.toString();
    }

    // 递归拼接上级名称
    private void buildFullName(RegionCode area, StringBuilder fullName) {
        if (area == null) {
            return;
        }

        // 先递归上级（省 → 市 → 区）
        if (area.getParentCode() != null && !area.getParentCode().isEmpty() && !area.getParentCode().equals("0")) {
            RegionCode parent = regionCodeMapper.selectById(area.getParentCode());
            buildFullName(parent, fullName);
        }

        // 拼接当前名称
        if (area.getName() != null && !area.getName().isEmpty()) {
            fullName.append(area.getName());
        }
    }
}
