package com.ecole._2.controller;

import com.ecole._2.models.TokenResponse;
import com.ecole._2.services.User42Service;
import com.ecole._2.models.User;
import com.ecole._2.services.OAuth42Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private OAuth42Service oauth42Service;

    @Autowired
    private User42Service user42Service;

    @GetMapping("/auth")
    public String auth(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            Model model,
            HttpSession session
    ) {
        // Vérifie si les données sont déjà en sessios
        TokenResponse tokenResponse = (TokenResponse) session.getAttribute("tokenResponse");
        tokenResponse = oauth42Service.getAccessToken(code);
        session.setAttribute("tokenResponse", tokenResponse);
        session.setAttribute("code", code);
        session.setAttribute("state", state);

        User userResponse = (User) session.getAttribute("userResponse");
        if (userResponse == null) {
            userResponse = user42Service.getUserInfo(tokenResponse.getAccessToken());
            session.setAttribute("userResponse", userResponse);
        }

        // Ajouter les objets au model pour Thymeleaf
        model.addAttribute("userResponse", session.getAttribute("userResponse"));
        model.addAttribute("kind", userResponse.getKind());
        session.setAttribute("kind", userResponse.getKind());
        // model.addAttribute("kind", "admin");
        
        return "home";
    }
}
