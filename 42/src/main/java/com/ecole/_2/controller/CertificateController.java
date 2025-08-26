package com.ecole._2.controller;

import com.ecole._2.services.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping("/certificate")
    public ResponseEntity<Resource> getCertificate(
            @RequestParam("login") String login,
            @RequestParam(value = "signer_par", defaultValue = "Aucune") String signerPar
    ) {
        Resource pdf = certificateService.generateCertificate(login, signerPar);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=certificat_scolarite_" + login + ".pdf")
                .body(pdf);
    }
}
