package com.ecole._2.controller;

import com.ecole._2.models.TokenResponse;
import com.ecole._2.services.User42Service;
import com.ecole._2.services.UserLocationStatsFilterService;
import com.ecole._2.services.UserLocationStatsService;
import com.ecole._2.models.User;
import com.ecole._2.models.UserLocationStat;
import com.ecole._2.services.ApiService;
import com.ecole._2.services.CampusUsersService;
import com.ecole._2.services.OAuth42Service;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private OAuth42Service oauth42Service;
    
    @Autowired
    private User42Service user42Service;
    
    @Autowired
    private CampusUsersService campusUsersService;
    
    @Autowired
    private ApiService apiService;

    @Autowired
    private UserLocationStatsService userLocationStatsService;

    @Autowired
    private UserLocationStatsFilterService userLocationStatsFilterService;

    private static String CAMPUS_ID = "65";
    
    @GetMapping("/auth")
    public String auth(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            Model model,
            HttpSession session
    ) {
        try {
            logger.info("Début du processus d'authentification avec code: {}", code);

            TokenResponse tokenResponse = (TokenResponse) session.getAttribute("tokenResponse");
            User userResponse = (User) session.getAttribute("userResponse");

            // Si la session contient déjà les infos, on les utilise
            if (tokenResponse != null && userResponse != null) {
                logger.info("Infos utilisateur déjà présentes en session: {} (ID: {})", userResponse.getLogin(), userResponse.getId());
            } else {
                tokenResponse = oauth42Service.getAccessToken(code);
                if (tokenResponse == null) {
                    logger.error("Échec de récupération du token d'accès");
                    model.addAttribute("error", "Erreur d'authentification");
                    return "error-page";
                }

                session.setAttribute("tokenResponse", tokenResponse);
                session.setAttribute("code", code);
                session.setAttribute("state", state);

                // Retry logic for user42Service.getUserInfo
                int maxRetries = 3;
                int attempt = 0;
                while (attempt < maxRetries) {
                    try {
                        userResponse = user42Service.getUserInfo(tokenResponse.getAccessToken());
                        break;
                    } catch (HttpClientErrorException e) {
                        if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                            attempt++;
                            String retryAfter = e.getResponseHeaders().getFirst("Retry-After");
                            long waitTime = retryAfter != null ? Long.parseLong(retryAfter) * 1000 : 1000 * attempt;
                            logger.warn("429 Too Many Requests, retrying after {}ms (attempt {}/{})", waitTime, attempt, maxRetries);
                            try {
                                Thread.sleep(waitTime);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException("Interrupted during retry wait", ie);
                            }
                        } else {
                            throw e;
                        }
                    }
                }

                if (userResponse == null) {
                    logger.error("Échec de récupération des informations utilisateur après {} tentatives", maxRetries);
                    model.addAttribute("error", "Erreur lors de la récupération des informations utilisateur");
                    return "error-page";
                }

                session.setAttribute("userResponse", userResponse);
                logger.info("Utilisateur authentifié: {} (ID: {})", userResponse.getLogin(), userResponse.getId());
            }

            String userKind = "admin";
            session.setAttribute("kind", userKind);
            model.addAttribute("kind", userKind);
            model.addAttribute("userResponse", userResponse);

            logger.info("Authentification réussie pour utilisateur: {} (Type: {})", userResponse.getLogin(), userKind);

        } catch (Exception e) {
            logger.error("Erreur lors du processus d'authentification", e);
            model.addAttribute("error", "Erreur d'authentification: " + e.getMessage());
            return "error-page";
        }

        return "certificat-page";
    }
    
    private String determineUserKind(User user) {
        if (user.getKind() != null) {
            return user.getKind();
        }

        if (isAdminUser(user)) {
            return "admin";
        }
        
        return "student";
    }
    
    private boolean isAdminUser(User user) {
        String[] adminLogins = {"admin", "root", "supervisor"};
        
        if (user.getLogin() != null) {
            for (String adminLogin : adminLogins) {
                if (user.getLogin().toLowerCase().contains(adminLogin)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}