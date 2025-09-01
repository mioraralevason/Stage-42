package com.ecole._2.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class ApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    
    @Value("${app.client_id}")
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
            logger.info("Demande de token d'accès à l'API 42");
            
            MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
            data.add("grant_type", "client_credentials");
            data.add("scope", "public projects profile tig elearning forum");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(uid, secret);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(data, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(ACCESS_TOKEN_URL, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Token d'accès obtenu avec succès");
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                if (jsonNode.has("access_token")) {
                    return jsonNode.get("access_token").asText();
                } else {
                    throw new RuntimeException("Token d'accès non trouvé dans la réponse");
                }
            } else {
                logger.error("Erreur lors de l'obtention du token: {}", response.getStatusCode());
                throw new RuntimeException("Erreur lors de l'obtention du jeton d'accès: " + response.getStatusCode());
            }
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Erreur HTTP lors de l'obtention du token: {}", e.getMessage());
            throw new RuntimeException("Erreur d'authentification avec l'API 42: " + e.getStatusCode());
        } catch (Exception e) {
            logger.error("Erreur lors de l'obtention du token: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de l'obtention du jeton: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère l'ID utilisateur par login
     */
    public String getIdUsers(String login, String token) {
        try {
            logger.info("Recherche de l'utilisateur: {}", login);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            String url = USERS_URL + "?filter[login]=" + login;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String responseBody = response.getBody();
                logger.debug("Réponse de recherche utilisateur: {}", responseBody);
                
                // Vérifier si la réponse n'est pas vide
                if (responseBody.trim().isEmpty()) {
                    throw new RuntimeException("Réponse vide de l'API pour l'utilisateur: " + login);
                }
                
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode.isArray() && jsonNode.size() > 0) {
                    String userId = jsonNode.get(0).get("id").asText();
                    logger.info("ID utilisateur trouvé: {} pour login: {}", userId, login);
                    return userId;
                } else {
                    throw new RuntimeException("Utilisateur non trouvé: " + login);
                }
            } else {
                logger.error("Erreur lors de la recherche utilisateur: {}", response.getStatusCode());
                throw new RuntimeException("Erreur lors de la récupération de l'ID utilisateur: " + response.getStatusCode());
            }
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Erreur HTTP lors de la recherche utilisateur: {}", e.getMessage());
            throw new RuntimeException("Erreur API lors de la récupération de l'ID utilisateur: " + e.getStatusCode());
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche utilisateur: {}", e.getMessage());
            throw new RuntimeException("Erreur API lors de la récupération de l'ID utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les données utilisateur par ID
     */
    public Map<String, Object> getUser(String userId, String token) {
        try {
            logger.info("Récupération des données utilisateur: {}", userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            String url = BASE_URL + "/v2/users/" + userId;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String responseBody = response.getBody();
                
                // Vérifier si la réponse n'est pas vide
                if (responseBody.trim().isEmpty()) {
                    throw new RuntimeException("Réponse vide de l'API pour l'utilisateur ID: " + userId);
                }
                
                Map<String, Object> userData = objectMapper.readValue(responseBody, Map.class);
                logger.info("Données utilisateur récupérées avec succès pour ID: {}", userId);
                return userData;
            } else {
                logger.error("Erreur lors de la récupération utilisateur: {}", response.getStatusCode());
                throw new RuntimeException("Erreur lors de la récupération de l'utilisateur: " + response.getStatusCode());
            }
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Erreur HTTP lors de la récupération utilisateur: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur: " + e.getStatusCode());
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération utilisateur: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les données de candidature par ID utilisateur
     */
    public Map<String, Object> getUserCandidature(String userId, String token) {
        try {
            logger.info("Récupération des données de candidature: {}", userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            String url = BASE_URL + "/v2/users/" + userId + "/user_candidature";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String responseBody = response.getBody();
                
                // Vérifier si la réponse n'est pas vide
                if (responseBody.trim().isEmpty()) {
                    throw new RuntimeException("Réponse vide de l'API pour les données de candidature ID: " + userId);
                }
                
                Map<String, Object> candidatureData = objectMapper.readValue(responseBody, Map.class);
                logger.info("Données de candidature récupérées avec succès pour ID: {}", userId);
                return candidatureData;
            } else {
                logger.error("Erreur lors de la récupération candidature: {}", response.getStatusCode());
                throw new RuntimeException("Erreur lors de la récupération des données de candidature: " + response.getStatusCode());
            }
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Erreur HTTP lors de la récupération candidature: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des données de candidature: " + e.getStatusCode());
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération candidature: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des données de candidature: " + e.getMessage(), e);
        }
    }
}