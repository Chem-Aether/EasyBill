package com.travel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class RailwayRouteClient {
    private final RestClient client;

    public RailwayRouteClient(@Value("${mapserver.base-url:http://127.0.0.1:8765}") String baseUrl) {
        client = RestClient.builder().baseUrl(baseUrl).build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> sample(String trainNo, List<String> stations) {
        Map<String, Object> response = client.post().uri("/api/railway/routes/sample")
                .body(Map.of("trainCode", trainNo == null ? "" : trainNo, "stations", stations))
                .retrieve().body(new ParameterizedTypeReference<>() {});
        Object data = response == null ? null : response.get("data");
        return data instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }
}
