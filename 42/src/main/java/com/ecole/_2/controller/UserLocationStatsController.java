package com.ecole._2.controller;

import com.ecole._2.models.CursusUser;
import com.ecole._2.models.Freeze;
import com.ecole._2.models.UserLocationStat;
import com.ecole._2.services.ApiService;
import com.ecole._2.services.UserCursusService;
import com.ecole._2.services.UserLocationStatsService;

import jakarta.servlet.http.HttpSession;

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
    public String getLocationsStats(@RequestParam("user_id") String userId, HttpSession session, Model model) {
        userId = "211018";
        if(userId == null) {
            model.addAttribute("error", "User ID is required");
            return new CertificateController().auth(model, session);
        }
        String kind = (String) session.getAttribute("kind");
        if (kind == null || (!kind.equals("admin"))) {
            userId = "203988";
            // userId = ((User) session.getAttribute("userResponse")).getId();
        }
        try {
            String tokenAdmin = apiService.getAccessToken();
            CursusUser userCursus = userCursusService.getUserCursus(userId, tokenAdmin).filterByGrade("Cadet");
            UserLocationStat userLocationStat = userLocationStatsService.getUserLocationStats(userId, tokenAdmin);

            model.addAttribute("locationStats", userLocationStat);
            model.addAttribute("userCursus", userCursus);
            Freeze freeze = new Freeze();
            freeze.setA(userLocationStat.getNbDays(userCursus.getBegin_at(),null));
            freeze.setB(userLocationStat.getNbOpenDays(userCursus.getBegin_at(), null));
            freeze.setC(userLocationStat.getTotalHours(userCursus.getBegin_at(), null));
            freeze.setD(userCursus.getMilestone());
            model.addAttribute("freeze", freeze.calculFreeze());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return new CertificateController().auth(model, session);
        }


        return "freeze-page";
    }
}
