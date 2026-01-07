package com.example.model;

public class PanierItem {
    private int id;
    private Chaussure chaussure;
    private int quantite;
    private double prixUnitaire;
    private double sousTotal;

    public PanierItem() {}

    public PanierItem(int id, Chaussure chaussure, int quantite, double prixUnitaire) {
        this.id = id;
        this.chaussure = chaussure;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = quantite * prixUnitaire;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Chaussure getChaussure() { return chaussure; }
    public void setChaussure(Chaussure chaussure) { this.chaussure = chaussure; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { 
        this.quantite = quantite;
        this.calculerSousTotal();
    }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { 
        this.prixUnitaire = prixUnitaire;
        this.calculerSousTotal();
    }

    public double getSousTotal() { return sousTotal; }
    public void setSousTotal(double sousTotal) { this.sousTotal = sousTotal; }

    private void calculerSousTotal() {
        this.sousTotal = this.quantite * this.prixUnitaire;
    }
}