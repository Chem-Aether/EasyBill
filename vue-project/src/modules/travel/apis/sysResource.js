import request from '@/utils/request.js'

export const getTrainStationAll = (params = {}) => {
    return request({
        url: '/sys/support/TrainStations/all',
        method: 'GET',
    })
}