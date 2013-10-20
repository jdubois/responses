package com.github.jdubois.responses.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Julien Dubois
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NamedQueries({
        @NamedQuery(name = "Instance.findByName",
                query = "select i from Instance i where i.name = :name"),
        @NamedQuery(name = "Instance.findAllInstances",
                query = "select i from Instance i order by i.longName")
})
public class Instance implements Serializable, Comparable<Instance> {

    private static final long serialVersionUID = 1800178460367596562L;

    public static final int TYPE_PRIVATE = 0;
    public static final int TYPE_PUBLIC = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Field(index = Index.NO)
    private int id;

    private String name;

    private String longName;

    private int type;

    private int enabled;

    @Basic(fetch = FetchType.LAZY)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instance)) return false;

        Instance instance = (Instance) o;

        if (!name.equals(instance.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public int compareTo(Instance that) {
        if (!name.equals(that.name)) {
            return name.compareTo(that.name);
        }
        return id - that.id;
    }
}
