package com.github.jdubois.responses.service.exception;

/**
 * Quality Of Service exception: thrown when a user tries to spam the website.
 *
 * @author Julien Dubois
 */
public class QOSException extends Exception {

    public QOSException(String msg) {
        super(msg);
    }

    public QOSException(Exception e) {
        super(e);
    }
}