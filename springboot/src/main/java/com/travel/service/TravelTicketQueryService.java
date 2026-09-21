package com.travel.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mapserver.GeoReferenceClient;
import com.mapserver.GeoReferenceClient.GeoAirport;
import com.mapserver.GeoReferenceClient.GeoStation;
import com.travel.entity.TrainRecord;
import com.travel.entity.FlightRecord;
import com.travel.entity.TrainStationRecord;
import com.travel.mapper.FlightRecordMapper;
import com.travel.mapper.TrainRecordMapper;
import com.travel.mapper.TrainStationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;



@Service
@DS("travel")
public class TravelTicketQueryService {

    @Autowired
    private TrainRecordMapper trainRecordMapper;
    @Autowired
    private FlightRecordMapper flightRecordMapper;
    @Autowired
    private TrainStationRecordMapper trainStationRecordMapper;
    @Autowired
    private GeoReferenceClient geoReferenceClient;

    public List<Map<String, Object>> findTickets(String type, Integer year) {
        validateFilter(type, year, true);
        List<Map<String, Object>> trainList = new ArrayList<>();
        List<Map<String, Object>> flightList = new ArrayList<>();

        boolean isAll = type == null || type.isEmpty() || "all".equals(type);
        boolean isTrain = "train".equals(type);
        boolean isPlane = "flight".equals(type);

        // 获取数据
        if (isAll || isTrain) {
            trainList = getTrainTickets(year);
        }
        if (isAll || isPlane) {
            flightList = getFlightTickets(year);
        }

        if (isAll) {
            List<Map<String, Object>> allList = new ArrayList<>();
            allList.addAll(trainList);
            allList.addAll(flightList);
            allList.sort(
                    Comparator.comparing(
                            (Map<String, Object> map) -> (LocalDateTime) map.get("Time"),
                            Comparator.nullsLast(Comparator.reverseOrder())
                    )
            );
            allList.forEach(map -> map.remove("Time"));
            return allList;
        }
        return isTrain ? trainList : flightList;
    }

    private List<Map<String, Object>> buildDistribution(String type, Integer year) {
        if ("train".equals(type)) {
            var query = Wrappers.<TrainRecord>query()
                    .ge(year != null, "departure_datetime", year + "-01-01")
                    .lt(year != null, "departure_datetime", (year == null ? 0 : year + 1) + "-01-01")
                            .groupBy("train_type")
                            .select("train_type AS name", "COUNT(*) AS value");
            return trainRecordMapper.selectMaps(query);
        }

        if ("flight".equals(type)) {
            var query = Wrappers.<FlightRecord>query()
                    .ge(year != null, "takeoff_time", year + "-01-01")
                    .lt(year != null, "takeoff_time", (year == null ? 0 : year + 1) + "-01-01")
                            .groupBy("LEFT(flight_no, 2)")
                            .select("LEFT(flight_no, 2) AS name", "COUNT(*) AS value");
            return flightRecordMapper.selectMaps(query);
        }

        return new ArrayList<>();
    }

    private List<Map<String, Object>> buildMetrics(String type, Integer year) {
        List<Map<String, Object>> dashboard = new ArrayList<>();

        if ("train".equals(type)) {
            // 火车统计
            Map<String, Object> stats = trainRecordMapper.selectMaps(Wrappers.<TrainRecord>query()
                    .ge(year != null, "departure_datetime", year + "-01-01")
                    .lt(year != null, "departure_datetime", (year == null ? 0 : year + 1) + "-01-01")
                    .select("COUNT(*) AS totalCount",
                            "IFNULL(SUM(mileage_km), 0) AS totalMileage",
                            "IFNULL(SUM(TIMESTAMPDIFF(MINUTE, departure_datetime, arrival_datetime)), 0) AS totalMinutes")
            ).get(0);

            List<Long> filteredTrainIds = year == null ? List.of() : trainRecordMapper.selectList(
                    Wrappers.<TrainRecord>lambdaQuery()
                            .select(TrainRecord::getTrainId)
                            .ge(TrainRecord::getDepartureDatetime, LocalDateTime.of(year, 1, 1, 0, 0))
                            .lt(TrainRecord::getDepartureDatetime, LocalDateTime.of(year + 1, 1, 1, 0, 0)))
                    .stream().map(TrainRecord::getTrainId).toList();
            int stationCount = year != null && filteredTrainIds.isEmpty() ? 0 : trainStationRecordMapper.selectObjs(
                    Wrappers.<TrainStationRecord>lambdaQuery()
                            .select(TrainStationRecord::getStationName)
                            .in(year != null, TrainStationRecord::getTrainId, filteredTrainIds)
                            .groupBy(TrainStationRecord::getStationName)
            ).size();

            long count = ((Number) stats.get("totalCount")).longValue();
            long mileage = ((Number) stats.get("totalMileage")).longValue();
            long minutes = ((Number) stats.get("totalMinutes")).longValue();

            dashboard.add(Map.of("label", "里程", "value", mileage + " km"));
            dashboard.add(Map.of("label", "时长", "value", formatTime(minutes)));
            dashboard.add(Map.of("label", "次数", "value", count));
            dashboard.add(Map.of("label", "车站", "value", stationCount));
        }

        if ("flight".equals(type)) {
            // 航班统计
            Map<String, Object> stats = flightRecordMapper.selectMaps(Wrappers.<FlightRecord>query()
                    .ge(year != null, "takeoff_time", year + "-01-01")
                    .lt(year != null, "takeoff_time", (year == null ? 0 : year + 1) + "-01-01")
                    .select("COUNT(*) AS flightCount",
                            "IFNULL(SUM(flight_distance_km), 0) AS totalDistance",
                            "IFNULL(SUM(TIMESTAMPDIFF(MINUTE, takeoff_time, landing_time)), 0) AS totalMinutes",
                            "COUNT(DISTINCT departure_icao, arrival_icao) AS airportCount")
            ).get(0);

            long count = ((Number) stats.get("flightCount")).longValue();
            long distance = ((Number) stats.get("totalDistance")).longValue();
            long minutes = ((Number) stats.get("totalMinutes")).longValue();
            long airportCount = ((Number) stats.get("airportCount")).longValue();

            dashboard.add(Map.of("label", "航程", "value", distance + " km"));
            dashboard.add(Map.of("label", "航时", "value", formatTime(minutes)));
            dashboard.add(Map.of("label", "航次", "value", count));
            dashboard.add(Map.of("label", "航点", "value", airportCount));
        }

        return dashboard;
    }

