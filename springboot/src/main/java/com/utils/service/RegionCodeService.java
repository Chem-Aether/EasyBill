package com.utils.service;

import com.utils.entity.RegionCode;
import com.utils.mapper.RegionCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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