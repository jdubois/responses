package com.github.jdubois.responses.service;

/**
 * @author Julien Dubois
 */
public interface EmailService {

    void asyncSendEmail(String to, String subject, String message);

}
