import { defineStore } from 'pinia'
import { searchAirports } from '@/modules/travel/apis/sysResource.js'

export const useAirportStore = defineStore('airportStore', {
  state: () => ({ requestId: 0 }),
  actions: {
    async search(keyword) {
      const value = keyword?.trim()
      if (!value) return []
      const currentRequest = ++this.requestId
      const response = await searchAirports(value)
      if (currentRequest !== this.requestId) return null
      return (response.data || []).map(item => ({
        ...item,
        value: `${item.name}${item.iata ? ` / ${item.iata}` : ''} (${item.icao})`,
      }))
    },
  },
})
