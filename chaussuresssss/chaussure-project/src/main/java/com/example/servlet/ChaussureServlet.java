package com.example.servlet;

import com.example.dao.ChaussureDao;
import com.example.dao.PanierDao;
import com.example.dao.RemiseDao;
import com.example.model.Chaussure;import com.example.dao.RemisePanierDao;
import com.example.model.PanierItem; 
import com.example.model.RemisePanier;
import com.example.model.Panier;
import com.example.model.Remise;
import com.example.model.Utilisateur;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;import java.sql.SQLException; 

import java.util.List;

public class ChaussureServlet extends HttpServlet {
    // Ajouter dans les déclarations
        private RemisePanierDao remisePanierDao = new RemisePanierDao();  // Cette ligne est correcte

    // private RemisePanierDao remisePanierDao = new RemisePanierDao();
    private ChaussureDao chaussureDao = new ChaussureDao();
    private PanierDao panierDao = new PanierDao();
    private RemiseDao remiseDao = new RemiseDao();  // AJOUTER CETTE LIGNE

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // Récupérer l'utilisateur connecté depuis la session
        int userId = getConnectedUserId(request, response);

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
                case "insert":
                    showInsertForm(request, response);
                    break;
                case "listInserted":
                    showInsertedList(request, response);
                    break;
                // AJOUTER LE CAS POUR LA GESTION DES REMISES
                case "remises":
                    showRemisesManagement(request, response);
                    break;
                case "editRemise":
                    showEditRemiseForm(request, response);
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
        String action = request.getParameter("action");
        
