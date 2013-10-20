package com.github.jdubois.responses.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Julien Dubois
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Workflow implements Comparable<Workflow> {

    public static final int STATE_NONE = 0;
    public static final int STATE_START = 1;
    public static final int STATE_VALIDATED = 2;
    public static final int STATE_NOT_VALIDATED = 3;
    public static final int STATE_ASSIGNED = 4;
    public static final int STATE_RESOLVED = 5;
    public static final int STATE_REJECTED = 6;
    public static final int STATE_END = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Question question;

    private int state;

    private Date stateDate;

    @ManyToOne
    private User user;

    @ManyToOne
    private User assignedUser;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getStateDate() {
        return stateDate;
    }

    public void setStateDate(Date stateDate) {
        this.stateDate = stateDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Workflow that = (Workflow) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public int compareTo(Workflow that) {
        return that.getStateDate().compareTo(this.getStateDate());
    }
}
