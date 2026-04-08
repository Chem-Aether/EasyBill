package com.travel.service;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.travel.entity.TrainRecord;
import com.travel.entity.FlightRecord;
import com.travel.mapper.FlightRecordMapper;
import com.travel.mapper.TrainRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;



@Service
public class TicketService {

    @Autowired
    private TrainRecordMapper trainRecordMapper;
    @Autowired
    private FlightRecordMapper flightRecordMapper;

    public List<Map<String, Object>> getTicketList(String type) {
        List<Map<String, Object>> trainList = new ArrayList<>();
        List<Map<String, Object>> flightList = new ArrayList<>();

        boolean isAll = type == null || type.isEmpty() || "all".equals(type);
        boolean isTrain = "train".equals(type);
        boolean isPlane = "flight".equals(type);

        // 获取数据
        if (isAll || isTrain) {
            trainList = getTrainTickets();
        }
        if (isAll || isPlane) {
            flightList = getFlightTickets();
        }

        // 🔥 关键：all模式下，合并+混合排序
        if (isAll) {
            List<Map<String, Object>> allList = new ArrayList<>();
            allList.addAll(trainList);
            allList.addAll(flightList);

            // 统一按 出发时间 倒序排序（最新在前，空时间排最后）
            allList.sort(
                    Comparator.comparing(
                            (Map<String, Object> map) -> (LocalDateTime) map.get("Time"),
                            Comparator.nullsLast(Comparator.reverseOrder())
                    )
            );

            // 可选：移除临时排序字段，不影响前端返回格式
            allList.forEach(map -> map.remove("Time"));
            return allList;
        }

        // 单类型直接返回
        return isTrain ? trainList : flightList;
    }

    public List<Map<String, Object>> getTravelStatistics() {
        List<Map<String, Object>> list = trainRecordMapper.selectMaps(
                Wrappers.<TrainRecord>query()
                        .groupBy("train_type")
                        .orderByAsc("train_type")
                        .select("train_type as name", "count(*) as value")
        );

        return list;
    }

    // ====================== 火车格式化（添加临时排序时间） ======================
    private List<Map<String, Object>> getTrainTickets() {
        List<TrainRecord> list = trainRecordMapper.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();

        for (TrainRecord t : list) {
            Map<String, Object> item = new HashMap<>();
            Map<String, Object> more = new HashMap<>();

            item.put("Number", t.getTrainNo());
            item.put("From", t.getStartStation());
            item.put("To", t.getEndStation());
            item.put("time", calculateDuration(t.getDepartureDatetime(), t.getArrivalDatetime()));
            item.put("Time", t.getDepartureDatetime());

            more.put("发车时间", formatTime(t.getDepartureDatetime()));
            more.put("到达时间", formatTime(t.getArrivalDatetime()));
            more.put("铁路类型", t.getTrainType());
            more.put("车型", t.getTrainModel() == null ? "" : t.getTrainModel());
            more.put("里程/km", t.getMileageKm() == null ? "" : t.getMileageKm().toString());

            item.put("more", more);
            result.add(item);
        }
        return result;
    }

    // ====================== 航班格式化（添加临时排序时间） ======================
    private List<Map<String, Object>> getFlightTickets() {
        List<FlightRecord> list = flightRecordMapper.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();

        for (FlightRecord f : list) {
            Map<String, Object> item = new HashMap<>();
            Map<String, Object> more = new HashMap<>();

            item.put("Number", f.getFlightNo());
            item.put("From", f.getDepartureIcao());
            item.put("To", f.getArrivalIcao());
            item.put("time", calculateDuration(f.getTakeoffTime(), f.getLandingTime()));
            // 🔥 临时添加统一排序时间（仅内部排序用）
            item.put("Time", f.getTakeoffTime());

            more.put("起点", f.getDepartureAirport());
            more.put("终点", f.getArrivalAirport());
            more.put("起飞时间", formatTime(f.getTakeoffTime()));
            more.put("降落时间", formatTime(f.getLandingTime()));
            more.put("注册号", f.getAircraftReg() == null ? "" : f.getAircraftReg());
            more.put("机型", f.getAircraftType() == null ? "" : f.getAircraftType());
            more.put("里程/km", f.getFlightDistanceKm() == null ? "" : f.getFlightDistanceKm().toString());
            more.put("经停", f.getStopoverAirport() == null ? "" : f.getStopoverAirport());

            item.put("more", more);
            result.add(item);
        }
        return result;
    }

    // ====================== 工具方法 ======================
    private String formatTime(LocalDateTime time) {
        if (time == null) return "";
        return time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    private String calculateDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return "";
        long total = ChronoUnit.MINUTES.between(start, end);
        return total <= 0 ? "0min" : String.format("%dh%02dmin", total/60, total%60).replace("0h","").replace("00min","");
    }
}