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
        String sql = "SELECT ajouter_au_panier(?, ?, ?) AS success";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, chaussureId);
            stmt.setInt(3, quantity);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Pour l'instant, on suppose que l'opération a réussi
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
}