package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.AntiSamyService;
import com.github.jdubois.responses.service.UserService;
import com.github.jdubois.responses.service.exception.HtmlValidationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * @author Julien Dubois
 */
@Service
public class AntiSamyServiceImpl implements AntiSamyService {

    private final Log log = LogFactory.getLog(AntiSamyServiceImpl.class);

    @Autowired
    private UserService userService;

    private AntiSamy antiSamy;

    @PostConstruct
    void init() {
        try {
            InputStream is = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("antisamy-1.3.xml"));
            Policy policy = null;
            policy = Policy.getInstance(is);
            is.close();
            antiSamy = new AntiSamy(policy);
        } catch (Exception e) {
            log.fatal("The antisamy configuration file could not be read.");
            e.printStackTrace();
            throw new RuntimeException("The antisamy configuration file could not be read.");
        }
    }

    public String cleanHtml(String taintedHTML) throws HtmlValidationException {
        CleanResults results;
        try {
            results = antiSamy.scan(taintedHTML);
            if (log.isDebugEnabled()) {
                log.debug("AntiSamy scan time : " + results.getScanTime() + " ms.");
            }
            if (results.getNumberOfErrors() > 0) {
                if (log.isWarnEnabled()) {
                    User user = userService.getCurrentUser();
                    log.warn("HTML validation errors, for user=" + user.getId());
                }
                if (log.isInfoEnabled()) {
                    log.info("HTML : " + taintedHTML);
                    for (Object o : results.getErrorMessages()) {
                        String msg = (String) o;
                        log.info("Erreur : " + msg);
                    }
                }
                throw new HtmlValidationException("The HTML has validation errors.");
            }
        } catch (Exception e) {
            log.error("Antisamy could not scan the HTML. " + e.getMessage());
            throw new HtmlValidationException(e);
        }

        return results.getCleanHTML();
    }
}
