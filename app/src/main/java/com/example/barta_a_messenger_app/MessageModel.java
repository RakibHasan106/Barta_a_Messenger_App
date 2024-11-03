package com.example.barta_a_messenger_app;

public class MessageModel {
    String uid, message, messageId;
    String messageType;
    String isNotified;
    Long timestamp;

    public MessageModel(String uid, String message, Long timestamp) {
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
        messageType = "msg";
    }

    public MessageModel(String uid, String message,  String messageType) {
        this.uid = uid;
        this.message = message;
        this.messageType = messageType;
    }

    public MessageModel(String uid, String message) {
        this.uid = uid;
        this.message = message;
        messageType = "msg";
    }

    public MessageModel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getIsNotified() {
        return isNotified;
    }

    public void setIsNotified(String isNotified) {
        this.isNotified = isNotified;
    }
}
