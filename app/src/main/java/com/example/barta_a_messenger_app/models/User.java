package com.example.barta_a_messenger_app.models;

public class User {
    private String username;
    private String email;
    private String phone;
    private String profilePicture;
    private String status;
    private String publicKey;  // 🔐 NEW FIELD

    public User() {
    }

    public User(String username, String email, String phone, String publicKey, String status) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.profilePicture = "";
        this.status = status;
        this.publicKey = publicKey;
    }

    // Getters & setters

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getProfilePicture() { return profilePicture; }
    public String getStatus() { return status; }
    public String getPublicKey() { return publicKey; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public void setStatus(String status) { this.status = status; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
}
