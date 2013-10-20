package com.github.jdubois.responses.security;

import com.github.jdubois.responses.model.Role;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.SessionService;
import com.github.jdubois.responses.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Service("userDetailsService")
@Transactional
public class ResponsesUserDetailsService implements UserDetailsService, ApplicationContextAware {

    private final Log log = LogFactory.getLog(ResponsesUserDetailsService.class);

    private ApplicationContext applicationContext;

    @Autowired
    private UserService userService;

    /**
     * Load a user for Spring Security.
     */
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException, DataAccessException {

        email = email.toLowerCase();
        if (log.isDebugEnabled()) {
            log.debug("Security verification for user '" + email + "'");
        }

        User user = userService.findDetailedUserByEmail(email);

        if (user == null) {
            if (log.isDebugEnabled()) {
                log.debug("User '" + email
                        + "' could not be found.");
            }
            throw new UsernameNotFoundException("User could not be found.");
        }

        user.setLastAccessDate(Calendar.getInstance().getTime());

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }

        SessionService sessionService = this.applicationContext.getBean(SessionService.class);
        sessionService.setCurrentUser(user);

        return new org.springframework.security.core.userdetails.User(email,
                user.getPassword(), user.isEnabled(), true, true, true,
                authorities);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
