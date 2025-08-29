package com.ecole._2.models;

import java.util.List;
import java.util.stream.Collectors;

public class CursusUserList {
    private String userId;
    private List<CursusUser> cursus_users;

    public CursusUserList() {}

    public CursusUserList(String userId, List<CursusUser> cursus_users) {
        this.userId = userId;
        this.cursus_users = cursus_users;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CursusUser> getCursusUsers() {
        return cursus_users;
    }

    public void setCursusUsers(List<CursusUser> cursus_users) {
        this.cursus_users = cursus_users;
    }

    /**
     * Filtre les CursusUser par grade.
     * @param grade le grade à filtrer (ex: "Cadet", "Pisciner")
     * @return CursusUser correspondant au grade
     */
    public CursusUser filterByGrade(String grade) {
        if (cursus_users == null || grade == null || grade.isEmpty()) {
            return null;
        }

        return cursus_users.stream()
                .filter(c -> grade.equalsIgnoreCase(c.getGrade()))
                .findFirst()
                .orElse(null);
    }
}
