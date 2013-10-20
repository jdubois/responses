package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Tag;
import com.github.jdubois.responses.model.TagSummary;
import com.github.jdubois.responses.service.TagService;
import com.github.jdubois.responses.service.TagSummaryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Repository
public class TagSummaryServiceImpl implements TagSummaryService {

    private final Log log = LogFactory.getLog(TagSummaryServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TagService tagService;

    public void tagSummaryCalculator(int instanceId, String[] tagsArray, boolean increment) {
        if (tagsArray.length > 0) {
            Arrays.sort(tagsArray);
            if (tagsArray.length == 2) {
                int tag1 = tagService.getTagIdFromText(instanceId, tagsArray[0]);
                int tag2 = tagService.getTagIdFromText(instanceId, tagsArray[1]);

                tagSummary(tag1, tag2, increment);
            } else if (tagsArray.length == 3) {
                int tag1 = tagService.getTagIdFromText(instanceId, tagsArray[0]);
                int tag2 = tagService.getTagIdFromText(instanceId, tagsArray[1]);
                int tag3 = tagService.getTagIdFromText(instanceId, tagsArray[2]);

                tagSummary(tag1, tag2, increment);
                tagSummary(tag1, tag3, increment);
                tagSummary(tag2, tag3, increment);
                tagSummary(tag1, tag2, tag3, increment);
            } else if (tagsArray.length == 4) {
                int tag1 = tagService.getTagIdFromText(instanceId, tagsArray[0]);
                int tag2 = tagService.getTagIdFromText(instanceId, tagsArray[1]);
                int tag3 = tagService.getTagIdFromText(instanceId, tagsArray[2]);
                int tag4 = tagService.getTagIdFromText(instanceId, tagsArray[3]);

                tagSummary(tag1, tag2, increment);
                tagSummary(tag1, tag3, increment);
                tagSummary(tag1, tag4, increment);
                tagSummary(tag2, tag3, increment);
                tagSummary(tag2, tag4, increment);
                tagSummary(tag3, tag4, increment);

                tagSummary(tag1, tag2, tag3, increment);
                tagSummary(tag1, tag2, tag4, increment);
                tagSummary(tag1, tag3, tag4, increment);
                tagSummary(tag2, tag3, tag4, increment);

                tagSummary(tag1, tag2, tag3, tag4, increment);
            } else if (tagsArray.length == 5) {
                int tag1 = tagService.getTagIdFromText(instanceId, tagsArray[0]);
                int tag2 = tagService.getTagIdFromText(instanceId, tagsArray[1]);
                int tag3 = tagService.getTagIdFromText(instanceId, tagsArray[2]);
                int tag4 = tagService.getTagIdFromText(instanceId, tagsArray[3]);
                int tag5 = tagService.getTagIdFromText(instanceId, tagsArray[4]);

                tagSummary(tag1, tag2, increment);
                tagSummary(tag1, tag3, increment);
                tagSummary(tag1, tag4, increment);
                tagSummary(tag1, tag5, increment);
                tagSummary(tag2, tag3, increment);
                tagSummary(tag2, tag4, increment);
                tagSummary(tag2, tag5, increment);
                tagSummary(tag3, tag4, increment);
                tagSummary(tag3, tag5, increment);
                tagSummary(tag4, tag5, increment);

                tagSummary(tag1, tag2, tag3, increment);
                tagSummary(tag1, tag2, tag4, increment);
                tagSummary(tag1, tag2, tag5, increment);
                tagSummary(tag1, tag3, tag4, increment);
                tagSummary(tag1, tag3, tag5, increment);
                tagSummary(tag1, tag4, tag5, increment);
                tagSummary(tag2, tag3, tag4, increment);
                tagSummary(tag2, tag3, tag5, increment);
                tagSummary(tag2, tag4, tag5, increment);
                tagSummary(tag3, tag4, tag5, increment);

                tagSummary(tag1, tag2, tag3, tag4, increment);
                tagSummary(tag1, tag2, tag3, tag5, increment);
                tagSummary(tag1, tag2, tag4, tag5, increment);
                tagSummary(tag1, tag3, tag4, tag5, increment);
                tagSummary(tag2, tag3, tag4, tag5, increment);

                tagSummary(tag1, tag2, tag3, tag4, tag5, increment);
            }
        }
    }


    /**
     * Clean up the tags for a question.
     */
    public String[] cleanQuestionTags(Set<Tag> tags, int instanceId) {
        String tagsArray[] = new String[tags.size()];
        int index = 0;
        for (Tag tag : tags) {
            tagsArray[index++] = tag.getText();
        }
        tagSummaryCalculator(instanceId, tagsArray, false);
        return tagsArray;
    }

    private void tagSummary(int tag1, int tag2, boolean increment) {
        Query query = em.createNamedQuery("TagSummary.2tags");
        query.setParameter("tag1", tag1);
        query.setParameter("tag2", tag2);
        if (increment) {
            incrementTagSummary(query, tag1, tag2, 0, 0, 0);
        } else {
            decrementTagSummary(query, tag1, tag2, 0, 0, 0);
        }
    }

    private void tagSummary(int tag1, int tag2, int tag3, boolean increment) {
        Query query = em.createNamedQuery("TagSummary.3tags");
        query.setParameter("tag1", tag1);
        query.setParameter("tag2", tag2);
        query.setParameter("tag3", tag3);
        if (increment) {
            incrementTagSummary(query, tag1, tag2, tag3, 0, 0);
        } else {
            decrementTagSummary(query, tag1, tag2, tag3, 0, 0);
        }
    }

    private void tagSummary(int tag1, int tag2, int tag3, int tag4, boolean increment) {
        Query query = em.createNamedQuery("TagSummary.4tags");
        query.setParameter("tag1", tag1);
        query.setParameter("tag2", tag2);
        query.setParameter("tag3", tag3);
        query.setParameter("tag4", tag4);
        if (increment) {
            incrementTagSummary(query, tag1, tag2, tag3, tag4, 0);
        } else {
            decrementTagSummary(query, tag1, tag2, tag3, tag4, 0);
        }
    }

    private void tagSummary(int tag1, int tag2, int tag3, int tag4, int tag5, boolean increment) {
        Query query = em.createNamedQuery("TagSummary.5tags");
        query.setParameter("tag1", tag1);
        query.setParameter("tag2", tag2);
        query.setParameter("tag3", tag3);
        query.setParameter("tag4", tag4);
        query.setParameter("tag5", tag5);
        if (increment) {
            incrementTagSummary(query, tag1, tag2, tag3, tag4, tag5);
        } else {
            decrementTagSummary(query, tag1, tag2, tag3, tag4, tag5);
        }
    }

    private void incrementTagSummary(Query query, int tag1, int tag2, int tag3, int tag4, int tag5) {
        List results = query.getResultList();
        TagSummary tagSummary;
        if (results.size() == 0) {
            tagSummary = new TagSummary();
            tagSummary.setTag1(tag1);
            tagSummary.setTag2(tag2);
            tagSummary.setTag3(tag3);
            tagSummary.setTag4(tag4);
            tagSummary.setTag5(tag5);
            tagSummary.setSize(1);
            em.persist(tagSummary);
        } else {
            tagSummary = (TagSummary) results.get(0);
            tagSummary.setSize(tagSummary.getSize() + 1);
        }
    }

    private void decrementTagSummary(Query query, int tag1, int tag2, int tag3, int tag4, int tag5) {
        List results = query.getResultList();
        TagSummary tagSummary;
        if (results.size() == 1) {
            tagSummary = (TagSummary) results.get(0);
            if (tagSummary.getSize() == 1) {
                em.remove(tagSummary);
            } else {
                tagSummary.setSize(tagSummary.getSize() - 1);
            }
        } else {
            log.error("TagSummary error : " + results.size() + " tagSummaries for " + tag1 + "|" + tag2 + "|" + tag3 + "|" +
                    tag4 + "|" + tag5);
        }
    }
}
