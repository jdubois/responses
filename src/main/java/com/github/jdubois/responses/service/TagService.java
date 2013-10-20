package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Tag;
import com.github.jdubois.responses.service.dto.TagSummaryInformation;

import java.util.List;
import java.util.Set;

/**
 * @author Julien Dubois
 */
public interface TagService {

    Tag addTag(int instanceId, String text);

    Tag getTagFromText(int instanceId, String text);

    int getTagIdFromText(int instanceId, String text);

    List<Tag> getPopularTags(int instanceId, int maxResults);

    TagSummaryInformation getTagSummaryFor(int instanceId);

    TagSummaryInformation getTagSummaryFor(int instanceId, String tag1);

    TagSummaryInformation getTagSummaryFor(int instanceId, String tag1, String tag2);

    TagSummaryInformation getTagSummaryFor(int instanceId, String tag1, String tag2, String tag3);

    TagSummaryInformation getTagSummaryFor(int instanceId, String tag1, String tag2, String tag3, String tag4);

    TagSummaryInformation getTagSummaryFor(int instanceId, String tag1, String tag2, String tag3, String tag4, String tag5);

    void cleanCacheTagSummaryInfo(int instanceId, Set<Tag> tags);

    void cleanCacheTagSummaryInfo(int instanceId, String[] tagsArray);
}
