package com.travel.dto;

import com.travel.entity.TrainStationRecord;
import lombok.Data;

import java.util.List;

@Data
public class TrainStationBatchDTO {
    private Long trainId;
    private List<TrainStationRecord> stationList;
}