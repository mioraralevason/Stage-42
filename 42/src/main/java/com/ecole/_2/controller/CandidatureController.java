package com.ecole._2.controller;

import com.ecole._2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CandidatureController {

    @Autowired
    private UserService userService;

    @PostMapping("/candidature")
    public String candidature(
            @RequestParam("userId") String userId,
            Model model
    ) {
        String candidature = "";
        try {
            candidature = userService.getUserCandidature(userId);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération de la candidature");
            return "login";
        }

        model.addAttribute("userId", userId);
        model.addAttribute("candidature", candidature);

        return "candidature";
    }
}
