package com.wefriend.wefriend.Main.Friends;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String profileImageUrl;

    public Chat(String sender, String receiver, String message, String profileImageUrl){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.profileImageUrl = profileImageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
