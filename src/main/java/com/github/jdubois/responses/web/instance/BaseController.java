package com.github.jdubois.responses.web.instance;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author Julien Dubois
 */
public class BaseController {

    private final Log log = LogFactory.getLog(BaseController.class);

    @Autowired
    UserService userService;

    @Autowired
    InstanceService instanceService;

    protected User putUserInModel(ModelAndView mv) {
        User user = this.userService.getCurrentUser();
        if (user != null) {
            mv.addObject("userName", user.getFirstName() + " " + user.getLastName());
            mv.addObject("user", user);
        }
        return user;
    }

    protected User putUserInRequest(HttpServletRequest request) {
        User user = this.userService.getCurrentUser();
        if (user != null) {
            request.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
            request.setAttribute("user", user);
        }
        return user;
    }

    protected Instance putInstanceInRequest(String instanceName, HttpServletRequest request) {
        Instance instance = instanceService.getInstanceByName(instanceName);
        request.setAttribute("instance", instance);
        return instance;
    }

    protected void checkInstance(String instanceName) {
        //This will trigger a security check on the instance, if it is a private instance.
        instanceService.getInstanceByName(instanceName);
    }

    protected Instance putInstanceInModel(String instanceName, ModelAndView mv) {
        Instance instance = instanceService.getInstanceByName(instanceName);
        mv.addObject("instance", instance);
        return instance;
    }

    protected void putSearchQueryInRequest(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("search?q=")) {
            String searchQuery = referer.substring(referer.indexOf("search?q=") + 9);
            if (searchQuery.contains("&")) {
                searchQuery = searchQuery.substring(0, searchQuery.indexOf("&"));
            }
            try {
                searchQuery = URLDecoder.decode(searchQuery, "utf-8");
            } catch (UnsupportedEncodingException e) {
                searchQuery = "";
            }
            request.setAttribute("searchQuery", searchQuery);
        }
    }

    protected void paginationCalculation(HttpServletRequest request, Integer questionSize) {
        Integer pagesNumber = (questionSize - 1) / 20;
        if (pagesNumber >= 30) {
            pagesNumber = 30;
        }
        request.setAttribute("questionSize", questionSize);
        request.setAttribute("pagesNumber", pagesNumber);
    }
}
