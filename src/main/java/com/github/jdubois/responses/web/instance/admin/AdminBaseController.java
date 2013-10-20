package com.github.jdubois.responses.web.instance.admin;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Role;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.security.SecurityUtil;
import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.service.UserService;
import com.github.jdubois.responses.service.exception.ResponsesSecurityException;
import com.github.jdubois.responses.web.instance.BaseController;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Julien Dubois
 */
public class AdminBaseController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    InstanceService instanceService;

    void securityCheck(String instanceName) {
        User currentUser = userService.getCurrentUser();
        Instance instance = instanceService.getInstanceByName(instanceName);
        if (!currentUser.getInstances().contains(instance)) {
            if (!SecurityUtil.isUserInRole(Role.ROLE_SU)) {
                throw new ResponsesSecurityException("User \"" + currentUser.getEmail() + "\" has tried to access the admin console of " +
                        "instance \"" + instance.getName() + "\"");
            }
        }
    }
}
