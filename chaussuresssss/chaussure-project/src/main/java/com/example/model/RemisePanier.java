package com.example.model;

public class RemisePanier {
    private int id;
    private int articlesMin;
    private double tauxRemise;
    private String description;
    private boolean active;
    
    // Constructeurs
    public RemisePanier() {}
    
    public RemisePanier(int id, int articlesMin, double tauxRemise, String description, boolean active) {
        this.id = id;
        this.articlesMin = articlesMin;
        this.tauxRemise = tauxRemise;
        this.description = description;
        this.active = active;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getArticlesMin() { return articlesMin; }
    public void setArticlesMin(int articlesMin) { this.articlesMin = articlesMin; }
    
    public double getTauxRemise() { return tauxRemise; }
    public void setTauxRemise(double tauxRemise) { this.tauxRemise = tauxRemise; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}