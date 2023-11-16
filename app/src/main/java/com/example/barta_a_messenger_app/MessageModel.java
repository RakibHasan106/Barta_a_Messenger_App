package com.example.barta_a_messenger_app;

public class MessageModel {
    String uid, message, messageId;
    Boolean isItImage;
    Long timestamp;

    public MessageModel(String uid, String message, Long timestamp) {
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
        isItImage=false;
    }

    public MessageModel(String uid, String message,  Boolean isItImage) {
        this.uid = uid;
        this.message = message;
        this.isItImage = isItImage;
    }

    public MessageModel(String uid, String message) {
        this.uid = uid;
        this.message = message;
        isItImage=false;
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

    public Boolean getItImage() {
        return isItImage;
    }

    public void setItImage(Boolean itImage) {
        isItImage = itImage;
    }
}
