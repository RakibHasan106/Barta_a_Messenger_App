package com.example.barta_a_messenger_app;

public class MessageModel {
    String uid, message, messageId;
    String fileType;
    Long timestamp;

    public MessageModel(String uid, String message, Long timestamp) {
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
        fileType = "msg";
    }

    public MessageModel(String uid, String message,  String fileType) {
        this.uid = uid;
        this.message = message;
        this.fileType = fileType;
    }

    public MessageModel(String uid, String message) {
        this.uid = uid;
        this.message = message;
        fileType = "msg";
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
