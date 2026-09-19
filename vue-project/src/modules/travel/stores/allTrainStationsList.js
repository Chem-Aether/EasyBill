import { defineStore } from 'pinia'
import { searchTrainStations } from '@/modules/travel/apis/sysResource.js'

export const useTrainStationStore = defineStore('trainStation', {
  state: () => ({ requestId: 0 }),
  actions: {
    async search(keyword) {
      const value = keyword?.trim()
      if (!value) return []
      const currentRequest = ++this.requestId
      const response = await searchTrainStations(value)
      if (currentRequest !== this.requestId) return null
      return (response.data || []).map(item => ({ ...item, value: item.name }))
    },
  },
})
