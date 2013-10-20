package com.github.jdubois.responses.web.authentication;

import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.EmailService;
import com.github.jdubois.responses.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Julien Dubois
 */
@Controller
public class ForgottenPasswordController {

    private final Log log = LogFactory.getLog(ForgottenPasswordController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/forgotten_password", method = RequestMethod.GET)
    public ModelAndView homepage(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("authentication/forgotten_password");
        return mv;
    }

    @RequestMapping(value = "/forgotten_password", method = RequestMethod.POST)
    public ModelAndView sendPassword(@RequestParam String email,
                                     HttpServletRequest request) {

        ModelAndView mv = new ModelAndView();
        User user = userService.findUserByEmail(email);
        if (user == null) {
            mv.addObject("error", "Erreur : aucun utilisateur ne poss&egrave;de cette adresse e-mail.");
            mv.setViewName("authentication/forgotten_password");
        } else {

            try {
                this.emailService.asyncSendEmail(email, "Votre mot de passe Responses",
                        "<p>Vous venez de demander votre mot de passe à Responses.<br/>" +
                                "<ul><li>Votre e-mail : " + user.getEmail() + "</li>" +
                                "<li>Votre mot de passe : " + user.getPassword() + "</li></ul>" +
                                "Pour des raisons de sécurité, nous vous recommandons de " +
                                "changer votre mot de passe le plus rapidement possible en vous connectant sur " +
                                "<a href=\"http://www.julien-dubois.com\">http://www.julien-dubois.com</a>.<br/><br/>" +
                                "Cordialement,<br/>L'équipe de Responses.<br/>" +
                                "<a href=\"http://www.julien-dubois.com\">http://www.julien-dubois.com</a></p>");

                mv.setViewName("authentication/forgotten_password_ok");
            } catch (MailException me) {
                log.warn("Impossible d'envoyer un e-mail : " + me.getMessage());
                if (log.isDebugEnabled()) {
                    me.printStackTrace();
                }
                mv.addObject("error", "Erreur : une erreur technique s'est produite lors de l'envoi de votre e-mail. " +
                        "Les &eacute;quipes de Responses vont corriger ce probl&egrave;me le plus rapidement possible, merci " +
                        "de r&eacute;utiliser cette page ult&eacute;rieurement.");

                mv.setViewName("authentication/forgotten_password");
            }
        }
        return mv;
    }
}