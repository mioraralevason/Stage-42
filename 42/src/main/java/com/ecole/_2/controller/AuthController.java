package com.ecole._2.controller;

import com.ecole._2.models.UserResponse;
import com.ecole._2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/auth")
    public String auth(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model
    ) {
        UserResponse response = new UserResponse();
        try {
            response = userService.getUserId(username);
        } catch (Exception e) {
            System.out.println("message d'erreur: " + e.getMessage());
            model.addAttribute("error", "Erreur de connexion au serveur FastAPI");
            return "login";
        }

        // Mettre les infos dans le modèle
        model.addAttribute("login", response.getLogin());
        model.addAttribute("userId", response.getUser_id());

        // Rediriger vers home.html
        return "home";
    }
}
