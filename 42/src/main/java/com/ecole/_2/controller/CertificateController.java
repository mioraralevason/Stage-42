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

import java.util.HashMap;
import java.util.Map;

@Controller
public class CertificateController {
    
    @Autowired
    private CertificateService certificateService;
    
    @GetMapping("/certificate-generator")
    public ResponseEntity<?> getCertificate(
            @RequestParam("login") String login,
            @RequestParam(value = "signer_par", defaultValue = "Aucune") String signerPar,
            HttpSession session,
            Model model
    ) {
        try {
            User user = (User) session.getAttribute("userResponse");
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Session expirée. Veuillez vous reconnecter.");
                return ResponseEntity.status(401)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(error);
            }
            
            // Validation et nettoyage des paramètres
            if (!signerPar.equalsIgnoreCase("Aucune") &&
                !signerPar.equalsIgnoreCase("Directeur") &&
                !signerPar.equalsIgnoreCase("Assistant")) {
                signerPar = "Aucune";
            }
            
            // Si ce n'est pas un admin, utiliser le login de la session
            if (!"admin".equals(session.getAttribute("kind"))) {
                login = user.getLogin();
            }
            
            // Validation du login
            if (login == null || login.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Login requis pour générer le certificat.");
                return ResponseEntity.status(400)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(error);
            }
            
            Resource pdf = certificateService.generateCertificate(login, signerPar);
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"certificat_scolarite_" + login + ".pdf\"")
                .body(pdf);
                
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Paramètre invalide: " + e.getMessage());
            return ResponseEntity.status(400)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
                
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la génération: " + e.getMessage());
            return ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
                
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur interne du serveur. Veuillez réessayer plus tard.");
            return ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }
    
    @GetMapping("/certificate")
    public String auth(Model model, HttpSession session) {
        User userResponse = (User) session.getAttribute("userResponse");
        
        if (userResponse == null) {
            return "redirect:/login"; // Rediriger vers la page de login si pas d'utilisateur
        }
        
        // Ajouter les objets au model pour Thymeleaf
        model.addAttribute("userResponse", userResponse);
        model.addAttribute("kind", userResponse.getKind());
        session.setAttribute("kind", userResponse.getKind());
        
        return "certificat-page";
    }
}