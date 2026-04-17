import request from '@/utils/request.js'

// 获取机票列表（支持条件 + 分页 + 时间）
export const getTrainList = (params = {}) => {
    return request({
        url: '/travel/trainTickets/list',
        method: 'GET',
        params: {
            // 分页
            pageNum: params.pageNum,
            pageSize: params.pageSize,

            // 查询条件
            trainNo: params.trainNo,
            startStation: params.startStation,
            endStation: params.endStation,

            // 时间范围
            departureDatetimeStart: params.departureDatetimeStart,
            departureDatetimeEnd: params.departureDatetimeEnd
        }
    })
}
