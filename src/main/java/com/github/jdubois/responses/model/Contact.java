package com.github.jdubois.responses.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Stocke les messages de contact envoyés par les utilisateurs, y compris les messages remontant des abus.
 *
 * @author Julien Dubois
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Contact.findLatestAbuses",
                query = "select c from Contact c where c.abuse = true order by c.creationDate desc")
})
public class Contact implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(max = 255)
    private String email;

    private Date creationDate;

    private boolean abuse;

    @Size(max = 255)
    private String url;

    private int questionId;

    private int answerId;

    @Size(max = 255)
    private String subject;

    @Size(max = 5000)
    private String message;

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isAbuse() {
        return abuse;
    }

    public void setAbuse(boolean abuse) {
        this.abuse = abuse;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (id != contact.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}