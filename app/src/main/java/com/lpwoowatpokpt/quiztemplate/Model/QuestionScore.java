package com.lpwoowatpokpt.quiztemplate.Model;

/**
 * Created by Death on 19/10/2017.
 */

public class QuestionScore {
    private String Question_Score;
    private String User;
    private String Score;
    private String CategoryId;
    private String CategoryName;
    private String TimeStamp;
    private String Mode;

    public QuestionScore() {
    }

    public QuestionScore(String question_Score, String user, String score, String categoryId, String categoryName, String timeStamp, String mode) {
        Question_Score = question_Score;
        User = user;
        Score = score;
        CategoryId = categoryId;
        CategoryName = categoryName;
        TimeStamp = timeStamp;
        Mode = mode;
    }

    public String getQuestion_Score() {
        return Question_Score;
    }

    public void setQuestion_Score(String question_Score) {
        Question_Score = question_Score;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }
}
