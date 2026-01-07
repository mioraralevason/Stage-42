package com.example.model;

public class Chaussure {
    private int id;
    private String photo;
    private String nom;
    private double prix;
    private int tailleId;
    private String taille;
    private int couleurId;
    private String couleur;
    private String description;
    private int marqueId;
    private String marque;
    private int categorieId;
    private String categorie;
    private int typeId;
    private String type;
    private int stock;

    public Chaussure() {}

    public Chaussure(int id, String photo, String nom, double prix, String taille, String couleur, 
                     String description, String marque, String categorie, String type, int stock) {
        this.id = id;
        this.photo = photo;
        this.nom = nom;
        this.prix = prix;
        this.taille = taille;
        this.couleur = couleur;
        this.description = description;
        this.marque = marque;
        this.categorie = categorie;
        this.type = type;
        this.stock = stock;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    
    public int getTailleId() { return tailleId; }
    public void setTailleId(int tailleId) { this.tailleId = tailleId; }
    
    public String getTaille() { return taille; }
    public void setTaille(String taille) { this.taille = taille; }
    
    public int getCouleurId() { return couleurId; }
    public void setCouleurId(int couleurId) { this.couleurId = couleurId; }
    
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getMarqueId() { return marqueId; }
    public void setMarqueId(int marqueId) { this.marqueId = marqueId; }
    
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    
    public int getCategorieId() { return categorieId; }
    public void setCategorieId(int categorieId) { this.categorieId = categorieId; }
    
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}