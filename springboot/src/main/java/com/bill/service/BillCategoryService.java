package com.bill.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bill.entity.BillCategory;
import com.bill.mapper.BillCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BillCategoryService {
    @Autowired
    private BillCategoryMapper billCategoryMapper;

    public List<BillCategory> listCategories() {
        return billCategoryMapper.findAllActive();
    }

    public List<Map<String, Object>> getCategoryTree() {
        // 1. 查询所有有效分类
        LambdaQueryWrapper<BillCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BillCategory::getIsDeleted, 0);
        List<BillCategory> list = billCategoryMapper.selectList(wrapper);

        // 2. 把所有节点转成 Map 并缓存 id -> node
        Map<String, Map<String, Object>> nodeMap = new HashMap<>();
        for (BillCategory c : list) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", c.getCateId());
            node.put("name", c.getClassName());
            node.put("type", c.getType());
            node.put("icon", c.getIcon());
            nodeMap.put(c.getCateId(), node);
        }

        List<Map<String, Object>> tree = new ArrayList<>();

        // 3. 核心：按你的规则自动构建树 ✅
        for (BillCategory c : list) {
            String selfId = c.getCateId();
            Map<String, Object> current = nodeMap.get(selfId);
            String parentId = selfId.substring(0, 2) + "00";

            // 如果自己就是父节点（以00结尾），加入顶级
            if (selfId.endsWith("00")) {
                tree.add(current);
            }
            else {
                // 找到父，挂进去
                Map<String, Object> parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    List<Map<String, Object>> children =
                            (List<Map<String, Object>>) parentNode.getOrDefault("children", new ArrayList<>());
                    children.add(current);
                    parentNode.put("children", children);
                }
            }
        }
        return tree;
    }

    public BillCategory getCategoryById(String cateId) {
        return billCategoryMapper.selectById(cateId);
    }

    public void createCategory(BillCategory category) {
        if (category == null || category.getCateId() == null || category.getCateId().isBlank()) {
            throw new RuntimeException("分类编码不能为空");
        }
        if (category.getClassName() == null || category.getClassName().isBlank()) {
            throw new RuntimeException("分类名称不能为空");
        }
        if (category.getType() == null) {
            throw new RuntimeException("分类类型不能为空");
        }
        category.setIsDeleted(0);
        category.setLevel(category.getLevel() == null ? 2 : category.getLevel());
        billCategoryMapper.insert(category);
    }

    public void updateCategory(BillCategory category) {
        if (category == null || category.getCateId() == null || category.getCateId().isBlank()) {
            throw new RuntimeException("分类编码不能为空");
        }
        billCategoryMapper.updateById(category);
    }

    public void deleteCategory(String cateId) {
        if (cateId == null || cateId.isBlank()) {
            throw new RuntimeException("分类编码不能为空");
        }
        billCategoryMapper.softDelete(cateId);
    }
}
