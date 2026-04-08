import request from '@/utils/request';


export const getTicketData = (type = 'all') => {
    return request({
        url: '/travel/getTicketData',
        method: 'get',
        params: { type }
    })
}


export const getTicketStatistics = (type = 'all') => {
    return request({
        url: '/travel/getTicketStatistics',
        method: 'get',
        params: { type }
    })
}