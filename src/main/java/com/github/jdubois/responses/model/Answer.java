package com.github.jdubois.responses.model;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed
public class Answer implements Serializable, Comparable<Answer> {

    private static final long serialVersionUID = 1911915015081247288L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Analyzer(definition = "html-analyzer")
    @Field(index = Index.YES, store = Store.NO)
    @Size(max = 10000)
    @NotNull
    private String text;

    @ManyToOne
    @ContainedIn
    private Question question;

    @OneToMany(mappedBy = "answer")
    @MapKey(name = "userId")
    @BatchSize(size = 40)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Map<Integer, AnswerVote> answerVotes;

    @OneToMany(mappedBy = "answer")
    @Sort(type = SortType.NATURAL)
    @BatchSize(size = 20)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<AnswerComment> answerComments;

    @ManyToOne
    private User user;

    private Date creationDate;

    private int votesSize;

    @Transient
    private String period;

    @Transient
    private int currentUserVote;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getVotesSize() {
        return votesSize;
    }

    public void setVotesSize(int votesSize) {
        this.votesSize = votesSize;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Map<Integer, AnswerVote> getAnswerVotes() {
        return answerVotes;
    }

    public void setAnswerVotes(Map<Integer, AnswerVote> answerVotes) {
        this.answerVotes = answerVotes;
    }

    public Set<AnswerComment> getAnswerComments() {
        return answerComments;
    }

    public void setAnswerComments(Set<AnswerComment> answerComments) {
        this.answerComments = answerComments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getCurrentUserVote() {
        return currentUserVote;
    }

    public void setCurrentUserVote(int currentUserVote) {
        this.currentUserVote = currentUserVote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer)) return false;

        Answer answer = (Answer) o;

        return id == answer.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", question=" + question +
                ", answerVotes=" + answerVotes +
                ", user=" + user +
                ", creationDate=" + creationDate +
                ", votesSize=" + votesSize +
                ", period='" + period + '\'' +
                ", currentUserVote=" + currentUserVote +
                '}';
    }

    public int compareTo(Answer that) {
        long bestAnswerId = this.getQuestion().getBestAnswerId();
        if (bestAnswerId == that.getId()) {
            return 1000000;
        } else if (bestAnswerId == this.getId()) {
            return -1000000;
        }
        if (this.getVotesSize() != that.getVotesSize()) {
            return that.getVotesSize() - this.getVotesSize();
        } else if (!that.getCreationDate().equals(this.getCreationDate())) {
            return that.getCreationDate().compareTo(this.getCreationDate());
        } else {
            return (int) (that.getId() - this.getId());
        }
    }
}
