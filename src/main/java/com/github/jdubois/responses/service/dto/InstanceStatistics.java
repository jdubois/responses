package com.github.jdubois.responses.service.dto;

import java.io.Serializable;

/**
 * @author Julien Dubois
 */
public class InstanceStatistics implements Serializable, Comparable<InstanceStatistics> {

    private String instanceName;

    private int questions;

    private int answers;

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public int getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public int compareTo(InstanceStatistics o) {
        return instanceName.compareTo(o.instanceName);
    }
}
