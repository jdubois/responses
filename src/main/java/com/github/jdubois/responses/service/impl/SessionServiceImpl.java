package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.SessionService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author Julien Dubois
 */
@Service("SessionService")
@Scope("session")
public class SessionServiceImpl implements SessionService, Serializable {

    private static final long serialVersionUID = 1911916015081257288L;

    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
