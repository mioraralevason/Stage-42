package com.ecole._2.controller;

import com.ecole._2.models.TokenResponse;
import com.ecole._2.models.User;
import com.ecole._2.models.UserLocationStat;
import com.ecole._2.services.ApiService;
import com.ecole._2.services.CampusUsersService;
import com.ecole._2.services.UserLocationStatsFilterService;
import com.ecole._2.services.UserLocationStatsService;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckingController {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckingController.class);
    
    @Autowired
    private CampusUsersService campusUsersService;
    
    @Autowired
    private ApiService apiService;
    
    @Autowired
    private UserLocationStatsService userLocationStatsService;
    
    @Autowired
    private UserLocationStatsFilterService userLocationStatsFilterService;
    
    private static final String CAMPUS_ID = "65";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @GetMapping("/check")
    public String checkPage(Model model, HttpSession session) {
        try {
            // Vérifier si l'utilisateur est authentifié
            TokenResponse tokenResponse = (TokenResponse) session.getAttribute("tokenResponse");
            User userResponse = (User) session.getAttribute("userResponse");
            
            if (tokenResponse == null || userResponse == null) {
                logger.warn("User not authenticated, redirecting to login");
                return "redirect:/login";
            }
            
            // Ajouter les informations utilisateur au modèle
            model.addAttribute("userResponse", userResponse);
            model.addAttribute("kind", session.getAttribute("kind"));
            
            // Définir les dates par défaut (aujourd'hui)
            String today = LocalDate.now().format(DATE_FORMATTER);
            model.addAttribute("startDate", today);
            model.addAttribute("endDate", today);
            
            logger.info("Displaying checking page for user: {}", userResponse.getLogin());
            
        } catch (Exception e) {
            logger.error("Error loading checking page", e);
            model.addAttribute("error", "Erreur lors du chargement de la page");
            return "error-page";
        }
        
        return "checking-page";
    }
    
    @PostMapping("/check")
    public String checkStudents(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "pool", required = false, defaultValue = "September") String pool,
            @RequestParam(value = "year", required = false, defaultValue = "2025") String year,
            Model model,
            HttpSession session
    ) {
        try {
            // Vérifier si l'utilisateur est authentifié
            TokenResponse tokenResponse = (TokenResponse) session.getAttribute("tokenResponse");
            User userResponse = (User) session.getAttribute("userResponse");
            
            if (tokenResponse == null || userResponse == null) {
                logger.warn("User not authenticated, redirecting to login");
                return "redirect:/login";
            }
            
            // Valider les dates
            if (!isValidDateRange(startDate, endDate)) {
                model.addAttribute("error", "Dates invalides. La date de fin doit être postérieure ou égale à la date de début.");
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);
                model.addAttribute("pool", pool);
                model.addAttribute("year", year);
                return "checking-page";
            }
            
            logger.info("Checking students presence from {} to {} for pool {} {}", 
                       startDate, endDate, pool, year);
            
            // Récupérer tous les utilisateurs du campus
            List<User> userList = campusUsersService.getAllCampusUsers(CAMPUS_ID, tokenResponse.getAccessToken());
            logger.info("Retrieved {} users from campus {}", userList.size(), CAMPUS_ID);
            
            // Filtrer par pool et année
            userList = User.filterUsersByPool(userList, pool, year);
            logger.info("Filtered to {} users for pool {} {}", userList.size(), pool, year);
            
            // Récupérer les statistiques de localisation
            List<UserLocationStat> userLocationStats = userLocationStatsService.getUserLocationStatsFromUsers(userList, apiService);
            logger.info("Retrieved location stats for {} users", userLocationStats.size());
            
            // Filtrer par plage de dates
            userLocationStats = userLocationStatsFilterService.filterUserLocationStatsByDateRange(
                userLocationStats, startDate, endDate);
            
            int studentCount = userLocationStats.size();
            logger.info("Found {} students present between {} and {}", studentCount, startDate, endDate);
            
            // Ajouter les résultats au modèle
            model.addAttribute("userResponse", userResponse);
            model.addAttribute("kind", session.getAttribute("kind"));
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("pool", pool);
            model.addAttribute("year", year);
            model.addAttribute("studentCount", studentCount);
            model.addAttribute("userLocationStats", userLocationStats);
            model.addAttribute("searchPerformed", true);
            
        } catch (Exception e) {
            logger.error("Error during student checking process", e);
            model.addAttribute("error", "Erreur lors de la vérification des présences: " + e.getMessage());
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("pool", pool);
            model.addAttribute("year", year);
        }
        
        return "checking-page";
    }
    
    private boolean isValidDateRange(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
            LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
            return !end.isBefore(start);
        } catch (DateTimeParseException e) {
            logger.warn("Invalid date format: startDate={}, endDate={}", startDate, endDate);
            return false;
        }
    }
}