package com.example.learningenglish.model;

import java.io.Serializable;

public class Vocabulary implements Serializable {
    private String word;
    private String mean;
    private String pronunciation;
    private String example;
    private String topic;
    private String type;

    public Vocabulary(String word, String mean, String pronunciation, String example, String topic) {
        this.word = word;
        this.mean = mean;
        this.pronunciation = pronunciation;
        this.example = example;
        this.topic = topic;
    }


    public Vocabulary(String word, String mean, String pronunciation, String example, String topic, String type) {
        this.word = word;
        this.mean = mean;
        this.pronunciation = pronunciation;
        this.example = example;
        this.topic = topic;
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public String getMean() {
        return mean;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getExample() {
        return example;
    }

    public String getTopic() {
        return topic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
