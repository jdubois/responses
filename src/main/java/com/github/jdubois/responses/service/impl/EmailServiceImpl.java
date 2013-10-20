package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.service.EmailService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author Julien Dubois
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final Log log = LogFactory.getLog(EmailServiceImpl.class);

    @Autowired
    private JavaMailSenderImpl mailSender;

    /**
     * Asynchronously send emails.
     */
    @Async
    public void asyncSendEmail(String to, String subject, String message) {
        try {
            this.sendEmail(to, subject, message);
        } catch (MailException me) {
            log.warn("Could not send e-mail : " + me.getMessage());
            if (log.isDebugEnabled()) {
                me.printStackTrace();
            }
        }
    }

    /**
     * Send emails synchronously.
     *
     * @param to      The email to which the email is sent
     * @param subject The subject of the email
     * @param message The message, in HTML format
     */
    private void sendEmail(String to, String subject, String message) {
        MimeMessage mimeMsg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true);
            helper.setFrom("Responses <mail@julien-dubois.com>");
            helper.setTo(to);
            helper.setSubject(subject);
            // use the true flag to indicate the text included is HTML
            helper.setText(message, true);

            mailSender.send(mimeMsg);

            if (log.isDebugEnabled()) {
                log.debug("Email sent to " + to);
            }
        } catch (MessagingException e) {
            log.warn("Could not send message to " + to);
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }
}
