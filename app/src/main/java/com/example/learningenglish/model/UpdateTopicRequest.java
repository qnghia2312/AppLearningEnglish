package com.example.learningenglish.model;

public class UpdateTopicRequest {
    private String newName;
    private String description;

    public UpdateTopicRequest(String newName, String description) {
        this.newName = newName;
        this.description = description;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}