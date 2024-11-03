package com.example.barta_a_messenger_app;

public class Contact {
    private String full_name;
    private String phone_number;
    private String uid;
    private String profilePic;
    private String status;

    private String last_message,last_sender_name, last_message_seen;


    private Long message_time;


    public Contact() {
    }


    public Contact(String full_name, String phone_number, String uid, String profilePic, String status, String last_message, Long message_time,String last_sender_name,String last_message_seen) {
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.uid = uid;
        this.profilePic=profilePic;
        this.status=status;
        this.last_message=last_message;
        this.message_time=message_time;
        this.last_sender_name=last_sender_name;
        this.last_message_seen=last_message_seen;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public Long getMessage_time() {
        return message_time;
    }

    public void setMessage_time(Long message_time) {
        this.message_time = message_time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getProfilePic(){ return profilePic; }

    public String getStatus() {
        return status;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setProfilePic(String profilePic) { this.profilePic = profilePic;}

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLast_sender_name() {
        return last_sender_name;
    }

    public String getLast_message_seen() {
        return last_message_seen;
    }

    public void setLast_message_seen(String last_message_seen) {
        this.last_message_seen = last_message_seen;
    }

    public void setLast_sender_name(String last_sender_name) {
        this.last_sender_name = last_sender_name;
    }
}
