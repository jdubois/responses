package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Answer;
import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.service.exception.HtmlValidationException;

/**
 * @author Julien Dubois
 */
public interface AnswerService {

    void answerQuestion(Instance instance, long questionId, String text) throws HtmlValidationException;

    int voteForAnswer(String instanceName, long answerId, int value);

    String bestAnswer(String instanceName, long answerId);

    String commentAnswer(String instanceName, long answerId, String value);

    Answer getAnswer(String instanceName, long answerId);

    void editAnswer(Long answerId, String answerText) throws HtmlValidationException;
}
