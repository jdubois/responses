package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.User;

/**
 * @author Julien Dubois
 */
public interface UserManagementService {

    User createPublicUser(String email, String firstName, String lastName);

    User createBusinessUser(String email, String firstName, String lastName, boolean isModerator, boolean isSupport, boolean isAdmin, String instanceName);

    boolean updateUser(int userId, String email, String firstName, String lastName, boolean isModerator, boolean isSupport, boolean isAdmin, boolean isEnabled);

    boolean updateCurrentUser(String email, String firstName, String lastName, String website, String blog,
                              String twitter, String linkedIn, String password);

    void enableUser(int userId, boolean enable);

    void setRoleUser(User user, boolean isUser);

    void setRoleAdmin(User user, boolean isAdmin);

    void setRoleSupport(User user, boolean isSupport);

    void setRoleModerator(User user, boolean isModerator);

    void cleanUpNonValidatedUsers();
}
