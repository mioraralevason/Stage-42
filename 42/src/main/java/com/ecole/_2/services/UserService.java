package com.ecole._2.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecole._2.models.UserResponse;

@Service
public class UserService {

    private final RestTemplate restTemplate = new RestTemplate();

    public UserResponse getUserId(String login)throws Exception {
        String url = "http://127.0.0.1:8000/get_id_users/" + login;
        return restTemplate.getForObject(url, UserResponse.class);
    }

    public String getUserCandidature(String userId) {
        String url = "http://127.0.0.1:8000/get_user_candidature/" + userId;
        return restTemplate.getForObject(url, String.class);
    }
}
