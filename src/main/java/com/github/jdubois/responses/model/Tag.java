package com.github.jdubois.responses.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NamedQueries({
        @NamedQuery(name = "Tag.getAllTagsForInstance",
                query = "select t from Tag t " +
                        "where t.instance.id = :instanceId "),
        @NamedQuery(name = "Tag.getPopularTags",
                query = "select t from Tag t " +
                        "where t.instance.id = :instanceId " +
                        "order by t.size desc")})
@Indexed
public class Tag implements Serializable, Comparable<Tag> {

    private static final long serialVersionUID = 7372678407925055673L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Field(index = Index.NO, store = Store.YES)
    @Boost(3f)
    private String text;

    @Field(index = Index.NO, store = Store.YES)
    private int size;

    @ManyToOne(fetch = FetchType.EAGER)
    @IndexedEmbedded
    private Instance instance;

    @ManyToMany(mappedBy = "tags")
    private Set<Question> questions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        Tag tag = (Tag) o;

        if (id != tag.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int compareTo(Tag that) {
        return text.compareTo(that.getText());
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", size=" + size +
                '}';
    }
}
