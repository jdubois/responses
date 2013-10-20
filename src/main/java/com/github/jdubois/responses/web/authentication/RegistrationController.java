package com.github.jdubois.responses.web.authentication;

import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.UserManagementService;
import net.tanesha.recaptcha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.recaptcha.ReCaptchaResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * @author Julien Dubois
 */
@Controller
@RequestMapping(value = "/registration")
public class RegistrationController {

    private final Log log = LogFactory.getLog(RegistrationController.class);

    ReCaptcha reCaptcha = null;

    Properties captchaProps = new Properties();

    @Autowired
    private UserManagementService userManagementService;

    @PostConstruct
    void init() {
        reCaptcha = ReCaptchaFactory.newReCaptcha("6Lc_FAoAAAAAAORM0cRFcHDA3rqCsd7xwUP5HRDj", "6Lc_FAoAAAAAAA79QrU4bEXRz_vd6qQurgfhgHx2", false);
        captchaProps.setProperty("lang", "fr");
        captchaProps.setProperty("tabindex", "7");
        captchaProps.setProperty("theme", "white");
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView homepage(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("captcha", reCaptcha.createRecaptchaHtml(null, captchaProps));
        mv.setViewName("authentication/registration");
        return mv;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView register(HttpServletRequest request,
                                 @RequestParam String email,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam(required = false) boolean rulesOk) {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("authentication/registration");
        String challenge = request.getParameter("recaptcha_challenge_field");
        String response = request.getParameter("recaptcha_response_field");
        if (log.isDebugEnabled()) {
            log.debug("ReCaptcha challenge : " + challenge);
            log.debug("ReCaptcha response : " + response);
        }
        ReCaptchaResponse rResponse = reCaptcha.checkAnswer(request.getRemoteAddr(), challenge, response);
        if (!rResponse.isValid()) {
            mv.addObject("errorRecaptcha", rResponse.getErrorMessage());
            regenerateForm(email, firstName, lastName, mv);
        } else if (rulesOk == false) {
            mv.addObject("rulesNotOk", "true");
            regenerateForm(email, firstName, lastName, mv);
        } else {
            User user = null;
            try {
                user = userManagementService.createPublicUser(email, firstName, lastName);
            } catch (DataIntegrityViolationException dive) {
                log.error("Could not create user \"" + email + "\" : " + dive.getMessage());
                user = null;
            }
            if (user == null) {
                mv.addObject("errorUserAlreadyExists", "true");
                regenerateForm(email, firstName, lastName, mv);
            } else {
                mv.setViewName("authentication/registration_ok");
            }
        }
        return mv;
    }

    private void regenerateForm(String email, String firstName, String lastName, ModelAndView mv) {
        mv.addObject("captcha", reCaptcha.createRecaptchaHtml(null, captchaProps));
        mv.addObject("email", email);
        mv.addObject("firstName", firstName);
        mv.addObject("lastName", lastName);
    }
}