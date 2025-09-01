package com.ecole._2.controller;

import com.ecole._2.models.TokenResponse;
import com.ecole._2.models.User;
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

    @GetMapping("/certificate-generator")
        public ResponseEntity<Resource> getCertificate(
        @RequestParam("login") String login,
        @RequestParam(value = "signer_par", defaultValue = "Aucune") String signerPar,
        HttpSession session,
        Model model
        ) {
        User user = (User) session.getAttribute("userResponse");
        
        if (user == null) {
                return ResponseEntity.status(401).build(); // Unauthorized
        }

        if (!signerPar.equalsIgnoreCase("Aucune") &&
                !signerPar.equalsIgnoreCase("Directeur") &&
                !signerPar.equalsIgnoreCase("Assistant")) {
                signerPar = "Aucune";
        }

        if (!"admin".equals(session.getAttribute("kind"))) {
                login = user.getLogin();
        }

        try {
                Resource pdf = certificateService.generateCertificate(login, signerPar);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"certificat_scolarite_" + login + ".pdf\"")
                        .body(pdf);
                        
        } catch (Exception e) {
                return ResponseEntity.status(500).build();
        }
        }


        @GetMapping("/certificate")
        public String auth(
            Model model,
            HttpSession session
    ) {

        User userResponse = (User) session.getAttribute("userResponse");
        // Ajouter les objets au model pour Thymeleaf
        model.addAttribute("userResponse", session.getAttribute("userResponse"));
        model.addAttribute("kind", userResponse.getKind());
        session.setAttribute("kind", userResponse.getKind());
        
        return "certificat-page";
    }

}
