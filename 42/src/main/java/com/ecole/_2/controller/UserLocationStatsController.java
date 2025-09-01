package com.ecole._2.controller;

import com.ecole._2.models.CursusUser;
import com.ecole._2.models.Freeze;
import com.ecole._2.models.UserLocationStat;
import com.ecole._2.services.ApiService;
import com.ecole._2.services.UserCursusService;
import com.ecole._2.services.UserLocationStatsService;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class UserLocationStatsController {
    @Autowired
    private UserLocationStatsService userLocationStatsService;

    @Autowired
    private UserCursusService userCursusService;

    @Autowired
    private ApiService apiService;

    @GetMapping("/freeze")
    public String getLocationsStats(@RequestParam("login") String login, HttpSession session, Model model) {
        String userId = null;
        String kind = null;
        if(userId == null) {
            kind = (String) session.getAttribute("kind");
            if (kind == null || (!kind.equals("admin"))) {
                userId = "211018";
                // userId = ((User) session.getAttribute("userResponse")).getId();    
            }else{
                if(login != null && !login.isEmpty()){
                    try {
                        userId = apiService.getIdUsers(login,apiService.getAccessToken());
                    }
                    catch (Exception err) {
                        model.addAttribute("error", err.getMessage());
                        return new CertificateController().auth(model, session);
                    }
                    // userId = "203988";
                }
            }
            if(userId == null){
                model.addAttribute("error", "User Login is required");
                return new CertificateController().auth(model, session);
            }
        }
        try {
            String tokenAdmin = apiService.getAccessToken();
            CursusUser userCursus = userCursusService.getUserCursus(userId, tokenAdmin).filterByGrade("Cadet");
            UserLocationStat userLocationStat = userLocationStatsService.getUserLocationStats(userId, tokenAdmin);
            
            Freeze freeze = new Freeze();
            freeze.setA(userLocationStat.getNbDays(userCursus.getBegin_at(),null));
            freeze.setB(userLocationStat.getNbOpenDays(userCursus.getBegin_at(), null));
            freeze.setC(userLocationStat.getTotalHours(userCursus.getBegin_at(), null));
            freeze.setD(userCursus.getMilestone());

            model.addAttribute("freeze", freeze.calculFreeze());
            model.addAttribute("login", getStringValue(apiService.getUser(userId, apiService.getAccessToken()), "login", ""));
            if (kind.equals("admin")) {
                model.addAttribute("locationStats", userLocationStat);
                model.addAttribute("userCursus", userCursus);
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return new CertificateController().auth(model, session);
        }
        return "freeze-page";
    }

    @GetMapping("/freeze-begin")
    public String getLocationsStats(HttpSession session, Model model) {
        String userId = null;
        String kind = null;
        if(userId == null) {
            kind = (String) session.getAttribute("kind");
            if (kind == null || (!kind.equals("admin"))) {
                userId = "211018";
                // userId = ((User) session.getAttribute("userResponse")).getId();    
            }else{
                userId = "203988";
            }
            if(userId == null){
                model.addAttribute("error", "User Login is required");
                return new CertificateController().auth(model, session);
            }
        }
        try {
            String tokenAdmin = apiService.getAccessToken();
            CursusUser userCursus = userCursusService.getUserCursus(userId, tokenAdmin).filterByGrade("Cadet");
            UserLocationStat userLocationStat = userLocationStatsService.getUserLocationStats(userId, tokenAdmin);
            
            Freeze freeze = new Freeze();
            freeze.setA(userLocationStat.getNbDays(userCursus.getBegin_at(),null));
            freeze.setB(userLocationStat.getNbOpenDays(userCursus.getBegin_at(), null));
            freeze.setC(userLocationStat.getTotalHours(userCursus.getBegin_at(), null));
            freeze.setD(userCursus.getMilestone());

            model.addAttribute("login", getStringValue(apiService.getUser(userId, apiService.getAccessToken()), "login", ""));
            model.addAttribute("freeze", freeze.calculFreeze());
            if (kind.equals("admin")) {
                model.addAttribute("locationStats", userLocationStat);
                model.addAttribute("userCursus", userCursus);
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return new CertificateController().auth(model, session);
        }
        return "freeze-page";
    }
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        if (map == null) return defaultValue;
        Object value = map.get(key);
        return (value != null) ? value.toString() : defaultValue;
    }
}
