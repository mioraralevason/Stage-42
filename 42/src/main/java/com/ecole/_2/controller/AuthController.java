package com.ecole._2.controller;

import com.ecole._2.models.CursusUser;
import com.ecole._2.models.CursusUserList;
import com.ecole._2.models.TokenResponse;
import com.ecole._2.services.User42Service;
import com.ecole._2.services.UserCursusService;
import com.ecole._2.services.UserLocationStatsFilterService;
import com.ecole._2.services.UserLocationStatsService;
import com.ecole._2.models.User;
import com.ecole._2.models.UserLocationStat;
import com.ecole._2.services.ApiService;
import com.ecole._2.services.CampusUserService;
import com.ecole._2.services.OAuth42Service;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    
    // private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private OAuth42Service oauth42Service;
    
    @Autowired
    private User42Service user42Service;
    
    @Autowired
    private CampusUserService campusUserService;
    
    @Autowired
    private ApiService apiService;

    @Autowired
    private UserCursusService userCursusService;

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
            // logger.info("Début du processus d'authentification avec code: {}", code);
 
            TokenResponse tokenResponse = oauth42Service.getAccessToken(code);
            if (tokenResponse == null) {
                // logger.error("Échec de récupération du token d'accès");
                model.addAttribute("error", "Erreur d'authentification");
                return "error-page";
            }
            
            session.setAttribute("tokenResponse", tokenResponse);
            session.setAttribute("code", code);
            session.setAttribute("state", state);
            
            
            User userResponse = user42Service.getUserInfo(tokenResponse.getAccessToken());
            if (userResponse == null) {
                // logger.error("Échec de récupération des informations utilisateur");
                model.addAttribute("error", "Erreur lors de la récupération des informations utilisateur");
                return "error-page";
            }
            
            session.setAttribute("userResponse", userResponse);
            // logger.info("Utilisateur authentifié: {} (ID: {})", userResponse.getLogin(), userResponse.getId());
            
            String tokenAdmin = apiService.getAccessToken();
            session.setAttribute("listLogin", campusUserService.getCampusUsers(CAMPUS_ID, tokenAdmin).getUserLogins(tokenAdmin));

            CursusUserList cursusUserList = userCursusService.getAllCursusUsers(tokenAdmin);
            List<CursusUser> cursusUsers = cursusUserList.filterByPoolMonthAndYear("september", "2025");

            List<UserLocationStat> userLocationStats = userLocationStatsService.getUserLocationStatsFromCursusUsers(cursusUsers, tokenAdmin);

            userLocationStats = userLocationStatsFilterService.filterUserLocationStatsByDateRange(userLocationStats, "2025-09-01", "2025-09-01");

            session.setAttribute("listUserLocationStats", userLocationStats.size());
            session.setAttribute("listCursusUsers", cursusUsers);

            // String userKind = determineUserKind(userResponse);
            String userKind = "admin";
            session.setAttribute("kind", userKind);
            model.addAttribute("kind", userKind);
            model.addAttribute("userResponse", userResponse);
            
            // logger.info("Authentification réussie pour utilisateur: {} (Type: {})", userResponse.getLogin(), userKind);
            
        } catch (Exception e) {
            // logger.error("Erreur lors du processus d'authentification", e);
            model.addAttribute("error", "Erreur d'authentification: " + e.getMessage());
            return "error-page";
        }
        
        return "certificat-page";
    }
    
    /**
     * Détermine le type d'utilisateur (admin ou student)
     * Vous pouvez adapter cette logique selon vos critères
     */
    private String determineUserKind(User user) {
        
        if (user.getKind() != null) {
            return user.getKind();
        }

        if (isAdminUser(user)) {
            return "admin";
        }
        
        return "student";
    }
    
    /**
     * Vérifie si l'utilisateur est un administrateur
     * À personnaliser selon vos critères
     */
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