package com.github.jdubois.responses.service.impl;


import com.github.jdubois.responses.model.Company;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.SessionService;
import com.github.jdubois.responses.service.UserService;
import com.github.jdubois.responses.service.dto.NameValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Julien Dubois
 */
@Repository("UserManager")
public class UserServiceImpl implements UserService, ApplicationContextAware {

    private final Log log = LogFactory.getLog(QuestionServiceImpl.class);

    private final static String SQL_USER_EXPERTIZE = "SELECT t.text text, e.points points FROM Expertize e join Tag t " +
            "on e.tag_id = t.id where t.instance_id=? and e.user_id=? order by e.points desc limit 0,200";

    private ApplicationContext applicationContext;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Find a user by his email address.
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public User findUserByEmail(String email) {
        Query query = em.createNamedQuery("User.findUserByEmail");
        query.setParameter("email", email);
        List<User> results = query.getResultList();
        if (results.size() != 1) {
            if (log.isDebugEnabled()) {
                log.debug("User with email \"" + email + "\" not found.");
            }
            return null;
        } else {
            return results.get(0);
        }
    }

    /**
     * Find a user by his id.
     */
    @Transactional(readOnly = true)
    public User findUserById(int userId) {
        return em.find(User.class, userId);
    }

    /**
     * Find a user and his roles.
     */
    @Transactional(readOnly = true)
    public User findUserAndRolesById(int userId) {
        User user = findUserById(userId);
        if (user == null) {
            if (log.isDebugEnabled()) {
                log.debug("User with id \"" + userId + "\" not found.");
            }
        } else {
            Hibernate.initialize(user.getRoles());
        }
        return user;
    }

    /**
     * Find a user by his email address.
     * This method eagerly fetches all the objects associated with the user.
     */
    @Transactional(readOnly = true)
    public User findDetailedUserByEmail(String email) {
        Session session = (Session) em.getDelegate();
        User user = (User) session.createCriteria(User.class)
                .setFetchMode("roles", FetchMode.JOIN)
                .setFetchMode("favoriteTags", FetchMode.JOIN)
                .setFetchMode("ignoredTags", FetchMode.JOIN)
                .setFetchMode("instances", FetchMode.JOIN)
                .setFetchMode("company", FetchMode.JOIN)
                .add(Restrictions.eq("email", email))
                .uniqueResult();

        if (user == null) {
            if (log.isDebugEnabled()) {
                log.debug("User with e-mail \"" + email + "\" not found.");
            }
        }
        return user;
    }

    /**
     * Find the currently authenticated user.
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Object principal = securityContext.getAuthentication().getPrincipal();
        if (principal instanceof String) {
            if (log.isDebugEnabled())
                log.debug("User is anonymous.");
            return null;
        } else {
            SessionService sessionService = this.applicationContext.getBean(SessionService.class);
            if (sessionService.getCurrentUser() == null) {
                log.debug("User is not cached in session.");
                org.springframework.security.core.userdetails.User springSecurityUser =
                        (org.springframework.security.core.userdetails.User) principal;

                User user = this.findDetailedUserByEmail(springSecurityUser.getUsername());
                if (user == null) {
                    log.warn("User \"" + springSecurityUser.getUsername() + "\" should have been found.");
                    return null;
                }
                sessionService.setCurrentUser(user);
            }
            return sessionService.getCurrentUser();
        }
    }

    @Transactional(readOnly = true)
    public List<NameValue> getExpertize(User user, int instanceId) {

        if (log.isDebugEnabled()) {
            log.debug("SQL_USER_EXPERTIZE |" + instanceId + "|" + user.getId());
        }

        List<NameValue> expertizeList = jdbcTemplate.query(SQL_USER_EXPERTIZE,
                new Object[]{instanceId, user.getId()}, expertizeSummaryRowMapper);

        return expertizeList;
    }

    @Transactional(readOnly = true)
    public List<User> findCompanyUsers(Company company) {
        Query query = em.createNamedQuery("User.findUsersByCompany");
        query.setParameter("companyId", company.getId());
        List<User> users = query.getResultList();
        return users;
    }

    @Transactional(readOnly = true)
    public List<User> findUsersByInstanceId(int instanceId) {
        Query query = em.createNamedQuery("User.findUsersByInstance");
        query.setParameter("instanceId", instanceId);
        List<User> users = query.getResultList();
        return users;
    }

    @Transactional(readOnly = true)
    public List<User> findAllSuperUsers() {
        return em.createNamedQuery("User.findAllSuperUsers").getResultList();
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers(int index) {
        return em.createNamedQuery("User.findAllUsers")
                .setFirstResult(index)
                .setMaxResults(100)
                .getResultList();
    }

    private static ParameterizedRowMapper<NameValue> expertizeSummaryRowMapper = new ParameterizedRowMapper<NameValue>() {
        public NameValue mapRow(ResultSet rs, int rowNum) throws SQLException {
            NameValue nv = new NameValue();
            nv.setName(rs.getString("text"));
            nv.setValue(rs.getString("points"));
            return nv;
        }
    };

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
