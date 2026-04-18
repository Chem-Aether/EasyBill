import { defineStore } from 'pinia'
import { getTrainStationAll } from '@/modules/travel/apis/sysResource.js'

export const useTrainStationStore = defineStore('trainStation', {
    state: () => ({
        stationList: [],
    }),

    actions: {
        async initTrainStations() {
            if (this.stationList.length > 0) return

            try {
                const res = await getTrainStationAll()
                this.stationList = res.data || []
                console.log('✅ 全国车站初始化完成：', this.stationList.length)
            } catch (err) {
                console.error('❌ 车站数据加载失败', err)
            }
        },
    },

    getters: {
        searchStation: (state) => (keyword) => {
            if (!keyword) return []
            return state.stationList
                .filter(item => item.name.includes(keyword.trim()))
                .map(item => ({ value: item.name }))
        },
    },
})