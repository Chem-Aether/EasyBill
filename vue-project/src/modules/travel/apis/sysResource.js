import request from '@/utils/request.js'


export const searchAirports = (keyword) => {
    return request({
        url: '/sys/support/Airports/search',
        method: 'GET',
        params: { keyword }
    })
}

export const searchTrainStations = (keyword) => {
    return request({
        url: '/sys/support/TrainStations/search',
        method: 'GET',
        params: { keyword }
    })
}

export const searchRegions = (keyword, level = 3) => {
    return request({
        url: '/sys/support/area/search',
        method: 'GET',
        params: { keyword, level }
    })
}
