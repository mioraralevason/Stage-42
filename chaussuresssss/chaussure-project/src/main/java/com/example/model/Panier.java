package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class Panier {
    private int id;
    private int utilisateurId;
    private List<PanierItem> items;
    private double total;

    public Panier() {
        this.items = new ArrayList<>();
    }

    public Panier(int id, int utilisateurId) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.items = new ArrayList<>();
        this.total = 0.0;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public List<PanierItem> getItems() { return items; }
    public void setItems(List<PanierItem> items) { 
        this.items = items;
        recalculerTotal();
    }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public void addItem(PanierItem item) {
        this.items.add(item);
        recalculerTotal();
    }

    public void removeItem(int itemId) {
        this.items.removeIf(item -> item.getId() == itemId);
        recalculerTotal();
    }

    public void updateQuantity(int itemId, int newQuantity) {
        for (PanierItem item : this.items) {
            if (item.getId() == itemId) {
                item.setQuantite(newQuantity);
                break;
            }
        }
        recalculerTotal();
    }

    private void recalculerTotal() {
        this.total = 0.0;
        for (PanierItem item : this.items) {
            this.total += item.getSousTotal();
        }
    }
}