package com.ecole._2.controller;

import com.ecole._2.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/auth")
    public String auth(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        // Appeler ton API FastAPI avec le username
        String response = userService.getUserId(username);
        
        // Ici tu peux ajouter une logique pour vérifier le password si besoin
        return "Réponse de FastAPI pour " + username + " : " + response;
    }
}
