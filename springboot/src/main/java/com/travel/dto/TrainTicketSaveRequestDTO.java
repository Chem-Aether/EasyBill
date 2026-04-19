package com.travel.dto;

import com.travel.entity.TrainRecord;
import com.travel.entity.TrainStationRecord;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 保存用请求：新增/修改车票，同时保存其途径站（最终态列表）。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainTicketSaveRequestDTO {
    private TrainRecord ticket;
    private List<TrainStationRecord> stations;
}
