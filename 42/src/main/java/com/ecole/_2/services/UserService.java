package com.ecole._2.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getUserId(String login) {
        String url = "http://127.0.0.1:8000/get_id_users/" + login;
        return restTemplate.getForObject(url, String.class);
    }
}
