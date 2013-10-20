package com.github.jdubois.responses.web.about;

import com.github.jdubois.responses.model.Contact;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.ConfigurationService;
import com.github.jdubois.responses.service.ContactService;
import com.github.jdubois.responses.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

/**
 * @author Julien Dubois
 */
@Controller
public class AboutController {

    private final Log log = LogFactory.getLog(AboutController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String contact(@RequestParam(required = false) String abuse,
                          @RequestParam(required = false) Integer id,
                          @RequestParam(required = false) String abuseUrl,
                          HttpServletRequest request) {

        User user = userService.getCurrentUser();
        if (user != null) {
            request.setAttribute("email", user.getEmail());
        }
        if (abuse != null) {
            request.setAttribute("abuse", 1);
            request.setAttribute("abuseUrl", abuseUrl);
            if ("answer".equals(abuse)) {
                request.setAttribute("answerId", id);
            } else if ("question".equals(abuse)) {
                request.setAttribute("questionId", id);
            }
        }
        return "about/contact";
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public String contact(
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String messageText,
            @RequestParam(required = false) boolean abuse,
            @RequestParam(required = false) String abuseUrl,
            @RequestParam(required = false) Integer questionId,
            @RequestParam(required = false) Integer answerId,
            HttpServletRequest request) {

        Contact contact = new Contact();
        contact.setEmail(email);
        contact.setSubject(subject);
        contact.setMessage(messageText);
        contact.setAbuse(abuse);
        contact.setUrl(abuseUrl);
        if (questionId != null) {
            contact.setQuestionId(questionId);
        }
        if (answerId != null) {
            contact.setAnswerId(answerId);
        }

        try {
            contactService.storeMessage(contact);
        } catch (ValidationException ex) {
            request.setAttribute("error", "Votre message n'a pas pu &ecirc;tre valid&eacute;. La taille du message ne doit pas d&eacute;passer 5000 caract&egrave;res.");
            return "about/contact";
        }
        contactService.sendMessage(contact);

        return "about/contact_ok";
    }

    @RequestMapping(value = "/conditions_generales_d_utilisation", method = RequestMethod.GET)
    public String conditions() {
        return "about/conditions_generales_d_utilisation";
    }

    @RequestMapping(value = "/mentions_legales", method = RequestMethod.GET)
    public String mentionsLegales() {
        return "about/mentions_legales";
    }

    @RequestMapping(value = "/ok", method = RequestMethod.GET)
    public String ok() {
        try {
            int i = configurationService.testDatabase();
            if (i == 1) {
                return "about/ok";
            } else {
                return "about/nok";
            }
        } catch (Throwable t) {
            log.warn("OK Service is down! " + t.getMessage());
            return "about/nok";
        }
    }
}