package com.github.jdubois.responses.service;

/**
 * @author Julien Dubois
 */
public interface DeletionService {

    void deleteInstance(int instanceId);

    void deleteQuestion(long questionId);

    void deleteAnswer(long answerId);

    void deleteQuestionComment(long commentId);

    void deleteAnswerComment(long commentId);
}
