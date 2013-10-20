package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Tag;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.TagService;
import com.github.jdubois.responses.service.UserService;
import com.github.jdubois.responses.service.UserTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Repository("UserTagManager")
public class UserTagServiceImpl implements UserTagService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Transactional
    public Set<Tag> addFavoriteTag(int instanceId, String text) {
        User user = userService.getCurrentUser();
        Tag tag = tagService.addTag(instanceId, text);
        if (tag != null) {
            user.getFavoriteTags().add(tag);
            em.merge(user);
        }
        return this.getFavoriteTags(user, instanceId);
    }

    @Transactional
    public Set<Tag> removeFavoriteTag(int instanceId, String text) {
        User user = userService.getCurrentUser();
        Tag tag = tagService.getTagFromText(instanceId, text);
        if (tag != null) {
            user.getFavoriteTags().remove(tag);
            em.merge(user);
        }
        return this.getFavoriteTags(user, instanceId);
    }

    @Transactional(readOnly = true)
    public Set<Tag> getFavoriteTags(User user, int instanceId) {
        //TODO faire un cache
        Set<Tag> tags = user.getFavoriteTags();
        Set<Tag> favTags = new HashSet<Tag>();
        for (Tag tag : tags) {
            if (tag.getInstance().getId() == instanceId) {
                favTags.add(tag);
            }
        }
        return favTags;
    }

    @Transactional
    public Set<Tag> addIgnoredTag(int instanceId, String text) {
        User user = userService.getCurrentUser();
        Tag tag = tagService.addTag(instanceId, text);
        if (tag != null) {
            user.getIgnoredTags().add(tag);
            em.merge(user);
        }
        return this.getIgnoredTags(user, instanceId);
    }

    @Transactional
    public Set<Tag> removeIgnoredTag(int instanceId, String text) {
        User user = userService.getCurrentUser();
        Tag tag = tagService.getTagFromText(instanceId, text);
        if (tag != null) {
            user.getIgnoredTags().remove(tag);
            em.merge(user);
        }
        return this.getIgnoredTags(user, instanceId);
    }

    @Transactional(readOnly = true)
    public Set<Tag> getIgnoredTags(User user, int instanceId) {
        //TODO faire un cache
        Set<Tag> tags = user.getIgnoredTags();
        Set<Tag> ignTags = new HashSet<Tag>();
        for (Tag tag : tags) {
            if (tag.getInstance().getId() == instanceId) {
                ignTags.add(tag);
            }
        }
        return ignTags;
    }
}
