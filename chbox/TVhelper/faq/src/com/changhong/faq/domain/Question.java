package com.changhong.faq.domain;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Jack Wang
 */
public class Question implements Serializable {

    private int sequence;
    private static final String TAG = "Question" ;
    private String title;

    private QuestionType questionType;

    private List<Answer> answers;

    public void addAnswer(Answer answer) {
        if (answers == null) {
            answers = new ArrayList<Answer>();
        }
        answers.add(answer);
    }

    public String getAnswerShow(Set<String> indexes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("问题" + sequence + ": " + getTitle() + "\n");
        String selected = Answer.getAnswer(indexes);
        Log.i(TAG, selected);
        
        for (Answer answer : answers) {
        	Log.i(TAG, answer.getSequence());
            if (selected.indexOf(answer.getSequence() + ",") >= 0) {
                buffer.append(answer.getSequence() + " : " + answer.getResult() + "\n");
            }
        }

        buffer.append("\n");
        return buffer.toString();
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
