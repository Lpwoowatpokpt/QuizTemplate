package com.lpwoowatpokpt.quiztemplate.Model;

public class User {
    private String userName;
    private String photoUrl;
    private String online;
    private int balance;

    public User() {
    }

    public User(String userName, String photoUrl, String online, int balance) {
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.online = online;
        this.balance = balance;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}


