package com.example.george.traffictracker;


import java.util.Date;

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private String messageUserId;
    private long messageTime;
    private String messagePlace;

    public ChatMessage(){}

    public ChatMessage(String messageText, String messageUser, String messageUserId, String messagePlace) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
        this.messageUserId = messageUserId;
        this.messagePlace = messagePlace;
    }


    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessagePlace(){
        return messagePlace;
    }
    public void setMessagePlace(String messagePlace){
        this.messagePlace = messagePlace;
    }
}