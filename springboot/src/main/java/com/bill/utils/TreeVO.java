package com.bill.utils;

import lombok.Data;
import java.util.List;

@Data
public class TreeVO {
    private String id;
    private String name;
    private Integer type;
    private List<TreeVO> children;
}