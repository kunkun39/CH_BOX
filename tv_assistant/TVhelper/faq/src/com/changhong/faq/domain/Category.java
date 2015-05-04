package com.changhong.faq.domain;

import java.io.Serializable;

/**
 * Created by Jack Wang
 */
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String title;

    public Category(int id, String title) {
        this.id = id;
        this.title = title;
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
}
