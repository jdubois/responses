package com.github.jdubois.responses.web.instance;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.UserService;
import com.github.jdubois.responses.service.dto.NameValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Julien Dubois
 */
@Controller
public class ProfileController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{instancename}/profile/{userId}/{userUrl}", method = RequestMethod.GET)
    public String view(@PathVariable("instancename") String instanceName,
                       @PathVariable("userId") int userId,
                       HttpServletRequest request) {

        Instance instance = putInstanceInRequest(instanceName, request);
        putUserInRequest(request);

        User userProfile = userService.findUserById(userId);
        request.setAttribute("userProfile", userProfile);
        List<NameValue> expertizeList = userService.getExpertize(userProfile, instance.getId());
        request.setAttribute("expertizeList", expertizeList);

        return "profile";
    }
}
