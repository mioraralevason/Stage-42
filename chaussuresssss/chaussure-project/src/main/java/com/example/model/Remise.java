package com.example.model;

public class Remise {
    private int id;
    private int chaussureId;
    private int quantiteMin;
    private double tauxRemise;
    
    public Remise() {}
    
    public Remise(int id, int chaussureId, int quantiteMin, double tauxRemise) {
        this.id = id;
        this.chaussureId = chaussureId;
        this.quantiteMin = quantiteMin;
        this.tauxRemise = tauxRemise;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getChaussureId() { return chaussureId; }
    public void setChaussureId(int chaussureId) { this.chaussureId = chaussureId; }
    
    public int getQuantiteMin() { return quantiteMin; }
    public void setQuantiteMin(int quantiteMin) { this.quantiteMin = quantiteMin; }
    
    public double getTauxRemise() { return tauxRemise; }
    public void setTauxRemise(double tauxRemise) { this.tauxRemise = tauxRemise; }
}