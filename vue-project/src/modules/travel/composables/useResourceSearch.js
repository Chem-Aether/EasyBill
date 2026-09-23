import { searchAirports, searchTrainStations } from '@/modules/travel/apis/sysResource.js'

const normalizeKeyword = keyword => keyword?.trim() || ''

const createSuggestionQuery = (request, formatValue) => async (keyword, callback) => {
  const value = normalizeKeyword(keyword)
  if (!value) {
    callback([])
    return
  }

  try {
    const response = await request(value)
    callback((response.data || []).map(item => ({
      ...item,
      value: formatValue(item),
    })))
  } catch {
    callback([])
  }
}

export const useResourceSearch = () => ({
  queryAirport: createSuggestionQuery(
    searchAirports,
    item => `${item.name}${item.iata ? ` / ${item.iata}` : ''}${item.icao ? ` (${item.icao})` : ''}`,
  ),
  queryTrainStation: createSuggestionQuery(
    searchTrainStations,
    item => item.name,
  ),
})