    public Map<String, Object> getTicketSummary(String type, Integer year) {
        validateFilter(type, year, false);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("metrics", buildMetrics(type, year));
        summary.put("distribution", buildDistribution(type, year));
        return summary;
    }

    private void validateFilter(String mode, Integer year, boolean allowAll) {
        Set<String> modes = allowAll ? Set.of("all", "train", "flight") : Set.of("train", "flight");
        if (!modes.contains(mode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的旅行模式");
        }
        if (year != null && (year < 1900 || year > 2100)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "年份超出有效范围");
        }
    }

    // ====================== 火车格式化（添加临时排序时间） ======================
    private List<Map<String, Object>> getTrainTickets(Integer year) {
        List<TrainRecord> list = trainRecordMapper.selectList(Wrappers.<TrainRecord>lambdaQuery()
                .ge(year != null, TrainRecord::getDepartureDatetime, year == null ? null : LocalDateTime.of(year, 1, 1, 0, 0))
                .lt(year != null, TrainRecord::getDepartureDatetime, year == null ? null : LocalDateTime.of(year + 1, 1, 1, 0, 0)));
        List<Map<String, Object>> result = new ArrayList<>();

        List<Long> trainIds = list.stream()
                .map(TrainRecord::getTrainId)
                .filter(Objects::nonNull)
                .toList();
        List<TrainStationRecord> routeRecords = trainIds.isEmpty() ? List.of() :
                trainStationRecordMapper.selectList(Wrappers.<TrainStationRecord>lambdaQuery()
                        .in(TrainStationRecord::getTrainId, trainIds)
                        .orderByAsc(TrainStationRecord::getTrainId, TrainStationRecord::getStationOrder, TrainStationRecord::getId));
        Map<Long, List<TrainStationRecord>> routeIndex = routeRecords.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        TrainStationRecord::getTrainId,
                        LinkedHashMap::new,
                        java.util.stream.Collectors.toList()));

        Set<String> stationNames = new HashSet<>();
        list.forEach(record -> {
            if (record.getStartStation() != null) stationNames.add(record.getStartStation());
            if (record.getEndStation() != null) stationNames.add(record.getEndStation());
        });
        routeRecords.stream()
                .map(TrainStationRecord::getStationName)
                .filter(Objects::nonNull)
                .forEach(stationNames::add);
        Map<String, GeoStation> stationIndex = geoReferenceClient.stationsByName(stationNames);

        for (TrainRecord t : list) {
            Map<String, Object> item = new HashMap<>();
            List<Map<String, Object>> more = new ArrayList<>();

            item.put("Number", t.getTrainNo());
            item.put("trainId", t.getTrainId());
            item.put("From", t.getStartStation());
            item.put("To", t.getEndStation());
            item.put("time", calculateDuration(t.getDepartureDatetime(), t.getArrivalDatetime()));
            item.put("Time", t.getDepartureDatetime());
            GeoStation fromStation = stationIndex.get(t.getStartStation());
            GeoStation toStation = stationIndex.get(t.getEndStation());
            if (fromStation != null) {
                item.put("fromLongitude", fromStation.longitude());
                item.put("fromLatitude", fromStation.latitude());
            }
            if (toStation != null) {
                item.put("toLongitude", toStation.longitude());
                item.put("toLatitude", toStation.latitude());
            }

            List<String> orderedNames = new ArrayList<>();
            List<TrainStationRecord> ticketRoute = routeIndex.getOrDefault(t.getTrainId(), List.of());
            if (ticketRoute.isEmpty()) {
                addStationName(orderedNames, t.getStartStation());
                addStationName(orderedNames, t.getEndStation());
            } else {
                for (TrainStationRecord station : ticketRoute) {
                    addStationName(orderedNames, station.getStationName());
                }
            }

            List<Map<String, Object>> routeStations = new ArrayList<>();
            for (String stationName : orderedNames) {
                GeoStation station = stationIndex.get(stationName);
                if (station == null || station.longitude() == null || station.latitude() == null) continue;
                Map<String, Object> routeStation = new LinkedHashMap<>();
                routeStation.put("name", stationName);
                routeStation.put("longitude", station.longitude());
                routeStation.put("latitude", station.latitude());
                routeStations.add(routeStation);
            }
            item.put("routeStations", routeStations);

            more.add(Map.of("label", "发车时间", "value", formatTime(t.getDepartureDatetime())));
            more.add(Map.of("label", "到达时间", "value", formatTime(t.getArrivalDatetime())));
            more.add(Map.of("label", "铁路类型", "value", t.getTrainType()));
            more.add(Map.of("label", "车型", "value", t.getTrainModel() == null ? "" : t.getTrainModel()));
            more.add(Map.of("label", "里程/km", "value", t.getMileageKm() == null ? "" : t.getMileageKm().toString()));

            item.put("more", more);
            result.add(item);
        }
        return result;
    }

    private void addStationName(List<String> names, String stationName) {
        if (stationName == null || stationName.isBlank()) return;
        if (names.isEmpty() || !stationName.equals(names.get(names.size() - 1))) {
            names.add(stationName);
        }
    }

    private List<Map<String, Object>> getFlightTickets(Integer year) {
        List<FlightRecord> list = flightRecordMapper.selectList(Wrappers.<FlightRecord>lambdaQuery()
                .ge(year != null, FlightRecord::getTakeoffTime, year == null ? null : LocalDateTime.of(year, 1, 1, 0, 0))
                .lt(year != null, FlightRecord::getTakeoffTime, year == null ? null : LocalDateTime.of(year + 1, 1, 1, 0, 0)));
        List<Map<String, Object>> result = new ArrayList<>();

        Set<String> airportCodes = new HashSet<>();
        list.forEach(record -> {
            if (record.getDepartureIcao() != null) airportCodes.add(record.getDepartureIcao());
            if (record.getArrivalIcao() != null) airportCodes.add(record.getArrivalIcao());
        });
        Map<String, GeoAirport> airportIndex = geoReferenceClient.airportsByIcao(airportCodes);

        for (FlightRecord f : list) {
            Map<String, Object> item = new HashMap<>();
            List<Map<String, Object>> more = new ArrayList<>();

            item.put("Number", f.getFlightNo());
            item.put("flightId", f.getFlightId());
            item.put("From", f.getDepartureIcao());
            item.put("To", f.getArrivalIcao());
            item.put("time", calculateDuration(f.getTakeoffTime(), f.getLandingTime()));
            item.put("Time", f.getTakeoffTime());
            GeoAirport fromAirport = airportIndex.get(f.getDepartureIcao());
            GeoAirport toAirport = airportIndex.get(f.getArrivalIcao());
            if (fromAirport != null) {
                item.put("fromLongitude", fromAirport.longitude());
                item.put("fromLatitude", fromAirport.latitude());
            }
            if (toAirport != null) {
                item.put("toLongitude", toAirport.longitude());
                item.put("toLatitude", toAirport.latitude());
            }

            more.add(Map.of("label", "起飞时间", "value", formatTime(f.getTakeoffTime())));
            more.add(Map.of("label", "降落时间", "value", formatTime(f.getLandingTime())));
            more.add(Map.of("label", "起点", "value", f.getDepartureAirport()));
            more.add(Map.of("label", "终点", "value", f.getArrivalAirport()));
            more.add(Map.of("label", "注册号", "value", f.getAircraftReg() == null ? "" : f.getAircraftReg()));
            more.add(Map.of("label", "机型", "value", f.getAircraftType() == null ? "" : f.getAircraftType()));
            more.add(Map.of("label", "里程/km", "value", f.getFlightDistanceKm() == null ? "" : f.getFlightDistanceKm().toString()));
            more.add(Map.of("label", "经停", "value", f.getStopoverAirport() == null ? "" : f.getStopoverAirport()));

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

    private String formatTime(long minutes) {
        if (minutes <= 0) return "0h0m";
        long h = minutes / 60;
        long m = minutes % 60;
        return h + "h" + m + "m";
    }
}
