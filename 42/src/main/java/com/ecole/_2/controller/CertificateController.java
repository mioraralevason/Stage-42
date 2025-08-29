package com.ecole._2.controller;

import com.ecole._2.services.CertificateService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping("/certificate")
        public ResponseEntity<?> getCertificate(
                @RequestParam("login") String login,
                @RequestParam(value = "signer_par", defaultValue = "Aucune") String signerPar,
                HttpSession session
        ) {
        if (signerPar.isEmpty() || signerPar == null ||
                (!signerPar.equalsIgnoreCase("Aucune") &&
                !signerPar.equalsIgnoreCase("Directeur") &&
                !signerPar.equalsIgnoreCase("Assistant"))) {
                signerPar = "Aucune";
        }

        if (session.getAttribute("kind") != null) {
                String kind = (String) session.getAttribute("kind");
                if (!kind.equals("admin")) {
                String sessionLogin = (String) ((com.ecole._2.models.UserResponse) session.getAttribute("userResponse")).getLogin();
                login = sessionLogin;
                }
        }

        if (login == null || login.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("{\"error\":\"Le paramètre 'login' est requis\"}");
        }

        try {
                Resource pdf = certificateService.generateCertificate(login, signerPar);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=certificat_scolarite_" + login + ".pdf")
                        .body(pdf);
        } catch (Exception e) {
                return ResponseEntity
                        .status(500)
                        .body("{\"error\":\"Erreur lors de la génération du certificat\"}");
        }
        }

}
