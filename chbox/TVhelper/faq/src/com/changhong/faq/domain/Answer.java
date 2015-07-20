package com.changhong.faq.domain;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Jack Wang
 */
public class Answer implements Serializable {

    private int id;

    private String sequence;

    private String result;

    public static String getAnswer(Set<String> positions) {
        StringBuffer buffer = new StringBuffer();
        for (String position : positions) {
            if (position.equals("0")) {
                buffer.append("A,");
            } else if (position.equals("1")) {
                buffer.append("B,");
            } else if (position.equals("2")) {
                buffer.append("C,");
            } else if (position.equals("3")) {
                buffer.append("D,");
            } else if (position.equals("4")) {
                buffer.append("E,");
            } else if (position.equals("5")) {
                buffer.append("F,");
            } else {
                buffer.append("G,");
            }
        }
        return buffer.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
