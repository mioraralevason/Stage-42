package com.example.dao;

import com.example.model.*;
import com.example.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PanierDao {

    public Panier getPanierByUserId(int userId) {
        Panier panier = null;
        String sql = "SELECT p.id FROM paniers p WHERE p.utilisateur_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int panierId = rs.getInt("id");
                panier = new Panier(panierId, userId);
                panier.setItems(getItemsByPanierId(panierId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (panier == null) {
            panier = createNewPanier(userId);
        }

        return panier;
    }

    private List<PanierItem> getItemsByPanierId(int panierId) {
        List<PanierItem> items = new ArrayList<>();
        String sql = "SELECT ep.id, ep.quantite, ep.prix_unitaire, " +
                    "c.id as chaussure_id, c.photo, c.nom, c.prix, c.description, c.stock, " +
                    "t.valeur as taille, co.nom as couleur, m.nom as marque, cat.nom as categorie, tc.nom as type_chaussure " +
                    "FROM elements_panier ep " +
                    "JOIN chaussures c ON c.id = ep.chaussure_id " +
                    "LEFT JOIN tailles t ON t.id = c.taille_id " +
                    "LEFT JOIN couleurs co ON co.id = c.couleur_id " +
                    "LEFT JOIN marques m ON m.id = c.marque_id " +
                    "LEFT JOIN categories cat ON cat.id = c.categorie_id " +
                    "LEFT JOIN types_chaussures tc ON tc.id = c.type_chaussure_id " +
                    "WHERE ep.panier_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, panierId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Créer la chaussure
                Chaussure chaussure = new Chaussure();
                chaussure.setId(rs.getInt("chaussure_id"));
                chaussure.setPhoto(rs.getString("photo"));
                chaussure.setNom(rs.getString("nom"));
                chaussure.setPrix(rs.getDouble("prix"));
                chaussure.setDescription(rs.getString("description"));
                chaussure.setStock(rs.getInt("stock"));
                chaussure.setTaille(rs.getString("taille"));
                chaussure.setCouleur(rs.getString("couleur"));
                chaussure.setMarque(rs.getString("marque"));
                chaussure.setCategorie(rs.getString("categorie"));
                chaussure.setType(rs.getString("type_chaussure"));

                // Créer l'élément du panier
                PanierItem item = new PanierItem(
                    rs.getInt("id"),
                    chaussure,
                    rs.getInt("quantite"),
                    rs.getDouble("prix_unitaire")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public boolean addItemToCart(int userId, int chaussureId, int quantity) {
        boolean success = false;

        // Trouver ou créer le panier de l'utilisateur
        int panierId = getOrCreatePanierId(userId);
        if (panierId == -1) {
            System.err.println("Échec de création/récupération du panier pour l'utilisateur: " + userId);
            return false;
        }

        System.out.println("Panier trouvé/créé avec ID: " + panierId);

        // Vérifier si la chaussure est déjà dans le panier
        String checkSql = "SELECT id, quantite FROM elements_panier WHERE panier_id = ? AND chaussure_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, panierId);
            checkStmt.setInt(2, chaussureId);

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // La chaussure est déjà dans le panier, on met à jour la quantité
                int existingItemId = rs.getInt("id");
                int existingQuantity = rs.getInt("quantite");
                int newQuantity = existingQuantity + quantity;

                // Vérifier le stock disponible
                String stockSql = "SELECT stock FROM chaussures WHERE id = ?";
                try (PreparedStatement stockStmt = conn.prepareStatement(stockSql)) {
                    stockStmt.setInt(1, chaussureId);
                    ResultSet stockRs = stockStmt.executeQuery();
                    if (stockRs.next()) {
                        int stockDisponible = stockRs.getInt("stock");
                        System.out.println("Stock disponible: " + stockDisponible + ", Quantité demandée: " + newQuantity);

                        if (stockDisponible >= newQuantity) {
                            String updateSql = "UPDATE elements_panier SET quantite = ? WHERE id = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                updateStmt.setInt(1, newQuantity);
                                updateStmt.setInt(2, existingItemId);
                                int rowsUpdated = updateStmt.executeUpdate();
                                success = rowsUpdated > 0;
                                System.out.println("Mise à jour du panier - Lignes affectées: " + rowsUpdated);
                            }
                        } else {
                            System.err.println("Stock insuffisant - Disponible: " + stockDisponible + ", Demandé: " + newQuantity);
                            return false;
                        }
                    } else {
                        System.err.println("Chaussure non trouvée dans la base de données: " + chaussureId);
                        return false;
                    }
                }
            } else {
                // Vérifier que la chaussure existe et a suffisamment de stock
                String chaussureCheckSql = "SELECT stock, prix FROM chaussures WHERE id = ?";
                try (PreparedStatement chaussureStmt = conn.prepareStatement(chaussureCheckSql)) {
                    chaussureStmt.setInt(1, chaussureId);
                    ResultSet chaussureRs = chaussureStmt.executeQuery();

                    if (chaussureRs.next()) {
                        int stockDisponible = chaussureRs.getInt("stock");
                        double prixUnitaire = chaussureRs.getDouble("prix");

                        if (stockDisponible >= quantity) {
                            // La chaussure n'est pas dans le panier, on l'ajoute
                            String insertSql = "INSERT INTO elements_panier (panier_id, chaussure_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";

                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setInt(1, panierId);
                                insertStmt.setInt(2, chaussureId);
                                insertStmt.setInt(3, quantity);
                                insertStmt.setDouble(4, prixUnitaire);

                                int rowsAffected = insertStmt.executeUpdate();
                                success = rowsAffected > 0;
                                System.out.println("Insertion dans le panier - Lignes affectées: " + rowsAffected);
                            }
                        } else {
                            System.err.println("Stock insuffisant pour la nouvelle insertion - Disponible: " + stockDisponible + ", Demandé: " + quantity);
                            return false;
                        }
                    } else {
                        System.err.println("Chaussure non trouvée dans la base de données: " + chaussureId);
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur SQL dans addItemToCart: " + e.getMessage());
        }

        return success;
    }

    public boolean removeItemFromCart(int userId, int chaussureId) {
        boolean success = false;
        String sql = "SELECT supprimer_du_panier(?, ?) AS success";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, chaussureId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean updateItemQuantity(int userId, int chaussureId, int newQuantity) {
        boolean success = false;
        String sql = "SELECT mettre_a_jour_quantite_panier(?, ?, ?) AS success";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, chaussureId);
            stmt.setInt(3, newQuantity);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean clearCart(int userId) {
        boolean success = false;
        String sql = "SELECT vider_panier(?) AS success";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    private Panier createNewPanier(int userId) {
        String sql = "INSERT INTO paniers (utilisateur_id) VALUES (?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int panierId = rs.getInt("id");
                return new Panier(panierId, userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int getOrCreatePanierId(int userId) {
        int panierId = -1;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Désactiver l'auto-commit pour gérer les transactions
            conn.setAutoCommit(false);

            // Essayer de trouver un panier existant
            String selectSql = "SELECT id FROM paniers WHERE utilisateur_id = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, userId);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    panierId = rs.getInt("id");
                } else {
                    // Pas de panier existant, en créer un nouveau
                    // Utilisation d'une requête séparée pour la création et récupération de l'ID
                    String insertSql = "INSERT INTO paniers (utilisateur_id) VALUES (?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.executeUpdate();
                    }

                    // Maintenant, récupérer l'ID nouvellement inséré
                    String selectNewIdSql = "SELECT id FROM paniers WHERE utilisateur_id = ? ORDER BY id DESC LIMIT 1";
                    try (PreparedStatement selectNewIdStmt = conn.prepareStatement(selectNewIdSql)) {
                        selectNewIdStmt.setInt(1, userId);
                        ResultSet newIdRs = selectNewIdStmt.executeQuery();

                        if (newIdRs.next()) {
                            panierId = newIdRs.getInt("id");
                        }
                    }
                }
            }

            conn.commit(); // Valider la transaction
        } catch (SQLException e) {
            e.printStackTrace();
            // Tenter de récupérer l'ID si une erreur s'est produite
            try {
                String fallbackSelectSql = "SELECT id FROM paniers WHERE utilisateur_id = ? ORDER BY created_at DESC LIMIT 1";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement fallbackStmt = conn.prepareStatement(fallbackSelectSql)) {
                    fallbackStmt.setInt(1, userId);
                    ResultSet rs = fallbackStmt.executeQuery();
                    if (rs.next()) {
                        panierId = rs.getInt("id");
                    }
                }
            } catch (SQLException fallbackException) {
                fallbackException.printStackTrace();
            }
        }

        return panierId;
    }

    public boolean utilisateurExiste(int userId) {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int creerUtilisateurParDefaut(String nom) {
        String sql = "INSERT INTO utilisateurs (nom, email, mot_de_passe) VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            stmt.setString(2, "temp_" + System.currentTimeMillis() + "@example.com");
            stmt.setString(3, "password"); // En production, utiliser un mot de passe hashé

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1; // Retourne l'ID 1 par défaut si la création échoue
    }
}