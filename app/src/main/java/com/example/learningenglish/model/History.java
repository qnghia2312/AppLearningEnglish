package com.example.learningenglish.model;

import java.util.Date;

public class History {
    private String username;
    private String type;
    private String content;
    private String result;
    private Date date;

    public History(String username, String type, String content, String result) {
        this.username = username;
        this.type = type;
        this.content = content;
        this.result = result;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
