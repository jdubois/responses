package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Expertize;
import com.github.jdubois.responses.model.Tag;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.ExpertizeService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Repository
public class ExpertizeServiceImpl implements ExpertizeService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Add expertize for the user on the tags.
     */
    @Transactional
    public void addExpertize(User answerUser, Set<Tag> questionTags, int value) {
        Map<Integer, Expertize> expertizes = answerUser.getExpertizes();
        for (Tag tag : questionTags) {
            Expertize expertize = expertizes.get(tag.getId());
            if (expertize != null) {
                expertize.setPoints(expertize.getPoints() + value);
            } else {
                expertize = new Expertize();
                expertize.setTagId(tag.getId());
                expertize.setUser(answerUser);
                expertize.setPoints(value);
                em.persist(expertize);
                expertizes.put(tag.getId(), expertize);
            }
        }
    }

    /**
     * Remove the user expertize on those tags.
     */
    @Transactional
    public void removeExpertize(User answerUser, Set<Tag> questionTags, int value) {
        Map<Integer, Expertize> expertizes = answerUser.getExpertizes();
        for (Tag tag : questionTags) {
            Expertize expertize = expertizes.get(tag.getId());
            if (expertize != null) {
                expertize.setPoints(expertize.getPoints() - value);
                if (expertize.getPoints() == 0) {
                    em.remove(expertize);
                    expertizes.remove(tag.getId());
                }
            }
        }
    }
}
