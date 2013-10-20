package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.model.Workflow;
import com.github.jdubois.responses.service.exception.HtmlValidationException;
import com.github.jdubois.responses.service.exception.QOSException;

import java.util.Collection;
import java.util.Set;

/**
 * @author Julien Dubois
 */
public interface QuestionService {

    long askQuestion(int instanceId, String title, String text, String[] tagsArray) throws HtmlValidationException, QOSException;

    String[] addQuestionTag(long questionId, String tagText);

    String[] deleteQuestionTag(long questionId, String tagText);

    Collection<Question> showLatestQuestions(int instanceId, boolean updated, boolean unanswered, int questionIndex);

    Collection<Question> showQuestionsForTags(int instanceId, boolean updated, boolean unanswered, String[] tagsArray, int questionIndex);

    int voteForQuestion(String instanceName, long questionId, int value);

    Question viewQuestion(Long questionId);

    Question getQuestion(Long questionId);

    void editQuestion(Long questionId, String title, String text) throws HtmlValidationException;

    void commentQuestion(String instanceName, long questionId, String value);

    void countQuestionView(Long questionId);

    long countWatchedQuestions(User user);

    Collection<Question> listWatchedQuestions(User user, int index);

    Set<Workflow> getWorkflows(String instanceName, Long questionId);

    Set<Workflow> updateWorkflow(String instanceName, Long questionId, int step, String userId);

    void hydrateQuestions(Collection<Question> questions);

    Collection<Question> showTopQuestions(int instanceId, int type, int when);

    Collection<Question> showTopQuestions(int instanceId, int type, int when, String[] selTagsArray);
}
