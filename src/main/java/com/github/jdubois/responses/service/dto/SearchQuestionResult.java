package com.github.jdubois.responses.service.dto;

import com.github.jdubois.responses.model.Question;

import java.io.Serializable;
import java.util.List;

/**
 * @author Julien Dubois
 */
public class SearchQuestionResult implements Serializable {

    List<Question> questions;

    int resultSize;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getResultSize() {
        return resultSize;
    }

    public void setResultSize(int resultSize) {
        this.resultSize = resultSize;
    }
}
