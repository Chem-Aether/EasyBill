package com.bill.controller;

import com.bill.entity.BillCategory;
import com.bill.service.BillCategoryService;
import com.sysconfig.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bill")
public class BillCategoryController {
    @Autowired
    private BillCategoryService billCategoryService;

    @GetMapping("/categories")
    @Operation(summary = "获取可用账单分类")
    public ResponseEntity<Result> listCategories() {
        return ResponseEntity.ok(
                Result.success(billCategoryService.listCategories())
        );
    }

    @GetMapping("/tree")
    public ResponseEntity<Result> tree() {
        return ResponseEntity.ok(
                Result.success(billCategoryService.getCategoryTree())
        );
    }

    @PostMapping("/category")
    @Operation(summary = "新增账单分类")
    public ResponseEntity<Result> createCategory(@RequestBody BillCategory category) {
        try {
            billCategoryService.createCategory(category);
            return ResponseEntity.ok(Result.success("分类创建成功"));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Result.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/category")
    @Operation(summary = "更新账单分类")
    public ResponseEntity<Result> updateCategory(@RequestBody BillCategory category) {
        try {
            billCategoryService.updateCategory(category);
            return ResponseEntity.ok(Result.success("分类更新成功"));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Result.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/category/{cateId}")
    @Operation(summary = "删除账单分类")
    public ResponseEntity<Result> removeCategory(@PathVariable String cateId) {
        try {
            billCategoryService.deleteCategory(cateId);
            return ResponseEntity.ok(Result.success("分类已删除"));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Result.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
