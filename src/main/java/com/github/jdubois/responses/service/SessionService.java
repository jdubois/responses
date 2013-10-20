package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.User;

/**
 * @author Julien Dubois
 */
public interface SessionService {

    void setCurrentUser(User user);

    User getCurrentUser();
}
