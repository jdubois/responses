package com.github.jdubois.responses.web.instance.account;

import com.github.jdubois.responses.service.UserManagementService;
import com.github.jdubois.responses.web.instance.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Julien Dubois
 */
@Controller
public class AccountController extends BaseController {

    @Autowired
    private UserManagementService userManagementService;

    @RequestMapping(value = "/{instancename}/account", method = RequestMethod.GET)
    public ModelAndView homepage(@PathVariable("instancename") String instanceName) {
        ModelAndView mv = new ModelAndView();
        this.putUserInModel(mv);
        this.putInstanceInModel(instanceName, mv);
        mv.setViewName("account/account");
        return mv;
    }

    @RequestMapping(value = "/{instancename}/account", method = RequestMethod.POST)
    public ModelAndView update(@PathVariable("instancename") String instanceName,
                               @RequestParam(required = false) String firstName,
                               @RequestParam(required = false) String lastName,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String website,
                               @RequestParam(required = false) String blog,
                               @RequestParam(required = false) String twitter,
                               @RequestParam(required = false) String linkedIn,
                               @RequestParam(required = false) String password) {

        ModelAndView mv = new ModelAndView();
        boolean success = userManagementService.updateCurrentUser(email, firstName, lastName, website, blog, twitter, linkedIn, password);
        if (success) {
            mv.addObject("message", "Mise &agrave; jour r&eacute;ussie.");
        } else {
            mv.addObject("error", "La mise &agrave; jour a &eacute;chou&eacute;e, cette adresse e-mail est" +
                    " est d&eacute;j&agrave; utilis&eacute;e.");
        }
        this.putUserInModel(mv);
        this.putInstanceInModel(instanceName, mv);

        mv.setViewName("account/account");
        return mv;
    }
}
