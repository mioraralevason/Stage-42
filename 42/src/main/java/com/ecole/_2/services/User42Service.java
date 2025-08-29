package com.ecole._2.services;

import com.ecole._2.models.User;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class User42Service {

    private static final String USER_URL = "https://api.intra.42.fr/v2/me";

    public User getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<User> response =
                restTemplate.exchange(USER_URL, HttpMethod.GET, entity, User.class);

        return response.getBody();
    }
}
