package com.github.jdubois.responses.service;

import com.github.jdubois.responses.service.dto.SearchQuestionResult;

import java.util.List;

/**
 * @author Julien Dubois
 */
public interface SearchEngineService {

    public static final int SORT_BY_DEFAULT = 0;
    public static final int SORT_BY_VOTE = 1;
    public static final int SORT_BY_CREATION_DATE = 2;
    public static final int SORT_BY_UPDATE_DATE = 3;

    void rebuildLuceneIndex();

    List<Object[]> suggestQuestions(int instanceId, String query);

    SearchQuestionResult searchQuestions(int instanceId, String query);

    SearchQuestionResult searchQuestions(int instanceId, String query, boolean showNegativeQuestions, int sortBy, int index);

    List<Object[]> searchTags(int instanceId, String query);
}
