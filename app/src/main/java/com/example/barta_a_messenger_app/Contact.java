package com.example.barta_a_messenger_app;

public class Contact {
    private String full_name;
    private String phone_number;
    private String uid;

    private String profilePic;

    private String status;

    private String last_messege;

    private Long messege_time;


    public Contact() {
    }

    public boolean isActive() {
        return "active".equals(this.status);
    }

    public Contact(String full_name, String phone_number, String uid, String profilePic, String status,String last_messege,Long messege_time) {
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.uid = uid;
        this.profilePic=profilePic;
        this.status=status;
    }

    public String getLast_messege() {
        return last_messege;
    }

    public void setLast_messege(String last_messege) {
        this.last_messege = last_messege;
    }

    public Long getMessege_time() {
        return messege_time;
    }

    public void setMessege_time(Long messege_time) {
        this.messege_time = messege_time;
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
}
