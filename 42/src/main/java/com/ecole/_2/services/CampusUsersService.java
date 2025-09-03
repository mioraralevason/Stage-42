package com.ecole._2.services;

import com.ecole._2.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CampusUsersService {
    private static final Logger logger = LoggerFactory.getLogger(CampusUsersService.class);
    private static final String BASE_URL_CAMPUS = "https://api.intra.42.fr/v2/campus";
    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Récupère les utilisateurs d'un campus avec pagination.
     * 
     * @param campusId ID du campus
     * @param accessToken Token d'accès à l'API 42
     * @param pageNumber Numéro de la page (optionnel, par défaut 1)
     * @param pageSize Taille de la page (optionnel, par défaut 30, maximum 100)
     * @return Liste des utilisateurs du campus pour la page spécifiée
     * @throws IllegalArgumentException si les paramètres sont invalides
     * @throws RuntimeException si erreur lors de l'appel API
     */
    public List<User> getCampusUsers(String campusId, String accessToken, Integer pageNumber, Integer pageSize) 
            throws IllegalArgumentException, RuntimeException {
        
        if (campusId == null || campusId.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus ID cannot be null or empty");
        }
        
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }

        // Valeurs par défaut et validation
        int page = (pageNumber != null && pageNumber > 0) ? pageNumber : 1;
        int size = (pageSize != null && pageSize > 0) ? Math.min(pageSize, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;

        RestTemplate restTemplate = new RestTemplate();
        
        // Construire l'URL avec les paramètres de pagination
        String url = BASE_URL_CAMPUS + "/" + campusId + "/users?page[number]=" + page + "&page[size]=" + size;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<User[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                User[].class
            );

            User[] users = response.getBody();
            return users != null ? Arrays.asList(users) : new ArrayList<>();
            
        } catch (RestClientException e) {
            throw new RuntimeException("Error fetching users for campus " + campusId + " from API: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère TOUS les utilisateurs d'un campus depuis la page 1 jusqu'à la fin.
     * 
     * @param campusId ID du campus
     * @param accessToken Token d'accès à l'API 42
     * @return Liste complète de TOUS les utilisateurs du campus
     * @throws IllegalArgumentException si les paramètres sont invalides
     * @throws RuntimeException si erreur lors de l'appel API
     */
    public List<User> getAllCampusUsers(String campusId, String accessToken) 
            throws IllegalArgumentException, RuntimeException {
        
        if (campusId == null || campusId.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus ID cannot be null or empty");
        }
        
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }

        List<User> allUsers = new ArrayList<>();
        int currentPage = 1;  // Commencer à la page 1
        boolean hasMorePages = true;

        while (hasMorePages) {
            try {
                logger.debug("Fetching users for campus {}, page: {}", campusId, currentPage);
                List<User> pageUsers = getCampusUsers(campusId, accessToken, currentPage, MAX_PAGE_SIZE);
                
                if (pageUsers.isEmpty()) {
                    hasMorePages = false;
                    logger.info("No more users found for campus {}. Total pages fetched: {}", campusId, currentPage - 1);
                } else {
                    allUsers.addAll(pageUsers);
                    logger.debug("Fetched {} users from page {}. Total users for campus {} so far: {}", 
                                pageUsers.size(), currentPage, campusId, allUsers.size());
                    
                    // Si la page retourne moins d'éléments que la taille maximale, c'est la dernière page
                    if (pageUsers.size() < MAX_PAGE_SIZE) {
                        hasMorePages = false;
                        logger.info("Last page reached for campus {}. Total users: {}", campusId, allUsers.size());
                    } else {
                        currentPage++;  // Passer à la page suivante
                    }
                }
                
                // Petite pause pour éviter de surcharger l'API
                Thread.sleep(200);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while fetching users for campus " + campusId, e);
            } catch (Exception e) {
                throw new RuntimeException("Error fetching users for campus " + campusId + ", page " + currentPage + ": " + e.getMessage(), e);
            }
        }

        logger.info("Finished fetching all users for campus {}. Total: {}", campusId, allUsers.size());
        return allUsers;
    }

    /**
     * Récupère les utilisateurs d'un campus avec filtres additionnels.
     * 
     * @param campusId ID du campus
     * @param accessToken Token d'accès à l'API 42
     * @param pageNumber Numéro de la page (optionnel)
     * @param pageSize Taille de la page (optionnel)
     * @param filters Filtres additionnels (ex: "active", "staff", etc.)
     * @return Liste des utilisateurs du campus filtrés
     * @throws IllegalArgumentException si les paramètres sont invalides
     * @throws RuntimeException si erreur lors de l'appel API
     */
    public List<User> getCampusUsersWithFilter(String campusId, String accessToken, Integer pageNumber, 
                                              Integer pageSize, String filterKey, String filterValue) 
            throws IllegalArgumentException, RuntimeException {
        
        if (campusId == null || campusId.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus ID cannot be null or empty");
        }
        
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }

        // Valeurs par défaut et validation
        int page = (pageNumber != null && pageNumber > 0) ? pageNumber : 1;
        int size = (pageSize != null && pageSize > 0) ? Math.min(pageSize, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;

        RestTemplate restTemplate = new RestTemplate();
        
        // Construire l'URL avec les paramètres de pagination et filtre
        String url = BASE_URL_CAMPUS + "/" + campusId + "/users?page[number]=" + page + "&page[size]=" + size;

        // Ajouter le filtre si spécifié
        if (filterKey != null && !filterKey.trim().isEmpty() && 
            filterValue != null && !filterValue.trim().isEmpty()) {
            url += "&filter[" + filterKey + "]=" + filterValue;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<User[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                User[].class
            );

            User[] users = response.getBody();
            return users != null ? Arrays.asList(users) : new ArrayList<>();
            
        } catch (RestClientException e) {
            throw new RuntimeException("Error fetching filtered users for campus " + campusId + " from API: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère TOUS les utilisateurs actifs d'un campus.
     * 
     * @param campusId ID du campus
     * @param accessToken Token d'accès à l'API 42
     * @return Liste complète de tous les utilisateurs actifs du campus
     */
    public List<User> getAllActiveCampusUsers(String campusId, String accessToken) 
            throws IllegalArgumentException, RuntimeException {
        
        if (campusId == null || campusId.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus ID cannot be null or empty");
        }
        
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }

        List<User> allActiveUsers = new ArrayList<>();
        int currentPage = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            try {
                logger.debug("Fetching active users for campus {}, page: {}", campusId, currentPage);
                List<User> pageUsers = getCampusUsersWithFilter(campusId, accessToken, currentPage, MAX_PAGE_SIZE, "active", "true");
                
                if (pageUsers.isEmpty()) {
                    hasMorePages = false;
                    logger.info("No more active users found for campus {}. Total pages fetched: {}", campusId, currentPage - 1);
                } else {
                    allActiveUsers.addAll(pageUsers);
                    logger.debug("Fetched {} active users from page {}. Total active users for campus {} so far: {}", 
                                pageUsers.size(), currentPage, campusId, allActiveUsers.size());
                    
                    if (pageUsers.size() < MAX_PAGE_SIZE) {
                        hasMorePages = false;
                        logger.info("Last page reached for campus {}. Total active users: {}", campusId, allActiveUsers.size());
                    } else {
                        currentPage++;
                    }
                }
                
                Thread.sleep(200);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while fetching active users for campus " + campusId, e);
            } catch (Exception e) {
                throw new RuntimeException("Error fetching active users for campus " + campusId + ", page " + currentPage + ": " + e.getMessage(), e);
            }
        }

        logger.info("Finished fetching all active users for campus {}. Total: {}", campusId, allActiveUsers.size());
        return allActiveUsers;
    }
}