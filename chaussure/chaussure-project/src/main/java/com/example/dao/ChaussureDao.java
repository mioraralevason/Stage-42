package com.example.dao;

import com.example.model.Chaussure;
import com.example.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChaussureDao implements Dao<Chaussure> {

    @Override
    public List<Chaussure> findAll() {
        List<Chaussure> chaussures = new ArrayList<>();
        String sql = "SELECT c.id, c.photo, c.nom, c.prix, c.description, c.stock, " +
                    "t.valeur as taille, co.nom as couleur, m.nom as marque, cat.nom as categorie, tc.nom as type_chaussure " +
                    "FROM chaussures c " +
                    "LEFT JOIN tailles t ON t.id = c.taille_id " +
                    "LEFT JOIN couleurs co ON co.id = c.couleur_id " +
                    "LEFT JOIN marques m ON m.id = c.marque_id " +
                    "LEFT JOIN categories cat ON cat.id = c.categorie_id " +
                    "LEFT JOIN types_chaussures tc ON tc.id = c.type_chaussure_id " +
                    "ORDER BY c.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Chaussure chaussure = mapResultSetToChaussure(rs);
                chaussures.add(chaussure);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chaussures;
    }

    @Override
    public Chaussure findById(int id) {
        String sql = "SELECT c.id, c.photo, c.nom, c.prix, c.description, c.stock, " +
                    "t.valeur as taille, co.nom as couleur, m.nom as marque, cat.nom as categorie, tc.nom as type_chaussure " +
                    "FROM chaussures c " +
                    "LEFT JOIN tailles t ON t.id = c.taille_id " +
                    "LEFT JOIN couleurs co ON co.id = c.couleur_id " +
                    "LEFT JOIN marques m ON m.id = c.marque_id " +
                    "LEFT JOIN categories cat ON cat.id = c.categorie_id " +
                    "LEFT JOIN types_chaussures tc ON tc.id = c.type_chaussure_id " +
                    "WHERE c.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToChaussure(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Chaussure> findByFilters(Integer marqueId, Integer categorieId, Integer typeId, 
                                        Integer tailleId, Integer couleurId, Double prixMin, Double prixMax) {
        List<Chaussure> chaussures = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("SELECT c.id, c.photo, c.nom, c.prix, c.description, c.stock, " +
                    "t.valeur as taille, co.nom as couleur, m.nom as marque, cat.nom as categorie, tc.nom as type_chaussure " +
                    "FROM chaussures c " +
                    "LEFT JOIN tailles t ON t.id = c.taille_id " +
                    "LEFT JOIN couleurs co ON co.id = c.couleur_id " +
                    "LEFT JOIN marques m ON m.id = c.marque_id " +
                    "LEFT JOIN categories cat ON cat.id = c.categorie_id " +
                    "LEFT JOIN types_chaussures tc ON tc.id = c.type_chaussure_id " +
                    "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (marqueId != null) {
            sql.append("AND c.marque_id = ? ");
            params.add(marqueId);
        }
        if (categorieId != null) {
            sql.append("AND c.categorie_id = ? ");
            params.add(categorieId);
        }
        if (typeId != null) {
            sql.append("AND c.type_chaussure_id = ? ");
            params.add(typeId);
        }
        if (tailleId != null) {
            sql.append("AND c.taille_id = ? ");
            params.add(tailleId);
        }
        if (couleurId != null) {
            sql.append("AND c.couleur_id = ? ");
            params.add(couleurId);
        }
        if (prixMin != null) {
            sql.append("AND c.prix >= ? ");
            params.add(prixMin);
        }
        if (prixMax != null) {
            sql.append("AND c.prix <= ? ");
            params.add(prixMax);
        }

        sql.append("ORDER BY c.created_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Chaussure chaussure = mapResultSetToChaussure(rs);
                chaussures.add(chaussure);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chaussures;
    }

    public List<Chaussure> searchByKeyword(String keyword) {
        List<Chaussure> chaussures = new ArrayList<>();
        String sql = "SELECT c.id, c.photo, c.nom, c.prix, c.description, c.stock, " +
                    "t.valeur as taille, co.nom as couleur, m.nom as marque, cat.nom as categorie, tc.nom as type_chaussure " +
                    "FROM chaussures c " +
                    "LEFT JOIN tailles t ON t.id = c.taille_id " +
                    "LEFT JOIN couleurs co ON co.id = c.couleur_id " +
                    "LEFT JOIN marques m ON m.id = c.marque_id " +
                    "LEFT JOIN categories cat ON cat.id = c.categorie_id " +
                    "LEFT JOIN types_chaussures tc ON tc.id = c.type_chaussure_id " +
                    "WHERE c.nom ILIKE ? OR c.description ILIKE ? OR m.nom ILIKE ? " +
                    "ORDER BY c.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchTerm = "%" + keyword + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Chaussure chaussure = mapResultSetToChaussure(rs);
                chaussures.add(chaussure);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chaussures;
    }

    private Chaussure mapResultSetToChaussure(ResultSet rs) throws SQLException {
        Chaussure chaussure = new Chaussure();
        chaussure.setId(rs.getInt("id"));
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
        return chaussure;
    }

    @Override
    public boolean save(Chaussure entity) {
        // Implémentation pour sauvegarder une chaussure
        // Cette méthode n'est pas nécessaire immédiatement
        return false;
    }

    @Override
    public boolean update(Chaussure entity) {
        // Implémentation pour mettre à jour une chaussure
        // Cette méthode n'est pas nécessaire immédiatement
        return false;
    }

    @Override
    public boolean delete(int id) {
        // Implémentation pour supprimer une chaussure
        // Cette méthode n'est pas nécessaire immédiatement
        return false;
    }

    // Méthodes pour récupérer les listes de filtres depuis la base de données
    public List<String[]> getAllMarques() {
        List<String[]> marques = new ArrayList<>();
        String sql = "SELECT id, nom FROM marques ORDER BY nom";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] marque = {String.valueOf(rs.getInt("id")), rs.getString("nom")};
                marques.add(marque);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marques;
    }

    public List<String[]> getAllCategories() {
        List<String[]> categories = new ArrayList<>();
        String sql = "SELECT id, nom FROM categories ORDER BY nom";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] categorie = {String.valueOf(rs.getInt("id")), rs.getString("nom")};
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<String[]> getAllTypes() {
        List<String[]> types = new ArrayList<>();
        String sql = "SELECT id, nom FROM types_chaussures ORDER BY nom";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] type = {String.valueOf(rs.getInt("id")), rs.getString("nom")};
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    public List<String[]> getAllTailles() {
        List<String[]> tailles = new ArrayList<>();
        String sql = "SELECT id, valeur FROM tailles ORDER BY valeur::INTEGER";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] taille = {String.valueOf(rs.getInt("id")), rs.getString("valeur")};
                tailles.add(taille);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tailles;
    }

    public List<String[]> getAllCouleurs() {
        List<String[]> couleurs = new ArrayList<>();
        String sql = "SELECT id, nom FROM couleurs ORDER BY nom";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] couleur = {String.valueOf(rs.getInt("id")), rs.getString("nom")};
                couleurs.add(couleur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return couleurs;
    }

    public Integer getTailleIdByValue(String tailleValue) {
        String sql = "SELECT id FROM tailles WHERE valeur = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tailleValue);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retourne null si la taille n'est pas trouvée
    }
}