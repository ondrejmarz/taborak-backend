package cz.ondrejmarz.taborakserver.model;

import java.util.Objects;

public class TourUser {
    private String userId;
    private String userName;
    private String email;
    private String role;

    public TourUser() {
    }

    public TourUser(String userId, String userName, String email, String role) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, email, role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TourUser tourUser = (TourUser) o;
        return Objects.equals(userId, tourUser.userId) && Objects.equals(userName, tourUser.userName) && Objects.equals(email, tourUser.email) && Objects.equals(role, tourUser.role);
    }
}
