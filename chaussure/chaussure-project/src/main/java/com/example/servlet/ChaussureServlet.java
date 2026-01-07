package com.example.servlet;

import com.example.dao.ChaussureDao;
import com.example.dao.PanierDao;
import com.example.model.Chaussure;
import com.example.model.Panier;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ChaussureServlet extends HttpServlet {
    
    private ChaussureDao chaussureDao = new ChaussureDao();
    private PanierDao panierDao = new PanierDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        int userId = 1; // Pour l'exemple, à remplacer par l'utilisateur réellement connecté
        
        if (action != null) {
            switch (action) {
                case "view":
                    viewChaussures(request, response);
                    break;
                case "addToCart":
                    addToCart(request, response, userId);
                    break;
                case "removeFromCart":
                    removeFromCart(request, response, userId);
                    break;
                case "updateCart":
                    updateCart(request, response, userId);
                    break;
                case "clearCart":
                    clearCart(request, response, userId);
                    break;
                default:
                    viewChaussures(request, response);
            }
        } else {
            viewChaussures(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void viewChaussures(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupérer les paramètres de filtrage
        String marqueStr = request.getParameter("marque");
        String categorieStr = request.getParameter("categorie");
        String typeStr = request.getParameter("type");
        String tailleStr = request.getParameter("taille");
        String couleurStr = request.getParameter("couleur");
        String prixMinStr = request.getParameter("prixMin");
        String prixMaxStr = request.getParameter("prixMax");
        String keyword = request.getParameter("keyword");

        List<Chaussure> chaussures;

        // Si des filtres sont appliqués, effectuer la recherche/filtrage
        if ((marqueStr != null && !marqueStr.isEmpty()) ||
            (categorieStr != null && !categorieStr.isEmpty()) ||
            (typeStr != null && !typeStr.isEmpty()) ||
            (tailleStr != null && !tailleStr.isEmpty()) ||
            (couleurStr != null && !couleurStr.isEmpty()) ||
            (prixMinStr != null && !prixMinStr.isEmpty()) ||
            (prixMaxStr != null && !prixMaxStr.isEmpty()) ||
            (keyword != null && !keyword.trim().isEmpty())) {

            // Convertir les paramètres en valeurs appropriées
            Integer marqueId = (marqueStr != null && !marqueStr.isEmpty()) ? Integer.parseInt(marqueStr) : null;
            Integer categorieId = (categorieStr != null && !categorieStr.isEmpty()) ? Integer.parseInt(categorieStr) : null;
            Integer typeId = (typeStr != null && !typeStr.isEmpty()) ? Integer.parseInt(typeStr) : null;

            // Pour le filtre de taille, nous devons d'abord récupérer l'ID correspondant
            Integer tailleId = null;
            if (tailleStr != null && !tailleStr.isEmpty()) {
                // On va chercher l'ID de la taille dans la base de données
                tailleId = chaussureDao.getTailleIdByValue(tailleStr);
            }

            Integer couleurId = (couleurStr != null && !couleurStr.isEmpty()) ? Integer.parseInt(couleurStr) : null;
            Double prixMin = (prixMinStr != null && !prixMinStr.isEmpty()) ? Double.parseDouble(prixMinStr) : null;
            Double prixMax = (prixMaxStr != null && !prixMaxStr.isEmpty()) ? Double.parseDouble(prixMaxStr) : null;

            if (keyword != null && !keyword.trim().isEmpty()) {
                chaussures = chaussureDao.searchByKeyword(keyword);
            } else {
                chaussures = chaussureDao.findByFilters(marqueId, categorieId, typeId, tailleId, couleurId, prixMin, prixMax);
            }
        } else {
            // Si aucun filtre n'est appliqué, afficher toutes les chaussures
            chaussures = chaussureDao.findAll();
        }

        // Récupérer le panier de l'utilisateur
        int userId = 1; // Pour l'exemple, à remplacer par l'utilisateur réellement connecté
        Panier panier = panierDao.getPanierByUserId(userId);

        // Récupérer les listes dynamiques pour les filtres
        request.setAttribute("marques", chaussureDao.getAllMarques());
        request.setAttribute("categories", chaussureDao.getAllCategories());
        request.setAttribute("types", chaussureDao.getAllTypes());
        request.setAttribute("tailles", chaussureDao.getAllTailles());
        request.setAttribute("couleurs", chaussureDao.getAllCouleurs());

        request.setAttribute("chaussures", chaussures);
        request.setAttribute("panier", panier);
        request.setAttribute("title", "Vente Chaussures");
        request.setAttribute("subtitle", "Découvrez notre sélection de chaussures");

        request.getRequestDispatcher("/jsp/chaussures.jsp")
               .forward(request, response);
    }

    private void addToCart(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws IOException {
        try {
            int chaussureId = Integer.parseInt(request.getParameter("chaussureId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            
            boolean success = panierDao.addItemToCart(userId, chaussureId, quantity);
            
            response.sendRedirect(request.getContextPath() + "/chaussures?action=view");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides");
        }
    }

    private void removeFromCart(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws IOException {
        try {
            int chaussureId = Integer.parseInt(request.getParameter("chaussureId"));
            
            boolean success = panierDao.removeItemFromCart(userId, chaussureId);
            
            response.sendRedirect(request.getContextPath() + "/chaussures?action=view");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides");
        }
    }

    private void updateCart(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws IOException {
        try {
            int chaussureId = Integer.parseInt(request.getParameter("chaussureId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            
            boolean success = panierDao.updateItemQuantity(userId, chaussureId, quantity);
            
            response.sendRedirect(request.getContextPath() + "/chaussures?action=view");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides");
        }
    }

    private void clearCart(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws IOException {
        boolean success = panierDao.clearCart(userId);
        response.sendRedirect(request.getContextPath() + "/chaussures?action=view");
    }
}