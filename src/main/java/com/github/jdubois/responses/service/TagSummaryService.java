package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Tag;

import java.util.Set;

/**
 * @author Julien Dubois
 */
public interface TagSummaryService {

    void tagSummaryCalculator(int instanceId, String[] tagsArray, boolean increment);

    String[] cleanQuestionTags(Set<Tag> tags, int instanceId);
}
