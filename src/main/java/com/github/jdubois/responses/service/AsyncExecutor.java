package com.github.jdubois.responses.service;

/**
 * @author Julien Dubois
 */
public interface AsyncExecutor {

    void asyncCleanupApplication();

    void asyncAddSearchQuery(int instanceId, String query);

    void countQuestionView(Long questionId);
}
