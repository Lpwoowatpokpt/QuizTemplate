package com.lpwoowatpokpt.quiztemplate.Model;

/**
 * Created by Death on 26/12/2017.
 */

public class Report {
    private String questionID;
    private String message;

    public Report() {
    }

    public Report(String questionID, String message) {
        this.questionID = questionID;
        this.message = message;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
