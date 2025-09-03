package com.ecole._2.services;

import com.ecole._2.models.LocationStat;
import com.ecole._2.models.User;
import com.ecole._2.models.UserLocationStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserLocationStatsService {
    private static final Logger logger = LoggerFactory.getLogger(UserLocationStatsService.class);
    private static final String BASE_URL = "https://api.intra.42.fr/v2/users/";
    private static final int MAX_RETRIES = 5;
    private static final long BASE_RETRY_DELAY_MS = 10000; // 10 sec
    private static final long RATE_LIMIT_DELAY_MS = 500; // 500ms for 2 requests per second

    /**
     * Récupère les statistiques de localisation pour un utilisateur spécifique.
     */
    public UserLocationStat getUserLocationStats(String userId, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_URL + userId + "/locations_stats";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            logger.debug("Fetching location stats for user {}", userId);
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
                    String[] hms = entry.getValue().split(":");
                    int hours = Integer.parseInt(hms[0]);
                    int minutes = Integer.parseInt(hms[1]);
                    String[] secMicro = hms[2].split("\\.");
                    int seconds = Integer.parseInt(secMicro[0]);
                    int micros = secMicro.length > 1 ? Integer.parseInt(secMicro[1]) : 0;

                    Duration duration = Duration.ofHours(hours)
                            .plusMinutes(minutes)
                            .plusSeconds(seconds)
                            .plusNanos(micros * 1000);

                    stats.add(new LocationStat(date, duration));
                }
            }

            logger.info("Successfully fetched location stats for user {}", userId);
            return new UserLocationStat(userId, stats);
        } catch (RestClientException e) {
            logger.error("Error fetching location stats for user {}: {}", userId, e.getMessage());
            return new UserLocationStat(userId, new ArrayList<>());
        }
    }

    /**
     * Récupère les statistiques de localisation pour une liste d'utilisateurs.
     * Limite à 2 requêtes par seconde.
     */
    public List<UserLocationStat> getUserLocationStatsFromUsers(List<User> users, ApiService apiService) {
        if (users == null) {
            logger.error("User list is null");
            throw new IllegalArgumentException("User list cannot be null");
        }

        String accessToken = apiService.getAccessToken();
        if (accessToken == null || accessToken.trim().isEmpty()) {
            logger.error("Access token is null or empty");
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }

        List<UserLocationStat> results = new ArrayList<>();
        logger.info("Starting to fetch location stats for {} users", users.size());

        for (User user : users) {
            if (user != null && user.getId() != null) {
                String userId = user.getId();
                boolean success = false;
                int retries = 0;

                while (!success && retries < MAX_RETRIES) {
                    try {
                        UserLocationStat stat = getUserLocationStats(userId, accessToken);
                        results.add(stat);
                        success = true;

                        // Pause pour limiter à 2 requêtes par seconde (500ms)
                        Thread.sleep(RATE_LIMIT_DELAY_MS);

                    } catch (RestClientException e) {
                        String message = e.getMessage() != null ? e.getMessage() : "";

                        if (message.contains("429")) {
                            retries++;
                            long delay = BASE_RETRY_DELAY_MS * (1L << (retries - 1)); // backoff exponentiel
                            logger.warn("429 Too Many Requests for user {}. Retry {} in {}s", 
                                        userId, retries, delay / 1000);

                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                logger.error("Interrupted during retry delay for user {}", userId, ie);
                                throw new IllegalStateException("Interrupted during retry delay", ie);
                            }

                            // Refresh token après un délai
                            accessToken = apiService.getAccessToken();
                            if (accessToken == null || accessToken.trim().isEmpty()) {
                                logger.error("Failed to refresh access token for user {}", userId);
                                throw new IllegalArgumentException("Failed to refresh access token");
                            }

                        } else {
                            logger.error("Non-429 error for user {}: {}", userId, e.getMessage());
                            break; // Sortir si ce n'est pas une erreur 429
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("Interrupted during rate limit pause for user {}", userId, e);
                        throw new IllegalStateException("Interrupted during rate limit pause", e);
                    }
                }
            } else {
                logger.warn("Skipping invalid user: {}", user);
            }
        }

        logger.info("Completed fetching location stats for {} users", results.size());
        return results;
    }
}