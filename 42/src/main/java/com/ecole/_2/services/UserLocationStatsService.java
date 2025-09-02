package com.ecole._2.services;
import com.ecole._2.models.LocationStat;
import com.ecole._2.models.UserLocationStat;
import com.ecole._2.models.CursusUser;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserLocationStatsService {
    private static final String BASE_URL = "https://api.intra.42.fr/v2/users/";
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
            
            return new UserLocationStat(userId, stats);
        } catch (RestClientException e) {
            // En cas d'erreur, retourner un objet avec une liste vide
            System.err.println("Error fetching location stats for user " + userId + ": " + e.getMessage());
            return new UserLocationStat(userId, new ArrayList<>());
        }
    }

    /**
     * Récupère les statistiques de localisation pour une liste de CursusUser.
     * Cette méthode traite les requêtes de manière séquentielle.
     * 
     * @param cursusUsers Liste des CursusUser
     * @param accessToken Token d'accès à l'API 42
     * @return Liste des UserLocationStat
     * @throws IllegalArgumentException si la liste est null ou le token est null/vide
     */
    public List<UserLocationStat> getUserLocationStatsFromCursusUsers(List<CursusUser> cursusUsers, String accessToken) 
            throws IllegalArgumentException {
        if (cursusUsers == null) {
            throw new IllegalArgumentException("CursusUser list cannot be null");
        }
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }

        List<UserLocationStat> results = new ArrayList<>();
        
        for (CursusUser cursusUser : cursusUsers) {
            if (cursusUser != null && cursusUser.getUser() != null) {
                String userId = String.valueOf(cursusUser.getUser().getId());
                UserLocationStat userLocationStat = getUserLocationStats(userId, accessToken);
                results.add(userLocationStat);
            }
        }
        
        return results;
    }
}