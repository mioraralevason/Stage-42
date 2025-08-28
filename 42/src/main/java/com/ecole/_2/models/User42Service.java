package com.ecole._2.models;

import com.ecole._2.models.UserResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class User42Service {

    private static final String USER_URL = "https://api.intra.42.fr/v2/me";

    public UserResponse getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // Préparer les headers avec le token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Appel GET à l’API
        ResponseEntity<UserResponse> response =
                restTemplate.exchange(USER_URL, HttpMethod.GET, entity, UserResponse.class);

        return response.getBody();
    }
}
