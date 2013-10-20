package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Company;
import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Role;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.security.PasswordGenerator;
import com.github.jdubois.responses.service.EmailService;
import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.service.UserManagementService;
import com.github.jdubois.responses.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Repository
public class UserManagementServiceImpl implements UserManagementService {

    private final Log log = LogFactory.getLog(UserManagementServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserService userService;

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private EmailService emailService;

    /**
     * Create a new public user, when the user registers and sets the password himself.
     * <p>
     * This method returns null if the user already exists.
     * </p>
     */
    @Transactional
    public User createPublicUser(String email, String firstName, String lastName) {

        User user = preCreateUser(email, firstName, lastName);
        if (user == null) return null;
        Company company = em.find(Company.class, Company.NO_COMPANY);
        user.setCompany(company);
        postCreateUser(email, user);
        return user;
    }


    /**
     * Create a new business user with an automatically generated password.
     */
    @Transactional
    public User createBusinessUser(String email, String firstName, String lastName,
                                   boolean isModerator, boolean isSupport, boolean isAdmin, String instanceName) {

        User user = preCreateUser(email, firstName, lastName);
        if (user == null) return null;

        Instance instance = instanceService.getInstanceByName(instanceName);
        user.setCompany(instance.getCompany());
        setRoleAdmin(user, isAdmin);
        setRoleModerator(user, isModerator);
        setRoleSupport(user, isSupport);

        postCreateUser(email, user);
        return user;
    }

    /**
     * Common code used at the beginning of the user creation process.
     */
    private User preCreateUser(String email, String firstName, String lastName) {
        User user = userService.findUserByEmail(email);
        if (user != null) {
            if (log.isDebugEnabled()) {
                log.debug("User with e-mail \"" + email + "\" already exists.");
                return null;
            }
        }
        user = new User();
        user.setEmail(email.toLowerCase());
        String password = PasswordGenerator.generatePassword();
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCreationDate(Calendar.getInstance().getTime());
        user.setEnabled(true);
        setRoleUser(user, true);
        return user;
    }

    /**
     * Common code used at the end of the user creation process.
     */
    private void postCreateUser(String email, User user) {
        em.persist(user);
        em.flush();
        emailService.asyncSendEmail(email, "Création d'un compte sur Responses",
                "<p>Un compte vient de vous &ecirc;tre cr&eacute;&eacute; sur Responses.<br/>" +
                        "<ul><li>Votre e-mail : " + user.getEmail() + "</li>" +
                        "<li>Votre mot de passe : " + user.getPassword() + "</li></ul>" +
                        "Afin de valider la création de votre compte, vous devez vous authentifier sur " +
                        "<a href=\"http://www.julien-dubois.com\">http://www.julien-dubois.com</a> dans les 3 jours." +
                        "<br/><br/>" +
                        "Cordialement,<br/>L'équipe de Responses.<br/>" +
                        "<a href=\"http://www.julien-dubois.com\">http://www.julien-dubois.com</a></p>");
    }

    /**
     * Updates a specific user.
     */
    @Transactional
    public boolean updateUser(int userId, String email, String firstName, String lastName,
                              boolean isModerator, boolean isSupport, boolean isAdmin, boolean isEnabled) {

        User user = userService.findUserById(userId);
        User testEmail = userService.findUserByEmail(email);
        if (testEmail != null) {
            if (!user.equals(testEmail)) {
                //This email already belongs to a registered user
                return false;
            }
        } else {
            user.setEmail(email.toLowerCase());
        }
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(isEnabled);
        setRoleModerator(user, isModerator);
        setRoleSupport(user, isSupport);
        setRoleAdmin(user, isAdmin);
        return true;
    }

    /**
     * Updates the currently authenticated user.
     */
    @Transactional
    public boolean updateCurrentUser(String email, String firstName, String lastName, String website, String blog,
                                     String twitter, String linkedIn, String password) {

        User user = userService.getCurrentUser();
        if (firstName != null && lastName != null && email != null) {
            User testEmail = userService.findUserByEmail(email);
            if (testEmail != null) {
                if (!user.equals(testEmail)) {
                    //This email already belongs to a registered user
                    return false;
                }
            } else {
                user.setEmail(email.toLowerCase());
            }
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setWebsite(website);
            user.setBlog(blog);
            user.setTwitter(twitter);
            user.setLinkedIn(linkedIn);
        }
        if (password != null) {
            user.setPassword(password);
        }
        em.merge(user);
        return true;
    }

    @Transactional
    public void enableUser(int userId, boolean enable) {
        User user = userService.findUserById(userId);
        if (user != null) {
            user.setEnabled(enable);
        }
    }

    @Transactional
    public void setRoleUser(User user, boolean isUser) {
        Set<Role> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<Role>();
        }
        Role roleUser = em.find(Role.class, Role.ROLE_USER);
        if (isUser) {
            roles.add(roleUser);
        } else {
            roles.remove(roleUser);
        }
        user.setRoles(roles);
    }

    @Transactional
    public void setRoleAdmin(User user, boolean isAdmin) {
        Set<Role> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<Role>();
        }
        Role roleAdmin = em.find(Role.class, Role.ROLE_ADMIN);
        if (isAdmin) {
            roles.add(roleAdmin);
        } else {
            roles.remove(roleAdmin);
        }
        user.setRoles(roles);
    }

    @Transactional
    public void setRoleSupport(User user, boolean isSupport) {
        Set<Role> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<Role>();
        }
        Role roleSupport = em.find(Role.class, Role.ROLE_SUPPORT);
        if (isSupport) {
            roles.add(roleSupport);
        } else {
            roles.remove(roleSupport);
        }
        user.setRoles(roles);
    }

    @Transactional
    public void setRoleModerator(User user, boolean isModerator) {
        Set<Role> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<Role>();
        }
        Role roleModerator = em.find(Role.class, Role.ROLE_MODERATOR);
        if (isModerator) {
            roles.add(roleModerator);
        } else {
            roles.remove(roleModerator);
        }
        user.setRoles(roles);
    }

    @Transactional
    public void cleanUpNonValidatedUsers() {
        if (log.isInfoEnabled()) {
            log.info("Cleaning up non validated users.");
        }
        Query query = em.createNamedQuery("User.findNonValidatedUsers");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        query.setParameter("creationDate", cal.getTime());
        if (log.isInfoEnabled()) {
            SimpleDateFormat sdf = new SimpleDateFormat();
            log.info("Cleaning up users created before: " + sdf.format(cal.getTime()));
        }
        Collection<User> users = query.getResultList();
        for (User user : users) {
            log.info("Deleting non-validated user: " + user.getEmail());
            user.setInstances(new HashSet<Instance>());
            em.remove(user);
        }
    }
}
