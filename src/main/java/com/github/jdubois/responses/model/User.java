package com.github.jdubois.responses.model;

import com.github.jdubois.responses.service.util.Md5Util;
import com.github.jdubois.responses.service.util.SeoUtil;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Julien Dubois
 */
@Entity
@Table(name = "Users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
        @NamedQuery(name = "User.findUserByEmail",
                query = "select u from User u where u.email = :email"),
        @NamedQuery(name = "User.findUsersByCompany",
                query = "select u from User u where u.company.id = :companyId order by u.firstName, u.lastName"),
        @NamedQuery(name = "User.findAllUsersForInstance",
                query = "select u from User u join u.instances i where i.id = :instanceId order by u.firstName, u.lastName"),
        @NamedQuery(name = "User.findAllUsers",
                query = "select u from User u order by u.firstName, u.lastName"),
        @NamedQuery(name = "User.findAllSuperUsers",
                query = "select u from User u join u.roles r where r.role = 'ROLE_SU' order by u.firstName, u.lastName"),
        @NamedQuery(name = "User.findUsersByInstance",
                query = "select u from User u join u.instances i where i.id = :instanceId order by u.firstName, u.lastName"),
        @NamedQuery(name = "User.findNonValidatedUsers",
                query = "select u from User u where u.lastAccessDate = null and u.creationDate < :creationDate"),
        @NamedQuery(name = "ExpertService.getExperts",
                query = "select u from User u " +
                        "join u.roles r " +
                        "join u.instances i " +
                        "where r.role = 'ROLE_SUPPORT' " +
                        "and i.id = :instanceId " +
                        "order by u.lastName"),
        @NamedQuery(name = "ExpertService.getExpertsForInstances",
                query = "select distinct u from User u " +
                        "join u.roles r " +
                        "join u.instances i " +
                        "where r.role = 'ROLE_SUPPORT' " +
                        "and i.id in (:instanceIds) " +
                        "order by u.lastName")})
public class User implements Serializable {

    private static final long serialVersionUID = -3601965915546770465L;

    public static Map<Integer, String> profileAsUrlCache = new ConcurrentHashMap<Integer, String>(1000);
    public static Map<Integer, String> gravatarCache = new ConcurrentHashMap<Integer, String>(1000);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Size(min = 2, max = 255)
    @Email
    private String email;

    @NotNull
    @Size(min = 5, max = 255)
    private String password;

    @NotNull
    @Size(min = 2, max = 255)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 255)
    private String lastName;

    private String language;

    private boolean enabled;

    private String dateFormat;

    private Date creationDate;

    private Date lastAccessDate;

    private String website;

    private String blog;

    private String twitter;

    private String linkedIn;

    @ManyToMany(fetch = FetchType.LAZY)
    @BatchSize(size = 5)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Instance> instances;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @OneToMany(mappedBy = "user")
    @MapKey(name = "tagId")
    @BatchSize(size = 40)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Map<Integer, Expertize> expertizes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Favorite_Tag")
    @BatchSize(size = 10)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Tag> favoriteTags;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Ignored_Tag")
    @BatchSize(size = 10)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Tag> ignoredTags;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Role> roles;

    public String getProfileUrl() {
        String cache = profileAsUrlCache.get(id);
        if (cache != null) {
            return cache;
        } else {
            cache = SeoUtil.seo(firstName + "-" + lastName);
            profileAsUrlCache.put(id, cache);
            return cache;
        }
    }

    public String getGravatarUrl() {
        String cache = gravatarCache.get(id);
        if (cache != null) {
            return cache;
        } else {
            cache = Md5Util.md5Hex(email);
            gravatarCache.put(id, cache);
            return cache;
        }
    }

    public String getSmallGravatarUrl() {
        String gravatar = getGravatarUrl();
        return gravatar + "?s=32";
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        profileAsUrlCache.remove(id);
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        profileAsUrlCache.remove(id);
        this.lastName = lastName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        gravatarCache.remove(id);
        this.email = email;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Date lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public Set<Instance> getInstances() {
        return instances;
    }

    public void setInstances(Set<Instance> instances) {
        this.instances = instances;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Map<Integer, Expertize> getExpertizes() {
        return expertizes;
    }

    public void setExpertizes(Map<Integer, Expertize> expertizes) {
        this.expertizes = expertizes;
    }

    public Set<Tag> getFavoriteTags() {
        return favoriteTags;
    }

    public void setFavoriteTags(Set<Tag> favoriteTags) {
        this.favoriteTags = favoriteTags;
    }

    public Set<Tag> getIgnoredTags() {
        return ignoredTags;
    }

    public void setIgnoredTags(Set<Tag> ignoredTags) {
        this.ignoredTags = ignoredTags;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", language='" + language + '\'' +
                ", enabled=" + enabled +
                ", dateFormat='" + dateFormat + '\'' +
                ", creationDate=" + creationDate +
                ", lastAccessDate=" + lastAccessDate +
                ", company=" + company +
                '}';
    }
}
