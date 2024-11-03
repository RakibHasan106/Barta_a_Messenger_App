package com.example.barta_a_messenger_app;

public class User {
    private String username;
    private String email;

    private String phone;
    private String profilePicture;
    private String status;

    public User() {
    }



    public User(String username, String email, String phone,String profilePicture, String status) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.profilePicture = profilePicture;
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getStatus() {
        return status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
