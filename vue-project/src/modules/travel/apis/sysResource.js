import request from '@/utils/request.js'

export const getTrainStationAll = (params = {}) => {
    return request({
        url: '/sys/support/TrainStations/all',
        method: 'GET',
    })
}

export const getAirportAll = () => {
    return request({
        url: '/sys/support/Airports/all',
        method: 'GET'
    })
}