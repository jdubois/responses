package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Company;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.dto.NameValue;

import java.util.List;

/**
 * @author Julien Dubois
 */
public interface UserService {

    User findUserByEmail(String email);

    User findUserById(int userId);

    User findUserAndRolesById(int userId);

    User findDetailedUserByEmail(String email);

    User getCurrentUser();

    List<NameValue> getExpertize(User user, int instanceId);

    List<User> findCompanyUsers(Company company);

    List<User> findUsersByInstanceId(int instanceId);

    List<User> findAllSuperUsers();

    List<User> findAllUsers(int index);
}
