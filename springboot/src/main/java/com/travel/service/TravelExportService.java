package com.travel.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.travel.mapper.FlightRecordMapper;
import com.travel.mapper.FootSpotMapper;
import com.travel.mapper.TrainRecordMapper;
import com.travel.mapper.TrainStationRecordMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@DS("travel")
public class TravelExportService {
    private final FlightRecordMapper flightMapper;
    private final TrainRecordMapper trainMapper;
    private final TrainStationRecordMapper stationMapper;
    private final FootSpotMapper footSpotMapper;

    public TravelExportService(FlightRecordMapper flightMapper, TrainRecordMapper trainMapper,
                               TrainStationRecordMapper stationMapper, FootSpotMapper footSpotMapper) {
        this.flightMapper = flightMapper;
        this.trainMapper = trainMapper;
        this.stationMapper = stationMapper;
        this.footSpotMapper = footSpotMapper;
    }

    public byte[] exportAll() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            writeSheet(workbook, "足迹", footSpotMapper.selectMaps(null));
            writeSheet(workbook, "航班", flightMapper.selectMaps(null));
            writeSheet(workbook, "铁路", trainMapper.selectMaps(null));
            writeSheet(workbook, "铁路途经站", stationMapper.selectMaps(null));
            workbook.write(output);
            return output.toByteArray();
        }
    }

    private void writeSheet(Workbook workbook, String name, List<Map<String, Object>> rows) {
        Sheet sheet = workbook.createSheet(name);
        if (rows.isEmpty()) return;
        List<String> columns = new ArrayList<>(rows.get(0).keySet());
        Row header = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        for (int index = 0; index < columns.size(); index++) {
            Cell cell = header.createCell(index);
            cell.setCellValue(columns.get(index));
            cell.setCellStyle(headerStyle);
        }
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1);
            Map<String, Object> values = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                Object value = values.get(columns.get(columnIndex));
                row.createCell(columnIndex).setCellValue(value == null ? "" : String.valueOf(value));
            }
        }
        sheet.createFreezePane(0, 1);
        for (int index = 0; index < columns.size(); index++) sheet.setColumnWidth(index, 18 * 256);
    }
}
