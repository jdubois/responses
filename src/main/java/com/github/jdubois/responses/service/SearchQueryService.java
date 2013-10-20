package com.github.jdubois.responses.service;

/**
 * @author Julien Dubois
 */
public interface SearchQueryService {

    void addSearchQuery(int instanceId, String query);

    void cleanupSearchQueries();
}
