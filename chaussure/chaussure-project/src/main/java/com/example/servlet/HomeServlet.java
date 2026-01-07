package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HomeServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Définir l'attribut de message pour la page JSP
        request.setAttribute("message", "Bienvenue sur Chaussure Shop ! Découvrez notre sélection exclusive de chaussures en vous rendant sur la page Vente Chaussures.");
        request.setAttribute("title", "Accueil");
        request.setAttribute("subtitle", "Page d'accueil");
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/jsp/home.jsp")
               .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}