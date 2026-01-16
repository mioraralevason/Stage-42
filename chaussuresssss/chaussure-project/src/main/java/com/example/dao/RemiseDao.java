package com.example.dao;

import com.example.model.Remise;
import com.example.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RemiseDao {
    private Connection connection;
    
    public RemiseDao() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion à la base", e);
        }
    }
    
    public void save(Remise remise) throws SQLException {
        String sql = "INSERT INTO remise (chaussure_id, quantite_min, taux_remise) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, remise.getChaussureId());
            stmt.setInt(2, remise.getQuantiteMin());
            stmt.setDouble(3, remise.getTauxRemise());
            stmt.executeUpdate();
        }
    }
    
    public void update(Remise remise) throws SQLException {
        String sql = "UPDATE remise SET quantite_min = ?, taux_remise = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, remise.getQuantiteMin());
            stmt.setDouble(2, remise.getTauxRemise());
            stmt.setInt(3, remise.getId());
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM remise WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public Remise getRemiseById(int id) throws SQLException {
        String sql = "SELECT * FROM remise WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRemise(rs);
            }
        }
        return null;
    }
    
    public Remise getRemiseByChaussureId(int chaussureId) throws SQLException {
        String sql = "SELECT * FROM remise WHERE chaussure_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chaussureId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRemise(rs);
            }
        }
        return null;
    }
    
    public List<Remise> getAllRemises() throws SQLException {
        List<Remise> remises = new ArrayList<>();
        String sql = "SELECT * FROM remise";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                remises.add(mapResultSetToRemise(rs));
            }
        }
        return remises;
    }
    
    private Remise mapResultSetToRemise(ResultSet rs) throws SQLException {
        Remise remise = new Remise();
        remise.setId(rs.getInt("id"));
        remise.setChaussureId(rs.getInt("chaussure_id"));
        remise.setQuantiteMin(rs.getInt("quantite_min"));
        remise.setTauxRemise(rs.getDouble("taux_remise"));
        return remise;
    }
}