import request from '@/utils/request.js';


export const getTicketData = (type = 'all') => {
    return request({
        url: '/travel/getTicketData',
        method: 'get',
        params: { type }
    })
}


export const getTicketStatistics = (type = 'train') => {
    return request({
        url: '/travel/getTicketStatistics',
        method: 'get',
        params: { type }
    })
}

export const getTicketDashboard = (type = 'train') => {
    return request({
        url: '/travel/getTicketDashboard',
        method: 'get',
        params: { type }
    })
}

export const statsSpotCount = () => {
    return request({
        url: '/travel/statsSpotCount',
        method: 'get',
    })
}

export const getVisitedCities = () => {
    return request({
        url: '/travel/getVisitedCities',
        method: 'get',
    })
}