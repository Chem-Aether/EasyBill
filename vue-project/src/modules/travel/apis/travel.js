import request from '@/utils/request.js';


const yearParams = (year) => year === 'all' ? {} : { year }

export const getTickets = (mode, year = 'all') => {
    return request({
        url: '/travel/tickets',
        method: 'get',
        params: { mode, ...yearParams(year) }
    })
}


export const getTicketSummary = (mode, year = 'all') => {
    return request({
        url: '/travel/tickets/summary',
        method: 'get',
        params: { mode, ...yearParams(year) }
    })
}

export const getFootprints = (year = 'all') => request({
    url: '/travel/footprints',
    method: 'get',
    params: yearParams(year)
})

export const exportTravelData = () => request({
    url: '/travel/export',
    method: 'get',
    responseType: 'blob',
    fullResponse: true
})

export const addFootprint = (data) => request({
    url: '/travel/footprints',
    method: 'post',
    data
})

export const updateFootprint = (id, data) => request({
    url: `/travel/footprints/${id}`,
    method: 'put',
    data
})

export const deleteFootprint = (id) => request({
    url: `/travel/footprints/${id}`,
    method: 'delete'
})
