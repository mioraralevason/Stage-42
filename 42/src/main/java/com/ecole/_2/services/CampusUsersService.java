package com.ecole._2.services;

import com.ecole._2.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

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
     * Retrieves campus users with pagination.
     *
     * @param campusId Campus ID
     * @param accessToken 42 API access token
     * @param pageNumber Page number (optional, default 1)
     * @param pageSize Page size (optional, default 30, max 100)
     * @return List of campus users for the specified page
     * @throws IllegalArgumentException if parameters are invalid
     * @throws RuntimeException if API call fails
     */
    public List<User> getCampusUsers(String campusId, String accessToken, Integer pageNumber, Integer pageSize)
            throws IllegalArgumentException, RuntimeException {

        if (campusId == null || campusId.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus ID cannot be null or empty");
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }

        int page = (pageNumber != null && pageNumber > 0) ? pageNumber : 1;
        int size = (pageSize != null && pageSize > 0) ? Math.min(pageSize, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;

        RestTemplate restTemplate = new RestTemplate();
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
     * Retrieves ALL users from a campus from page 1 to the end.
     *
     * @param campusId Campus ID
     * @param accessToken 42 API access token
     * @return Complete list of ALL campus users
     * @throws IllegalArgumentException if parameters are invalid
     * @throws RuntimeException if API call fails
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
        int currentPage = 1;
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

                    if (pageUsers.size() < MAX_PAGE_SIZE) {
                        hasMorePages = false;
                        logger.info("Last page reached for campus {}. Total users: {}", campusId, allUsers.size());
                    } else {
                        currentPage++;
                    }
                }

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
     * Retrieves campus users with additional filters.
     *
     * @param campusId Campus ID
     * @param accessToken 42 API access token
     * @param pageNumber Page number (optional)
     * @param pageSize Page size (optional)
     * @param filterKey Filter key (e.g., "active", "staff", etc.)
     * @param filterValue Filter value
     * @return List of filtered campus users
     * @throws IllegalArgumentException if parameters are invalid
     * @throws RuntimeException if API call fails
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

        int page = (pageNumber != null && pageNumber > 0) ? pageNumber : 1;
        int size = (pageSize != null && pageSize > 0) ? Math.min(pageSize, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;

        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_URL_CAMPUS + "/" + campusId + "/users?page[number]=" + page + "&page[size]=" + size;

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
     * Retrieves ALL active users from a campus.
     *
     * @param campusId Campus ID
     * @param accessToken 42 API access token
     * @return Complete list of all active campus users
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