package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Contact;
import com.github.jdubois.responses.service.ContactService;
import com.github.jdubois.responses.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.List;

/**
 * @author Julien Dubois
 */
@Repository
public class ContactServiceImpl implements ContactService {

    public static final String RESPONCIA_CONTACT_EMAIL = "contact@julien-dubois.com";

    @Autowired
    EmailService emailService;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void storeMessage(Contact contact) {
        contact.setCreationDate(Calendar.getInstance().getTime());
        em.persist(contact);
    }

    public void sendMessage(Contact contact) {
        String subject = "[Responses]";
        String text = "";
        if (contact.isAbuse()) {
            subject += "[Abuse]";
            text += "<p><b>Abuse</b></p>";
            if (contact.getUrl() != null) {
                text += "<p><b>Url=</b><a href=\"" + contact.getUrl() + "\">" + contact.getUrl() + "</a></p>";
            }
            if (contact.getAnswerId() != 0) {
                text += "<p><b>AnswerId=</b>" + contact.getAnswerId() + "</p>";
            }
            if (contact.getQuestionId() != 0) {
                text += "<p><b>QuestionId=</b>" + contact.getQuestionId() + "</p>";
            }
        }
        subject += contact.getSubject();
        text += "<p><b>From=</b>" + contact.getEmail() + "</p>";
        text += "<p>" + contact.getMessage() + "</p>";

        emailService.asyncSendEmail(RESPONCIA_CONTACT_EMAIL,
                subject, text);
    }

    @Transactional(readOnly = true)
    public List<Contact> getLatestAbuses() {
        Query q = em.createNamedQuery("Contact.findLatestAbuses");
        q.setFirstResult(0);
        q.setMaxResults(10);
        List<Contact> contacts = q.getResultList();
        return contacts;
    }
}
