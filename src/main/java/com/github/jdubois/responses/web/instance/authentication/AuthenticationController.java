package com.github.jdubois.responses.web.instance.authentication;

import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.web.instance.BaseController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Julien Dubois
 */
@Controller
public class AuthenticationController extends BaseController {

    private final Log log = LogFactory.getLog(AuthenticationController.class);

    @Autowired
    InstanceService instanceService;

    @RequestMapping(value = "/{instancename}/authentication", method = RequestMethod.GET)
    public ModelAndView authentication(@PathVariable("instancename") String instanceName, HttpServletRequest request) {
        this.putInstanceInRequest(instanceName, request);
        ModelAndView mv = new ModelAndView();
        mv.addObject("instance", instanceService.getInstanceByName(instanceName));

        //Put the instance name in Session, for the authentication error page
        request.getSession().setAttribute("instanceName", instanceName);

        String error = (String) request.getParameter("error");
        if (error != null) {
            mv.addObject("error", "true");
        }
        String referer = request.getHeader("Referer");
        if (!referer.endsWith("authentication/registration") &&
                !referer.endsWith("authentication/error") &&
                !referer.endsWith("authentication/forgotten_password")) {

            mv.addObject("authenticationReferer", referer);
        }
        mv.setViewName("authentication/authentication");
        return mv;
    }

    @RequestMapping("/{instancename}/authentication/error")
    public ModelAndView authenticationError(@PathVariable("instancename") String instanceName, HttpServletRequest request) {
        this.putInstanceInRequest(instanceName, request);
        ModelAndView mv = new ModelAndView();
        this.putUserInModel(mv);
        this.putInstanceInModel(instanceName, mv);
        mv.setViewName("authentication/authentication_error");
        return mv;
    }
}
