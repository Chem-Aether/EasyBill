import { defineStore } from 'pinia'
import { getAirportAll } from '@/modules/travel/apis/sysResource.js'

export const useAirportStore = defineStore('airportStore', {
  state: () => ({
    airportList: []
  }),

  actions: {
    async initAirports() {
      if (this.airportList.length > 0) return

      try {
        const res = await getAirportAll()
        this.airportList = res.data || []
        console.log('✅ 机场初始化完成：', this.airportList.length)
      } catch (err) {
        console.error('❌ 机场数据加载失败', err)
      }
    }
  },

  getters: {
    // 给 el-autocomplete 用：返回 { value, icao, name }
    searchAirport: (state) => (keyword) => {
      if (!keyword?.trim()) return []
      const kw = keyword.trim()
      return state.airportList
        .filter(item => (item.name || '').includes(kw) || (item.attr || '').includes(kw) || (item.icao || '').includes(kw))
        .slice(0, 20)
        .map(item => {
          const attrPart = item.attr ? `${item.attr}` : ''
          return {
            value: `${item.name}${attrPart} (${item.icao})`,
            icao: item.icao,
            name: item.name,
            attr: item.attr
          }
        })
    }
  }
})
