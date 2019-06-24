package com.lpwoowatpokpt.quiztemplate.Model;

import com.google.firebase.firestore.Exclude;

public class User {
    private String userId;
    private String tokenId;
    private String userName;
    private String photoUrl;
    private int balance;
    private boolean online;
    private boolean isAdmin;

    public User() {
    }

    public User(String tokenId, String userName, String photoUrl, int balance, boolean online, boolean isAdmin) {
        this.tokenId = tokenId;
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.balance = balance;
        this.online = online;
        this.isAdmin = isAdmin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}


