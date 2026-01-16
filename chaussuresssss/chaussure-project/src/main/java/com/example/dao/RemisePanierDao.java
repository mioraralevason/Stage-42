package com.example.dao;

import com.example.model.RemisePanier;
import com.example.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RemisePanierDao {
    
    public RemisePanier getRemiseApplicable(int totalArticles) throws SQLException {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    System.out.println("=== DEBUG DAO: Recherche remise pour " + totalArticles + " articles ===");
    
    try {
        conn = DatabaseConnection.getConnection();
        System.out.println("Connexion BD: OK");
        
        String sql = "SELECT * FROM remise_panier WHERE articles_min <= ? AND active = TRUE ORDER BY articles_min DESC LIMIT 1";
        System.out.println("SQL exécuté: " + sql);
        System.out.println("Paramètre SQL: " + totalArticles);
        
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, totalArticles);
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            System.out.println("Résultat BD TROUVÉ:");
            System.out.println("  - ID: " + rs.getInt("id"));
            System.out.println("  - articles_min: " + rs.getInt("articles_min"));
            System.out.println("  - taux_remise: " + rs.getDouble("taux_remise"));
            System.out.println("  - description: " + rs.getString("description"));
            System.out.println("  - active: " + rs.getBoolean("active"));
            
            RemisePanier remise = new RemisePanier();
            remise.setId(rs.getInt("id"));
            remise.setArticlesMin(rs.getInt("articles_min"));
            remise.setTauxRemise(rs.getDouble("taux_remise"));
            remise.setDescription(rs.getString("description"));
            remise.setActive(rs.getBoolean("active"));
            
            return remise;
        } else {
            System.out.println("Aucun résultat trouvé en base de données");
            
            // Vérifier ce qu'il y a dans la table
            Statement stmt = conn.createStatement();
            ResultSet allRs = stmt.executeQuery("SELECT COUNT(*) as count FROM remise_panier");
            if (allRs.next()) {
                int count = allRs.getInt("count");
                System.out.println("Nombre de remises en BD: " + count);
                if (count == 0) {
                    System.out.println("ATTENTION: La table remise_panier est VIDE!");
                }
            }
            allRs.close();
            stmt.close();
            
            return null;
        }
        
    } catch (SQLException e) {
        System.err.println("ERREUR SQL dans getRemiseApplicable: " + e.getMessage());
        e.printStackTrace();
        throw e;
    } finally {
        if (rs != null) try { rs.close(); } catch (SQLException e) {}
        if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
        if (conn != null) try { conn.close(); } catch (SQLException e) {}
    }
}
    
    // Méthode pour avoir toutes les remises panier
    public List<RemisePanier> getAllRemisesPanier() throws SQLException {
        List<RemisePanier> remises = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM remise_panier ORDER BY articles_min");
            
            while (rs.next()) {
                RemisePanier remise = new RemisePanier();
                remise.setId(rs.getInt("id"));
                remise.setArticlesMin(rs.getInt("articles_min"));
                remise.setTauxRemise(rs.getDouble("taux_remise"));
                remise.setDescription(rs.getString("description"));
                remise.setActive(rs.getBoolean("active"));
                remises.add(remise);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
        return remises;
    }
}