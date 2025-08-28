package com.ecole._2.models;

import java.util.List;

public class User {
    private int id;
    private String email;
    private String login;
    private String first_name;
    private String last_name;
    private String usual_full_name;
    private Image image;
    private List<CursusUser> cursus_users;

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsual_full_name() {
        return usual_full_name;
    }

    public void setUsual_full_name(String usual_full_name) {
        this.usual_full_name = usual_full_name;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<CursusUser> getCursus_users() {
        return cursus_users;
    }

    public void setCursus_users(List<CursusUser> cursus_users) {
        this.cursus_users = cursus_users;
    }
}
