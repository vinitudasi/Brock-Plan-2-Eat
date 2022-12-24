package com.example.foodplaceviewer;
public class ChatModel {

    private String message;
    private String sender;


    //Getters and Setters for Message
    public String getMessage() {return message;}
    public void setMessage(String message) {
        this.message = message;
    }

    //Getters and Setters for Sender
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }


    //Constructor
    public ChatModel(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }
}