        if ("save".equals(action)) {
            processInsert(request, response);
        } else if ("addToCart".equals(action)) {
            int userId = getConnectedUserId(request, response);
            if (userId == -1) {
                request.getSession().setAttribute("redirectUrl", request.getRequestURI() + "?" + request.getQueryString());
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            addToCart(request, response, userId);
        } else if ("removeFromCart".equals(action)) {
            int userId = getConnectedUserId(request, response);
            if (userId == -1) {
                request.getSession().setAttribute("redirectUrl", request.getRequestURI() + "?" + request.getQueryString());
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            removeFromCart(request, response, userId);
        } else if ("updateCart".equals(action)) {
            int userId = getConnectedUserId(request, response);
            if (userId == -1) {
                request.getSession().setAttribute("redirectUrl", request.getRequestURI() + "?" + request.getQueryString());
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            updateCart(request, response, userId);
        } else if ("clearCart".equals(action)) {
            int userId = getConnectedUserId(request, response);
            if (userId == -1) {
                request.getSession().setAttribute("redirectUrl", request.getRequestURI() + "?" + request.getQueryString());
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            clearCart(request, response, userId);
        } else if ("saveRemise".equals(action)) {  // AJOUTER
            saveRemise(request, response);
        } else if ("updateRemise".equals(action)) {  // AJOUTER
            updateRemise(request, response);
        } else if ("deleteRemise".equals(action)) {  // AJOUTER
            deleteRemise(request, response);
        } else {
            doGet(request, response);
        }
    }

    private int getConnectedUserId(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object utilisateurObj = session.getAttribute("utilisateur");
            if (utilisateurObj instanceof Utilisateur) {
                Utilisateur utilisateur = (Utilisateur) utilisateurObj;
                return utilisateur.getId();
            }
        }
        return -1;
    }

 private void viewChaussures(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    // Récupérer l'utilisateur connecté
    int userId = getConnectedUserId(request, response);
    if (userId == -1) {
        // Si l'utilisateur n'est pas connecté, rediriger vers la page de connexion
        request.getSession().setAttribute("redirectUrl", request.getRequestURL().toString() + "?" + request.getQueryString());
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

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

    // Récupérer le panier de l'utilisateur connecté
    Panier panier = panierDao.getPanierByUserId(userId);

    // ============ DÉBUT DU DEBUG ============
    System.out.println("=== DEBUG REMISE PANIER ===");
    System.out.println("Panier trouvé: " + (panier != null));
    
    // CALCULER LE NOMBRE TOTAL D'ARTICLES DANS LE PANIER
    int totalArticlesPanier = 0;
    if (panier != null && panier.getItems() != null) {
        System.out.println("Nombre d'items dans panier: " + panier.getItems().size());
        for (PanierItem item : panier.getItems()) {
            System.out.println("- " + item.getChaussure().getNom() + ": " + item.getQuantite() + " unités");
            totalArticlesPanier += item.getQuantite();
        }
    }
    System.out.println("Total articles calculé: " + totalArticlesPanier);
    // ============ FIN DU DEBUG ============

    // RÉCUPÉRER LA REMISE PANIER APPLICABLE
    RemisePanier remisePanier = null;
    try {
        System.out.println("Appel DAO getRemiseApplicable avec: " + totalArticlesPanier + " articles");
        remisePanier = remisePanierDao.getRemiseApplicable(totalArticlesPanier);
        
        if (remisePanier != null) {
            System.out.println("SUCCÈS: Remise panier trouvée!");
            System.out.println("Description: " + remisePanier.getDescription());
            System.out.println("Articles min requis: " + remisePanier.getArticlesMin());
            System.out.println("Taux remise: " + remisePanier.getTauxRemise() + "%");
            System.out.println("Condition: " + totalArticlesPanier + " >= " + remisePanier.getArticlesMin() + 
                             " = " + (totalArticlesPanier >= remisePanier.getArticlesMin()));
        } else {
            System.out.println("ÉCHEC: Aucune remise panier trouvée");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Erreur lors de la récupération de la remise panier: " + e.getMessage());
    }

    // Récupérer les listes dynamiques pour les filtres
    request.setAttribute("marques", chaussureDao.getAllMarques());
    request.setAttribute("categories", chaussureDao.getAllCategories());
    request.setAttribute("types", chaussureDao.getAllTypes());
    request.setAttribute("tailles", chaussureDao.getAllTailles());
    request.setAttribute("couleurs", chaussureDao.getAllCouleurs());

    // PASSER LES DONNÉES DE REMISE À LA JSP
    request.setAttribute("remisePanier", remisePanier);
    request.setAttribute("totalArticlesPanier", totalArticlesPanier); // IMPORTANT: Ce nom
    
    request.setAttribute("chaussures", chaussures);
    request.setAttribute("panier", panier);
    request.setAttribute("title", "Vente Chaussures");
    
    // Ajuster le sous-titre selon les filtres
    String subtitle = "Découvrez notre sélection de chaussures";
    if ((keyword != null && !keyword.trim().isEmpty()) ||
        (marqueStr != null && !marqueStr.isEmpty()) ||
        (categorieStr != null && !categorieStr.isEmpty())) {
        
        int nbResultats = chaussures.size();
        if (nbResultats == 1) {
            subtitle = "1 résultat trouvé";
        } else if (nbResultats > 1) {
            subtitle = nbResultats + " résultats trouvés";
        } else {
            subtitle = "Aucun résultat trouvé";
        }
    }
    request.setAttribute("subtitle", subtitle);

    // PASSER LES PARAMÈTRES DE FILTRES POUR LA RÉTENTION DANS LE FORMULAIRE
    request.setAttribute("keywordParam", keyword != null ? keyword : "");
    request.setAttribute("marqueParam", marqueStr != null ? marqueStr : "");
    request.setAttribute("categorieParam", categorieStr != null ? categorieStr : "");
    request.setAttribute("typeParam", typeStr != null ? typeStr : "");
    request.setAttribute("tailleParam", tailleStr != null ? tailleStr : "");
    request.setAttribute("couleurParam", couleurStr != null ? couleurStr : "");
    request.setAttribute("prixMinParam", prixMinStr != null ? prixMinStr : "");
    request.setAttribute("prixMaxParam", prixMaxStr != null ? prixMaxStr : "");

    request.getRequestDispatcher("/jsp/chaussures.jsp")
           .forward(request, response);
}

    private void addToCart(HttpServletRequest request, HttpServletResponse response, int userId)
            throws IOException {
        try {
            int chaussureId = Integer.parseInt(request.getParameter("chaussureId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            boolean success = panierDao.addItemToCart(userId, chaussureId, quantity);

            if (!success) {
                // Si l'ajout a échoué, rediriger avec un message d'erreur
                response.sendRedirect(request.getContextPath() + "/chaussures?action=view&error=failedToAddToCart");
                return;
            }

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

    // ============ MÉTHODES D'INSERTION ============
    
    private void showInsertForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Récupérer les listes pour les menus déroulants
        request.setAttribute("marques", chaussureDao.getAllMarques());
        request.setAttribute("categories", chaussureDao.getAllCategories());
        request.setAttribute("tailles", chaussureDao.getAllTailles());
        request.setAttribute("couleurs", chaussureDao.getAllCouleurs());
        
        request.setAttribute("title", "Insertion de chaussures");
        request.setAttribute("subtitle", "Ajouter une nouvelle chaussure au catalogue");
        
        request.getRequestDispatcher("/jsp/insertion-chaussure.jsp")
               .forward(request, response);
    }

    private void processInsert(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Créer l'objet Chaussure à partir des paramètres
            Chaussure chaussure = new Chaussure();
            chaussure.setNom(request.getParameter("nom"));
            chaussure.setPrix(Double.parseDouble(request.getParameter("prix")));
            chaussure.setDescription(request.getParameter("description"));
            chaussure.setStock(Integer.parseInt(request.getParameter("stock")));
            
            // Définir les IDs
            String tailleId = request.getParameter("tailleId");
            String couleurId = request.getParameter("couleurId");
            String marqueId = request.getParameter("marqueId");
            String categorieId = request.getParameter("categorieId");
            
            chaussure.setTailleId(tailleId != null && !tailleId.isEmpty() ? Integer.parseInt(tailleId) : 1);
            chaussure.setCouleurId(couleurId != null && !couleurId.isEmpty() ? Integer.parseInt(couleurId) : 1);
            chaussure.setMarqueId(marqueId != null && !marqueId.isEmpty() ? Integer.parseInt(marqueId) : 1);
            chaussure.setCategorieId(categorieId != null && !categorieId.isEmpty() ? Integer.parseInt(categorieId) : 1);
            chaussure.setTypeId(1); // Par défaut
            
            // Sauvegarder dans la base
            boolean success = chaussureDao.save(chaussure);
            
            if (success) {
                // Rediriger vers la liste avec un message de succès
                HttpSession session = request.getSession();
                session.setAttribute("message", "Chaussure insérée avec succès !");
                response.sendRedirect(request.getContextPath() + "/chaussures?action=listInserted");
            } else {
                request.setAttribute("error", "Erreur lors de l'insertion");
                showInsertForm(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Veuillez vérifier les valeurs numériques");
            showInsertForm(request, response);
        }
    }

    private void showInsertedList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Récupérer toutes les chaussures
        List<Chaussure> chaussures = chaussureDao.findAll();
        
        request.setAttribute("chaussures", chaussures);
        request.setAttribute("title", "Liste des chaussures");
        request.setAttribute("subtitle", "Toutes les chaussures insérées dans le système");
        request.setAttribute("totalCount", chaussures.size());
        
        // Vérifier s'il y a un message de succès
        HttpSession session = request.getSession();
        String message = (String) session.getAttribute("message");
        if (message != null) {
            request.setAttribute("successMessage", message);
            session.removeAttribute("message");
        }
        
        request.getRequestDispatcher("/jsp/liste-chaussures.jsp")
               .forward(request, response);
    }

    // ============ MÉTHODES DE GESTION DES REMISES ============
    
    private void showRemisesManagement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Récupérer toutes les chaussures pour la liste déroulante
            List<Chaussure> chaussures = chaussureDao.findAll();
            request.setAttribute("chaussures", chaussures);
            
            // Récupérer toutes les remises existantes
            List<Remise> remises = remiseDao.getAllRemises();
            request.setAttribute("remises", remises);
            
            // Vérifier si on est en mode édition
            String editParam = request.getParameter("edit");
            if ("true".equals(editParam)) {
                int remiseId = Integer.parseInt(request.getParameter("id"));
                Remise remise = remiseDao.getRemiseById(remiseId);
                request.setAttribute("remise", remise);
            }
            
            // Définir les attributs pour la page
            request.setAttribute("title", "Gestion des Remises");
            request.setAttribute("subtitle", "Configuration des promotions");
            
            // Rediriger vers la page des remises
            request.getRequestDispatcher("/jsp/remises.jsp")
                   .forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement des remises: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp")
                   .forward(request, response);
        }
    }

    private void showEditRemiseForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int remiseId = Integer.parseInt(request.getParameter("id"));
            Remise remise = remiseDao.getRemiseById(remiseId);
            
            if (remise == null) {
                request.setAttribute("error", "Remise non trouvée");
                request.getRequestDispatcher("/jsp/error.jsp")
                       .forward(request, response);
                return;
            }
            
            // Récupérer la chaussure associée
            Chaussure chaussure = chaussureDao.findById(remise.getChaussureId());
            
            request.setAttribute("remise", remise);
            request.setAttribute("chaussure", chaussure);
            request.setAttribute("title", "Modifier la Remise");
            request.setAttribute("subtitle", "Modification de la promotion");
            
            request.getRequestDispatcher("/jsp/editRemise.jsp")
                   .forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp")
                   .forward(request, response);
        }
    }

    private void saveRemise(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int chaussureId = Integer.parseInt(request.getParameter("chaussureId"));
            int quantiteMin = Integer.parseInt(request.getParameter("quantiteMin"));
            double tauxRemise = Double.parseDouble(request.getParameter("tauxRemise"));
            
            // Vérifier si une remise existe déjà pour cette chaussure
            Remise existing = remiseDao.getRemiseByChaussureId(chaussureId);
            if (existing != null) {
                request.setAttribute("message", "Une remise existe déjà pour cette chaussure.");
                request.setAttribute("messageType", "error");
                showRemisesManagement(request, response);
                return;
            }
            
            // Créer et sauvegarder la nouvelle remise
            Remise remise = new Remise();
            remise.setChaussureId(chaussureId);
            remise.setQuantiteMin(quantiteMin);
            remise.setTauxRemise(tauxRemise);
            
            remiseDao.save(remise);
            
            request.setAttribute("message", "Remise ajoutée avec succès !");
            request.setAttribute("messageType", "success");
            
            // Rediriger vers la gestion des remises
            response.sendRedirect(request.getContextPath() + "/chaussures?action=remises");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp")
                   .forward(request, response);
        }
    }

    private void updateRemise(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int quantiteMin = Integer.parseInt(request.getParameter("quantiteMin"));
            double tauxRemise = Double.parseDouble(request.getParameter("tauxRemise"));
            
            Remise remise = remiseDao.getRemiseById(id);
            if (remise == null) {
                request.setAttribute("error", "Remise non trouvée");
                request.getRequestDispatcher("/jsp/error.jsp")
                       .forward(request, response);
                return;
            }
            
            remise.setQuantiteMin(quantiteMin);
            remise.setTauxRemise(tauxRemise);
            
            remiseDao.update(remise);
            
            request.setAttribute("message", "Remise mise à jour avec succès !");
            request.setAttribute("messageType", "success");
            
            response.sendRedirect(request.getContextPath() + "/chaussures?action=remises");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp")
                   .forward(request, response);
        }
    }

    private void deleteRemise(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            remiseDao.delete(id);
            
            request.setAttribute("message", "Remise supprimée avec succès !");
            request.setAttribute("messageType", "success");
            
            response.sendRedirect(request.getContextPath() + "/chaussures?action=remises");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp")
                   .forward(request, response);
        }
    }
}