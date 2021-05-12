package com.example.deming;

// Clasa pentru User, contine membrii corespunzatori

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String accountType;

    public User(int id, String username, String email, String password, String accountType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", accountType='" + accountType + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAccountType() {
        return accountType;
    }
}
