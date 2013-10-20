package com.github.jdubois.responses.web.authentication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Julien Dubois
 */
@Controller
public class AuthenticationErrorController {

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ModelAndView authenticationError() {

        ModelAndView mv = new ModelAndView();
        mv.addObject("error", "Erreur d'authenficiation");
        mv.setViewName("authentication/authentication_error");
        return mv;
    }
}