package com.mapserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GeoReferenceClient {

    private final RestClient restClient;

    public GeoReferenceClient(@Value("${mapserver.base-url:http://127.0.0.1:8765}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public Map<String, GeoStation> stationsByName(Collection<String> names) {
        if (names == null || names.isEmpty()) return Collections.emptyMap();
        ApiResponse<List<GeoStation>> response = restClient.post()
                .uri("/api/stations/by-names")
                .body(Map.of("names", names))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return data(response).stream().collect(Collectors.toMap(GeoStation::name, Function.identity(), (left, right) -> left));
    }

    public Map<String, GeoAirport> airportsByIcao(Collection<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        ApiResponse<List<GeoAirport>> response = restClient.post()
                .uri("/api/airports/by-codes")
                .body(Map.of("codes", codes))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return data(response).stream().collect(Collectors.toMap(GeoAirport::icao, Function.identity(), (left, right) -> left));
    }

    public Map<String, GeoRegion> regionsByCode(Collection<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        ApiResponse<List<GeoRegion>> response = restClient.post()
                .uri("/api/regions/by-codes")
                .body(Map.of("codes", codes))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return data(response).stream().collect(Collectors.toMap(GeoRegion::code, Function.identity(), (left, right) -> left));
    }

    public GeoLocation reverseGeocode(double longitude, double latitude) {
        ApiResponse<GeoLocation> response = restClient.get()
                .uri(builder -> builder.path("/api/geocode/reverse")
                        .queryParam("longitude", longitude)
                        .queryParam("latitude", latitude)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return response == null ? null : response.data();
    }

    private static <T> List<T> data(ApiResponse<List<T>> response) {
        return response == null || response.data() == null ? List.of() : response.data();
    }

    public record GeoStation(String name, String code, String city, String region, String province,
                             Double longitude, Double latitude) {}

    public record GeoAirport(String icao, String iata, String name, String city, String attr,
                             Double longitude, Double latitude) {}

    public record GeoRegion(String code, String name, Integer level, String type, String parentCode,
                            String fullName) {}

    public record GeoLocation(Double longitude, Double latitude, GeoRegion province, GeoRegion city,
                              GeoRegion district, String formattedRegion, String dataVersion) {}

    private record ApiResponse<T>(String msg, T data) {}
}
