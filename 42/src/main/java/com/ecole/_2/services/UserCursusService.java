package com.ecole._2.services;

import com.ecole._2.models.CursusUser;
import com.ecole._2.models.CursusUserList;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserCursusService {

    private static final String BASE_URL = "https://api.intra.42.fr/v2/users/";

    public CursusUserList getUserCursus(String userId, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        String url = BASE_URL + userId + "/cursus_users";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<CursusUser[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                CursusUser[].class
        );

        return new CursusUserList(userId, Arrays.asList(response.getBody()));
    }
}
