package com.ecole._2.models;

import java.util.List;

public class CursusUserList {
    private String userId;
    private List<CursusUser> cursus_users;

    public CursusUserList() {}

    public CursusUserList(String userId, List<CursusUser> cursus_users) {
        this.setUserId(userId);
        this.setCursusUsers(cursus_users);
    }

    public String getUserId() throws IllegalStateException {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalStateException("User ID is not set");
        }
        return userId;
    }

    public void setUserId(String userId) throws IllegalArgumentException {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        this.userId = userId;
    }

    public List<CursusUser> getCursusUsers() throws IllegalStateException {
        if (cursus_users == null || cursus_users.isEmpty()) {
            throw new IllegalStateException("Cursus users list is empty");
        }
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
    public CursusUser filterByGrade(String grade)throws IllegalStateException {
        List<CursusUser> cursusUserList = getCursusUsers();
        CursusUser result = cursusUserList.stream()
                .filter(c -> grade.equalsIgnoreCase(c.getGrade()))
                .findFirst()
                .orElse(null);
        if (result == null) {
            throw new IllegalStateException("No CursusUser found with grade: " + grade);
        }
        return result;
    }
}
