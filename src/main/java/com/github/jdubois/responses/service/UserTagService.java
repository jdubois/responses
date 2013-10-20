package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Tag;
import com.github.jdubois.responses.model.User;

import java.util.Set;

/**
 * @author Julien Dubois
 */
public interface UserTagService {

    Set<Tag> addFavoriteTag(int instanceId, String text);

    Set<Tag> removeFavoriteTag(int instanceId, String text);

    Set<Tag> getFavoriteTags(User user, int instanceId);

    Set<Tag> addIgnoredTag(int instanceId, String text);

    Set<Tag> removeIgnoredTag(int instanceId, String text);

    Set<Tag> getIgnoredTags(User user, int instanceId);
}
