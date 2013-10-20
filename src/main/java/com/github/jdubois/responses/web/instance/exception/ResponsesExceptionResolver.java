package com.github.jdubois.responses.web.instance.exception;

import com.github.jdubois.responses.service.UserService;
import com.github.jdubois.responses.service.exception.InstanceException;
import com.github.jdubois.responses.service.exception.ResponsesSecurityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

/**
 * @author Julien Dubois
 */
public class ResponsesExceptionResolver extends SimpleMappingExceptionResolver {

    private final Log log = LogFactory.getLog(ResponsesExceptionResolver.class);

    @Autowired
    UserService userService;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof ValidationException) {
            log.warn("Hibernate Validation exception! " + ex.getMessage());
        } else if (ex instanceof QuestionNotFoundException) {
            log.info("Question not found. " + ex.getMessage());
        } else if (ex instanceof ResponsesSecurityException) {
            log.warn("[Security] " + ex.getMessage());
        } else if (ex instanceof InstanceException) {
            log.info(ex.getMessage());
        } else {
            log.warn("An Exception has occured : ", ex);
            if (log.isDebugEnabled()) {
                ex.printStackTrace();
            }
        }
        return super.doResolveException(request, response, handler, ex);
    }

}
