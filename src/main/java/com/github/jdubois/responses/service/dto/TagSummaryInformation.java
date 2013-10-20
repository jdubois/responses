package com.github.jdubois.responses.service.dto;

import com.github.jdubois.responses.model.Tag;

import java.io.Serializable;
import java.util.List;

/**
 * @author Julien Dubois
 */
public class TagSummaryInformation implements Serializable {

    private int size;

    private List<Tag> relatedTags;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Tag> getRelatedTags() {
        return relatedTags;
    }

    public void setRelatedTags(List<Tag> relatedTags) {
        this.relatedTags = relatedTags;
    }
}
