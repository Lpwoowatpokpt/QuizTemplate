package com.lpwoowatpokpt.quiztemplate.Model;

public class QuestionSuggest {
    private String  AnswerA, AnswerB, AnswerC, AnswerD, CorrectAnswer, ImageUrl, IsImageQuestion, Question, CategoryID, Hint, QuestionID, userName, Status;

    public QuestionSuggest() {
    }

    public QuestionSuggest(String answerA, String answerB, String answerC, String answerD, String correctAnswer, String imageUrl, String isImageQuestion, String question, String categoryID, String hint, String questionID, String userName, String status) {
        AnswerA = answerA;
        AnswerB = answerB;
        AnswerC = answerC;
        AnswerD = answerD;
        CorrectAnswer = correctAnswer;
        ImageUrl = imageUrl;
        IsImageQuestion = isImageQuestion;
        Question = question;
        CategoryID = categoryID;
        Hint = hint;
        QuestionID = questionID;
        this.userName = userName;
        Status = status;
    }

    public String getAnswerA() {
        return AnswerA;
    }

    public void setAnswerA(String answerA) {
        AnswerA = answerA;
    }

    public String getAnswerB() {
        return AnswerB;
    }

    public void setAnswerB(String answerB) {
        AnswerB = answerB;
    }

    public String getAnswerC() {
        return AnswerC;
    }

    public void setAnswerC(String answerC) {
        AnswerC = answerC;
    }

    public String getAnswerD() {
        return AnswerD;
    }

    public void setAnswerD(String answerD) {
        AnswerD = answerD;
    }

    public String getCorrectAnswer() {
        return CorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        CorrectAnswer = correctAnswer;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getIsImageQuestion() {
        return IsImageQuestion;
    }

    public void setIsImageQuestion(String isImageQuestion) {
        IsImageQuestion = isImageQuestion;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getHint() {
        return Hint;
    }

    public void setHint(String hint) {
        Hint = hint;
    }

    public String getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(String questionID) {
        QuestionID = questionID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
