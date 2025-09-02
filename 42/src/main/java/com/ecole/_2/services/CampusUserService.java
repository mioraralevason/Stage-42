package com.ecole._2.services;

import com.ecole._2.models.CampusUser;
import com.ecole._2.models.CampusUserList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CampusUserService {
    
    private static final Logger logger = LoggerFactory.getLogger(CampusUserService.class);
    private static final String BASE_URL = "https://api.intra.42.fr/v2/campus_users";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public CampusUserList getCampusUsers(String campusId, String accessToken) {
        try {
            logger.info("=== DÉBUT RÉCUPÉRATION CAMPUS USERS ===");
            logger.info("Campus ID: {}", campusId);
            logger.info("Token présent: {}", accessToken != null ? "OUI" : "NON");
            
            RestTemplate restTemplate = new RestTemplate();
            String url = BASE_URL + "?campus_id=" + campusId;
            logger.info("URL de requête: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Content-Type", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            logger.info("Envoi de la requête vers l'API 42...");
            ResponseEntity<CampusUser[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                CampusUser[].class
            );
            
            // Log du statut de réponse
            logger.info("Statut de réponse: {}", response.getStatusCode());
            logger.info("Headers de réponse: {}", response.getHeaders());
            
            // Vérification du corps de réponse
            CampusUser[] campusUsers = response.getBody();
            if (campusUsers == null) {
                logger.warn("ATTENTION: Corps de réponse null");
                return new CampusUserList(campusId, Arrays.asList());
            }
            
            logger.info("Nombre d'utilisateurs récupérés: {}", campusUsers.length);
            
            // Log détaillé des premiers utilisateurs (max 5 pour éviter le spam)
            int maxToLog = Math.min(5, campusUsers.length);
            for (int i = 0; i < maxToLog; i++) {
                CampusUser user = campusUsers[i];
                logger.info("Utilisateur {}: {}", i + 1, logCampusUser(user));
            }
            
            if (campusUsers.length > 5) {
                logger.info("... et {} autres utilisateurs", campusUsers.length - 5);
            }
            
            // Création de la liste
            List<CampusUser> userList = Arrays.asList(campusUsers);
            CampusUserList result = new CampusUserList(campusId, userList);
            
            logger.info("=== FIN RÉCUPÉRATION CAMPUS USERS ===");
            logger.info("Résultat final - Campus ID: {}, Nombre d'utilisateurs: {}", 
                       result.getCampusId(), result.getCampusUsers().size());
            
            return result;
            
        } catch (HttpClientErrorException e) {
            logger.error("Erreur client HTTP ({}): {}", e.getStatusCode(), e.getMessage());
            logger.error("Corps de réponse d'erreur: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Erreur d'authentification API 42: " + e.getStatusCode());
            
        } catch (HttpServerErrorException e) {
            logger.error("Erreur serveur HTTP ({}): {}", e.getStatusCode(), e.getMessage());
            logger.error("Corps de réponse d'erreur: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Erreur serveur API 42: " + e.getStatusCode());
            
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la récupération des campus users", e);
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs campus: " + e.getMessage(), e);
        }
    }
    
    /**
     * Méthode utilitaire pour logger les détails d'un CampusUser de manière lisible
     */
    private String logCampusUser(CampusUser user) {
        if (user == null) {
            return "null";
        }
        
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("CampusUser{");
            
            // Propriétés basées sur votre modèle CampusUser
            if (user.getId() != null) sb.append("id=").append(user.getId()).append(", ");
            if (user.getUser_id() != null) sb.append("userId=").append(user.getUser_id()).append(", ");
            if (user.getCampus_id() != null) sb.append("campusId=").append(user.getCampus_id()).append(", ");
            sb.append("isPrimary=").append(user.is_primary()).append(", ");
            if (user.getCreated_at() != null) sb.append("createdAt=").append(user.getCreated_at()).append(", ");
            if (user.getUpdated_at() != null) sb.append("updatedAt=").append(user.getUpdated_at());

            sb.append("}");
            return sb.toString();
            
        } catch (Exception e) {
            logger.warn("Erreur lors du log du CampusUser: {}", e.getMessage());
            return "CampusUser{erreur de sérialisation}";
        }
    }
    
    /**
     * Méthode de debug pour afficher la réponse brute de l'API
     */
    public String getCampusUsersRawResponse(String campusId, String accessToken) {
        try {
            logger.info("=== RÉCUPÉRATION RÉPONSE BRUTE ===");
            
            RestTemplate restTemplate = new RestTemplate();
            String url = BASE_URL + "?campus_id=" + campusId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Content-Type", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            String rawResponse = response.getBody();
            logger.info("Réponse brute de l'API:");
            logger.info("Status: {}", response.getStatusCode());
            logger.info("Content-Type: {}", response.getHeaders().getContentType());
            logger.info("Taille réponse: {} caractères", rawResponse != null ? rawResponse.length() : 0);
            
            // Log des premiers caractères de la réponse
            if (rawResponse != null) {
                String preview = rawResponse.length() > 500 ? 
                    rawResponse.substring(0, 500) + "..." : rawResponse;
                logger.info("Aperçu réponse: {}", preview);
                
                // Tentative de parsing pour validation
                try {
                    objectMapper.readTree(rawResponse);
                    logger.info("JSON valide ✓");
                } catch (Exception e) {
                    logger.error("JSON invalide ✗: {}", e.getMessage());
                }
            }
            
            return rawResponse;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la réponse brute", e);
            return null;
        }
    }
}