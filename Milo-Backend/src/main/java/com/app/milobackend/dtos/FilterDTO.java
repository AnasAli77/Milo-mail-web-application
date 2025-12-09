package com.app.milobackend.dtos;

import java.util.List;

public class FilterDTO {
    private String word;
    private List<String> criteria;

    public List<String> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<String> criteria) {
        this.criteria = criteria;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
