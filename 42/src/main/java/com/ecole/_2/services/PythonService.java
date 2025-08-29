package com.ecole._2.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecole._2.models.TokenResponse;
import com.ecole._2.models.User;

@Service
public class PythonService {

    private final RestTemplate restTemplate = new RestTemplate();

    public User getUserId(String login)throws Exception {
        String url = "http://127.0.0.1:8000/get_id_users/" + login;
        return restTemplate.getForObject(url, User.class);
    }

    public String getUserCandidature(String userId) {
        String url = "http://127.0.0.1:8000/get_user_candidature/" + userId;
        return restTemplate.getForObject(url, String.class);
    }

    public User getUser(String login) {
        String url = "http://127.0.0.1:8000/get_user/" + login;
        return restTemplate.getForObject(url, User.class);
    }

    public String getTokenAdminUser() {
    String url = "http://127.0.0.1:8000/get_access_token";
    TokenResponse response = restTemplate.getForObject(url, TokenResponse.class);
    return (response != null) ? response.getAccessToken() : null;
}
}
