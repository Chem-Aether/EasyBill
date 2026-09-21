import request from '@/utils/request.js'

// 列表（支持分页/条件查询）
export const getTrainList = (params = {}) =>
    request({
        url: '/travel/trains',
        method: 'GET',
        params
    }).then(res => {
    // 永远分页：后端应返回 Result{ data: IPage{ records, total, ... } }
    const payload = (res && Object.prototype.hasOwnProperty.call(res, 'data')) ? res.data : res

    if (payload && Array.isArray(payload.records)) {
        return { data: payload.records, page: payload }
    }

    // 兜底：避免页面 map 报错
    return { data: [], page: { total: 0, records: [] } }
    })

// 获取车票基础信息（不含途经站明细）
export const getTrainTicket = (trainId) => {
    return request({
        url: `/travel/trains/${trainId}`,
        method: 'GET'
    })
}

// 获取某个车票的途经站明细（展开时用）
export const getTrainStationsByTrainId = (trainId) => {
    return request({
        url: `/travel/trains/${trainId}/stations`,
        method: 'GET'
    })
}

// 新增车票（支持同时保存途经站）
// ticket: TrainRecord
// stations: TrainStationRecord[]
export const addTrainTicket = ({ ticket, stations } = {}) => {
    return request({
        url: '/travel/trains',
        method: 'POST',
        data: { ticket, stations }
    })
}

// 更新车票（支持同时保存途经站最终态）
export const updateTrainTicket = ({ ticket, stations } = {}) => {
    return request({
        url: `/travel/trains/${ticket.trainId}`,
        method: 'PUT',
        data: { ticket, stations }
    })
}

export const deleteTrainTicket = (trainId) => {
    return request({
        url: `/travel/trains/${trainId}`,
        method: 'DELETE'
    })
}
