package com.travel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.dto.TrainRecordQueryDTO;
import com.travel.dto.TrainTicketSaveRequestDTO;
import com.travel.entity.TrainRecord;
import com.travel.mapper.TrainRecordMapper;
import com.travel.mapper.TrainStationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 车票对外领域服务：接口分开，但写操作内部依然用“聚合事务”保证原子性。
 */
@Service
public class TrainTicketService {

    @Autowired
    private TrainRecordMapper trainRecordMapper;

    @Autowired
    private TrainStationRecordMapper trainStationRecordMapper;

    @Autowired
    private TrainStationRouterService trainStationRouterService;

    /**
     * 列表：只查车票 + 站点数量（不展开途径站列表）。
     */
    public Object listWithStationCount(TrainRecordQueryDTO dto) {
        LambdaQueryWrapper<TrainRecord> qw = Wrappers.lambdaQuery();
        qw.eq(StringUtils.hasText(dto.getTrainNo()), TrainRecord::getTrainNo, dto.getTrainNo());
        qw.eq(StringUtils.hasText(dto.getStartStation()), TrainRecord::getStartStation, dto.getStartStation());
        qw.eq(StringUtils.hasText(dto.getEndStation()), TrainRecord::getEndStation, dto.getEndStation());
        qw.ge(dto.getDepartureDatetimeStart() != null, TrainRecord::getDepartureDatetime, dto.getDepartureDatetimeStart());
        qw.le(dto.getDepartureDatetimeEnd() != null, TrainRecord::getDepartureDatetime, dto.getDepartureDatetimeEnd());
        qw.orderByDesc(TrainRecord::getDepartureDatetime);

    // 永远分页：不传分页参数则使用默认值，避免全量查询
    long pageNum = (dto.getPageNum() == null || dto.getPageNum() < 1) ? 1L : dto.getPageNum().longValue();
    long pageSize = (dto.getPageSize() == null || dto.getPageSize() < 1) ? 10L : dto.getPageSize().longValue();
    pageSize = Math.min(pageSize, 100L);

    Page<TrainRecord> page = new Page<>(pageNum, pageSize);
    IPage<TrainRecord> pageResult = trainRecordMapper.selectPage(page, qw);
    fillStationCount(pageResult.getRecords());
    return pageResult;
    }

    private void fillStationCount(List<TrainRecord> tickets) {
        if (tickets == null || tickets.isEmpty()) return;

        List<Long> trainIds = tickets.stream()
                .map(TrainRecord::getTrainId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, Integer> countMap = new HashMap<>();
        if (!trainIds.isEmpty()) {
            List<Map<String, Object>> rows = trainStationRecordMapper.countByTrainIds(trainIds);
            for (Map<String, Object> row : rows) {
                Object tid = row.get("trainId");
                Object cnt = row.get("cnt");
                if (tid != null && cnt != null) {
                    countMap.put(((Number) tid).longValue(), ((Number) cnt).intValue());
                }
            }
        }

        for (TrainRecord t : tickets) {
            t.setStationCount(countMap.getOrDefault(t.getTrainId(), 0));
        }
    }

    /**
     * 车票详情：只查车票。
     */
    public TrainRecord getTicket(Long trainId) {
        return trainRecordMapper.selectById(trainId);
    }

    /**
     * 保存：新增/修改车票，同时保存途径站（一个事务）。
    * 说明：为保持接口清晰，Controller 拆分了 add/update；
    * Service 仍保留通用方法以复用实现。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(TrainTicketSaveRequestDTO dto) {
        if (dto == null || dto.getTicket() == null) {
            throw new IllegalArgumentException("ticket不能为空");
        }

        TrainRecord ticket = dto.getTicket();
        boolean isCreate = (ticket.getTrainId() == null);

        if (isCreate) {
            trainRecordMapper.insert(ticket);
        } else {
            trainRecordMapper.updateById(ticket);
        }

        Long trainId = ticket.getTrainId();
    // 途径站落库搬到独立服务，保持一个事务内原子性
    trainStationRouterService.saveBatch(trainId, ticket.getUserId(), dto.getStations());

        return trainId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long add(TrainTicketSaveRequestDTO dto) {
        if (dto == null || dto.getTicket() == null) {
            throw new IllegalArgumentException("ticket不能为空");
        }
        if (dto.getTicket().getTrainId() != null) {
            throw new IllegalArgumentException("新增时trainId必须为空");
        }
        return save(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long update(TrainTicketSaveRequestDTO dto) {
        if (dto == null || dto.getTicket() == null) {
            throw new IllegalArgumentException("ticket不能为空");
        }
        if (dto.getTicket().getTrainId() == null) {
            throw new IllegalArgumentException("更新时trainId不能为空");
        }
        return save(dto);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long trainId) {
        if (trainId == null) return;
    trainStationRouterService.removeByTrainId(trainId);
        trainRecordMapper.deleteById(trainId);
    }
}
