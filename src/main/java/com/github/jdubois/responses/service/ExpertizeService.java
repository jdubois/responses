package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Tag;
import com.github.jdubois.responses.model.User;

import java.util.Set;

/**
 * @author Julien Dubois
 */
public interface ExpertizeService {

    void addExpertize(User answerUser, Set<Tag> questionTags, int value);

    void removeExpertize(User answerUser, Set<Tag> questionTags, int value);

}
