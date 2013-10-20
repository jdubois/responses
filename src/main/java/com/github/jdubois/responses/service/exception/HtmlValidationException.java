package com.github.jdubois.responses.service.exception;

/**
 * @author Julien Dubois
 */
public class HtmlValidationException extends Exception {

    private static final long serialVersionUID = 6239843063837466143L;

    public HtmlValidationException(String msg) {
        super(msg);
    }

    public HtmlValidationException(Exception e) {
        super(e);
    }
}
