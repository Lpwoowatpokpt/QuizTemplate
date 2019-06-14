package com.lpwoowatpokpt.quiztemplate.Model;

public class MultiplayerGame {
    private String CategoryId;
    private String Player1;
    private String Player2;
    private String Score1;
    private String Score2;
    private int step;

    public MultiplayerGame() {
    }

    public MultiplayerGame(String categoryId, String player1, String player2, String score1, String score2, int step) {
        CategoryId = categoryId;
        Player1 = player1;
        Player2 = player2;
        Score1 = score1;
        Score2 = score2;
        this.step = step;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getPlayer1() {
        return Player1;
    }

    public void setPlayer1(String player1) {
        Player1 = player1;
    }

    public String getPlayer2() {
        return Player2;
    }

    public void setPlayer2(String player2) {
        Player2 = player2;
    }

    public String getScore1() {
        return Score1;
    }

    public void setScore1(String score1) {
        Score1 = score1;
    }

    public String getScore2() {
        return Score2;
    }

    public void setScore2(String score2) {
        Score2 = score2;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
