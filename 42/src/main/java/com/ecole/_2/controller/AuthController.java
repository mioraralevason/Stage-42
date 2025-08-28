package com.ecole._2.controller;

import com.ecole._2.models.User;
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
        User user;
        try {
            user = userService.getUser(username);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur connexion FastAPI");
            return "login";
        }

        model.addAttribute("user", user);
        return "home";
    }
}
