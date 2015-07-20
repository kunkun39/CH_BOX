package com.changhong.faq.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class Examination implements Serializable {

    private int id;

    private String title;

    private String description;

    private List<Question> questions;

    public void addQuestion(Question question) {
        if (questions == null) {
            questions = new ArrayList<Question>();
        }
        questions.add(question);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }


}
