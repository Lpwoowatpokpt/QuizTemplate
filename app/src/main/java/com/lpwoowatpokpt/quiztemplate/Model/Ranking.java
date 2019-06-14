package com.lpwoowatpokpt.quiztemplate.Model;

/**
 * Created by death on 10.01.18.
 */

public class Ranking {
    private String userName;
    private String photoUrl;
    private long score;

    public Ranking() {
    }

    public Ranking(String userName, String photoUrl, long score) {
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.score = score;
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

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
