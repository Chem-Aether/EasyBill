import request from '@/utils/request.js'

// 获取机票列表
export const getFlightList = (params = {}) => {
    return request({
        url: '/travel/flightTickets/list',
        method: 'GET',
        params: {
            // 分页
            pageNum: params.pageNum,
            pageSize: params.pageSize,

            // 查询条件
            flightNo: params.flightNo,
            departureAirport: params.departureAirport,
            arrivalAirport: params.arrivalAirport,

            // 时间范围
            departureDatetimeStart: params.departureDatetimeStart,
            departureDatetimeEnd: params.departureDatetimeEnd
        }
    })
}

// 公共字段
const flightTicketFields = (params) => ({
    flightNo: params.flightNo,
    company: params.company,
    aircraftReg: params.aircraftReg,
    aircraftType: params.aircraftType,

    departureAirport: params.departureAirport,
    departureTerminal: params.departureTerminal,
    departureIcao: params.departureIcao,
    takeoffTime: params.takeoffTime,
    boardingMethod: params.boardingMethod,

    arrivalAirport: params.arrivalAirport,
    arrivalTerminal: params.arrivalTerminal,
    arrivalIcao: params.arrivalIcao,
    landingTime: params.landingTime,
    deplaningMethod: params.deplaningMethod,

    stopoverAirport: params.stopoverAirport,
    flightDistanceKm: params.flightDistanceKm,
    seatNo: params.seatNo
})

// 新增机票
export const insertFlightTicket = (params = {}) => {
    return request({
        url: '/travel/flightTickets/insert',
        method: 'POST',
        data: flightTicketFields(params)
    })
}

// 根据 ID 修改
export const updateFlightTicketById = (params = {}) => {
    return request({
        url: '/travel/flightTickets/update',
        method: 'PUT',
        data: {
            flightId: params.flightId,
            ...flightTicketFields(params)
        }
    })
}

// 根据 ID 修改
export const deleteFlightTicketById = (id) => {
    return request({
        url: `/travel/flightTickets/delete/${id}`,
        method: 'DELETE'
    })
}