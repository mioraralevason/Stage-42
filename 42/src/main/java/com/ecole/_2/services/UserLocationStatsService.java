package com.ecole._2.services;

import com.ecole._2.models.LocationStat;
import com.ecole._2.models.UserLocationStat;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserLocationStatsService {

    private static final String BASE_URL = "https://api.intra.42.fr/v2/users/";

    public UserLocationStat getUserLocationStats(String userId, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        String url = BASE_URL + userId + "/locations_stats";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, String> rawData = response.getBody();
        List<LocationStat> stats = new ArrayList<>();

        if (rawData != null) {
            for (Map.Entry<String, String> entry : rawData.entrySet()) {
                LocalDate date = LocalDate.parse(entry.getKey());

                // Convertir "HH:mm:ss.SSSSSS" en Duration manuellement
                String[] hms = entry.getValue().split(":");
                int hours = Integer.parseInt(hms[0]);
                int minutes = Integer.parseInt(hms[1]);

                String[] secMicro = hms[2].split("\\.");
                int seconds = Integer.parseInt(secMicro[0]);
                int micros = secMicro.length > 1 ? Integer.parseInt(secMicro[1]) : 0;

                Duration duration = Duration.ofHours(hours)
                        .plusMinutes(minutes)
                        .plusSeconds(seconds)
                        .plusNanos(micros * 1000); // convertir µs → ns

                stats.add(new LocationStat(date, duration));
            }
        }

        return new UserLocationStat(userId, stats);
    }
}
