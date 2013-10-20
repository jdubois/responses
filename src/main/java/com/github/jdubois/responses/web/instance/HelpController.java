package com.github.jdubois.responses.web.instance;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Julien Dubois
 */
@Controller
public class HelpController extends BaseController {

    @RequestMapping(value = "/{instancename}/help", method = RequestMethod.GET)
    public String help(@PathVariable("instancename") String instanceName, HttpServletRequest request) {
        putInstanceInRequest(instanceName, request);
        putUserInRequest(request);
        return "help";
    }
}
