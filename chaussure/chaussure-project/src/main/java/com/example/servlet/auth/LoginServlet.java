package com.example.servlet.auth;

import com.example.dao.PanierDao;
import com.example.model.Utilisateur;
import com.example.util.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {

    private PanierDao panierDao = new PanierDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Afficher la page de connexion
        request.getRequestDispatcher("/jsp/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        Utilisateur utilisateur = authenticate(email, password);
        
        if (utilisateur != null) {
            // Authentification réussie, créer une session
            HttpSession session = request.getSession();
            session.setAttribute("utilisateur", utilisateur);
            
            // Rediriger vers la page d'accueil ou la page précédente
            String redirectUrl = (String) session.getAttribute("redirectUrl");
            if (redirectUrl != null) {
                session.removeAttribute("redirectUrl");
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } else {
            // Authentification échouée
            request.setAttribute("errorMessage", "Email ou mot de passe incorrect");
            request.getRequestDispatcher("/jsp/login.jsp")
                   .forward(request, response);
        }
    }
    
    private Utilisateur authenticate(String email, String password) {
        String sql = "SELECT id, nom, email FROM utilisateurs WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(rs.getInt("id"));
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setEmail(rs.getString("email"));
                return utilisateur;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}