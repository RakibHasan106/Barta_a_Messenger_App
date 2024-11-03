package com.example.barta_a_messenger_app;

public class Request {
    private String name;
    private String phone;
    private String senderUid;
    private String receiverUid;
    private String status;

    public Request() {
    }

    public Request(String name, String phone, String senderUid, String receiverUid, String status) {
        this.name = name;
        this.phone = phone;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
