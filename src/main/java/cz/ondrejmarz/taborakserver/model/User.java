package cz.ondrejmarz.taborakserver.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import java.util.*;

@Document(collectionName = "users")
public class User {

    @DocumentId
    private String userId;
    private String userName;
    private String email;
    private Map<String, String> roles = new HashMap<>();

    public User() {
    }

    public User(String userId, String userName, String email, Map<String, String> roles) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.roles = roles != null? roles : new HashMap<>();
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

    public Map<String, String> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, String> roles) {
        this.roles = roles;
    }

    public void addRole(String key, String value) {
        roles.put(key, value);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, email, roles);
    }
}
