package com.ecole._2.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CertificateService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Resource generateCertificate(String login, String signerPar) {
        String url = "http://127.0.0.1:8000/generate_certificate/" + login + "?signer_par=" + signerPar;
        System.out.println("url : " + url);
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return new ByteArrayResource(response.getBody());
        } else {
            throw new RuntimeException("Erreur lors de la génération du certificat");
        }
    }
}
