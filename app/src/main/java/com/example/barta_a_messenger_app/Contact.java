package com.example.barta_a_messenger_app;

public class Contact {
    private String full_name;
    private String phone_number;

    public Contact() {
    }

    public Contact(String full_name, String phone_number) {
        this.full_name = full_name;
        this.phone_number = phone_number;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
