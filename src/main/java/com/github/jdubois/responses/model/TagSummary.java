package com.github.jdubois.responses.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Julien Dubois
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "TagSummary.2tags",
                query = "select ts from TagSummary ts where ts.tag1 = :tag1 and ts.tag2 = :tag2 and ts.tag3 = 0 and ts.tag4 = 0 and ts.tag5 = 0"),
        @NamedQuery(name = "TagSummary.3tags",
                query = "select ts from TagSummary ts where ts.tag1 = :tag1 and ts.tag2 = :tag2 and ts.tag3 = :tag3 and ts.tag4 = 0 and ts.tag5 = 0"),
        @NamedQuery(name = "TagSummary.4tags",
                query = "select ts from TagSummary ts where ts.tag1 = :tag1 and ts.tag2 = :tag2 and ts.tag3 = :tag3 and ts.tag4 = :tag4 and ts.tag5 = 0"),
        @NamedQuery(name = "TagSummary.5tags",
                query = "select ts from TagSummary ts where ts.tag1 = :tag1 and ts.tag2 = :tag2 and ts.tag3 = :tag3 and ts.tag4 = :tag4 and ts.tag5 = :tag5")
})
public class TagSummary implements Serializable {

    private static final long serialVersionUID = -6689831562866164027L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int tag1;

    private int tag2;

    private int tag3;

    private int tag4;

    private int tag5;

    private int size;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTag1() {
        return tag1;
    }

    public void setTag1(int tag1) {
        this.tag1 = tag1;
    }

    public int getTag2() {
        return tag2;
    }

    public void setTag2(int tag2) {
        this.tag2 = tag2;
    }

    public int getTag3() {
        return tag3;
    }

    public void setTag3(int tag3) {
        this.tag3 = tag3;
    }

    public int getTag4() {
        return tag4;
    }

    public void setTag4(int tag4) {
        this.tag4 = tag4;
    }

    public int getTag5() {
        return tag5;
    }

    public void setTag5(int tag5) {
        this.tag5 = tag5;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagSummary that = (TagSummary) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
