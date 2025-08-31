package com.ecole._2.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

@Service
public class ApiService {
    
    @Value("${app.client_id")
    private String uid;
    
    @Value("${app.client_secret}")
    private String secret;
    
    private static final String BASE_URL = "https://api.intra.42.fr";
    private static final String ACCESS_TOKEN_URL = "https://api.intra.42.fr/oauth/token";
    private static final String USERS_URL = "https://api.intra.42.fr/v2/users";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Obtient un token d'accès depuis l'API 42
     */
    public String getAccessToken() {
        try {
            MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
            data.add("grant_type", "client_credentials");
            data.add("scope", "public projects profile tig elearning forum");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(uid, secret);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(data, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(ACCESS_TOKEN_URL, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("access_token").asText();
            } else {
                throw new RuntimeException("Erreur lors de l'obtention du jeton d'accès");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'obtention du jeton: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère l'ID utilisateur par login
     */
    public String getIdUsers(String login, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            String url = USERS_URL + "?filter[login]=" + login;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.isArray() && jsonNode.size() > 0) {
                    return jsonNode.get(0).get("id").asText();
                } else {
                    throw new RuntimeException("Utilisateur non trouvé");
                }
            } else {
                throw new RuntimeException("Erreur lors de la récupération de l'ID utilisateur");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur API lors de la récupération de l'ID utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les données utilisateur par ID
     */
    public Map<String, Object> getUser(String userId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            String url = BASE_URL + "/v2/users/" + userId;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), Map.class);
            } else {
                throw new RuntimeException("Erreur lors de la récupération de l'utilisateur");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les données de candidature par ID utilisateur
     */
    public Map<String, Object> getUserCandidature(String userId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            String url = BASE_URL + "/v2/users/" + userId + "/user_candidature";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), Map.class);
            } else {
                throw new RuntimeException("Erreur lors de la récupération des données de candidature");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des données de candidature: " + e.getMessage(), e);
        }
    }
}