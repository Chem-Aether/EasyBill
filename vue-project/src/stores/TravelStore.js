import { defineStore } from 'pinia'

export const useTravleStore = defineStore('map', {
    state: () => ({
        // 控制显示模式：足迹 / 航线 / 铁路
        mapType: 'foot',
        mapName: '铁路',
        // 控制地图下钻编码
        adcode: '100000', // 初始全国
    }),

    actions: {
        // 修改模式
        setMapType(type) {
            this.mapType = type
        },
        // 地图下钻
        setAdcode(code) {
            this.adcode = code
        },
    },
})