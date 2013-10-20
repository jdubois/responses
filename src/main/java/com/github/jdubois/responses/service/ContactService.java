package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Contact;

import java.util.List;

/**
 * @author Julien Dubois
 */
public interface ContactService {

    void storeMessage(Contact contact);

    void sendMessage(Contact contact);

    List<Contact> getLatestAbuses();
}
