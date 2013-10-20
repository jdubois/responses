package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.*;
import com.github.jdubois.responses.service.DeletionService;
import com.github.jdubois.responses.service.ExpertizeService;
import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.service.TagSummaryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * @author Julien Dubois
 */
@Repository
@Transactional
@Secured("ROLE_SU")
public class DeletionServiceImpl implements DeletionService {

    private final Log log = LogFactory.getLog(DeletionService.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TagSummaryService tagSummaryService;

    @Autowired
    private ExpertizeService expertizeService;

    @Autowired
    private InstanceService instanceService;

    public void deleteInstance(int instanceId) {
        log.warn("Deleting instance #" + instanceId);
        Instance instance = this.em.find(Instance.class, instanceId);

        Collection<Question> questions = em.createNamedQuery("Question.getAllQuestionsForInstance")
                .setParameter("instanceId", instanceId)
                .getResultList();

        batchDeleteQuestions(questions);

        Collection<User> users = em.createNamedQuery("User.findAllUsersForInstance")
                .setParameter("instanceId", instanceId)
                .getResultList();

        for (User user : users) {
            Set<Instance> instances = user.getInstances();
            instances.remove(instance);
            user.setInstances(instances);
            Set<Tag> tmpTags = new HashSet<Tag>();
            for (Tag tag : user.getFavoriteTags()) {
                if (tag.getInstance().equals(instance)) {
                    tmpTags.add(tag);
                }
            }
            Set<Tag> favoriteTags = user.getFavoriteTags();
            favoriteTags.removeAll(tmpTags);
            user.setFavoriteTags(favoriteTags);

            tmpTags = new HashSet<Tag>();
            for (Tag tag : user.getIgnoredTags()) {
                if (tag.getInstance().equals(instance)) {
                    tmpTags.add(tag);
                }
            }
            Set<Tag> ignoredTags = user.getIgnoredTags();
            ignoredTags.removeAll(tmpTags);
            user.setIgnoredTags(ignoredTags);
        }

        Collection<Tag> tags = em.createNamedQuery("Tag.getAllTagsForInstance")
                .setParameter("instanceId", instanceId)
                .getResultList();

        for (Tag tag : tags) {
            em.remove(tag);
        }
        em.remove(instance);
    }

    private void batchDeleteQuestions(Collection<Question> questions) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        transactionTemplate.setTimeout(30);
        for (final Question question : questions) {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    deleteQuestion(question.getId());
                }
            });
        }
    }

    public void deleteQuestion(long questionId) {
        log.warn("Deleting question #" + questionId);
        Question question = em.find(Question.class, questionId);
        Instance instance = question.getInstance();
        List<Long> answersId = new ArrayList<Long>();
        for (Answer answer : question.getAnswers()) {
            answersId.add(answer.getId());
        }
        for (Long answerId : answersId) {
            deleteAnswer(answerId);
        }
        for (QuestionComment comment : question.getQuestionComments()) {
            em.remove(comment);
        }
        for (QuestionVote vote : question.getQuestionVotes().values()) {
            em.remove(vote);
        }
        for (Tag tag : question.getTags()) {
            tag.setSize(tag.getSize() - 1);
        }
        tagSummaryService.cleanQuestionTags(question.getTags(), instance.getId());
        question.setTags(null);
        for (Watch watch : question.getWatchs()) {
            em.remove(watch);
        }
        for (Workflow workflow : question.getWorkflows()) {
            em.remove(workflow);
        }
        instanceService.decrementInstanceSize(instance.getId());
        em.remove(question);
    }

    public void deleteAnswer(long answerId) {
        log.warn("Deleting answer #" + answerId);

        Answer answer = em.find(Answer.class, answerId);
        Question question = answer.getQuestion();
        Set<Tag> tags = question.getTags();
        User answerUser = answer.getUser();
        for (AnswerVote vote : answer.getAnswerVotes().values()) {
            expertizeService.removeExpertize(answerUser, tags, vote.getValue());
            em.remove(vote);
        }
        if (question.getBestAnswerId() == answer.getId()) {
            question.setBestAnswerId(-1);
            expertizeService.removeExpertize(answerUser, tags, Expertize.BEST_ANSWER_VALUE);
        }
        for (AnswerComment comment : answer.getAnswerComments()) {
            em.remove(comment);
        }
        Set<Answer> answers = question.getAnswers();
        answers.remove(answer);
        question.setAnswers(answers);
        question.setAnswersSize(question.getAnswersSize() - 1);
        em.remove(answer);
    }

    public void deleteQuestionComment(long commentId) {
        log.warn("Deleting question comment #" + commentId);
        QuestionComment comment = em.find(QuestionComment.class, commentId);
        em.remove(comment);
        Question question = comment.getQuestion();
        Set<QuestionComment> comments = question.getQuestionComments();
        comments.remove(comment);
        question.setQuestionComments(comments);
    }

    public void deleteAnswerComment(long commentId) {
        log.warn("Deleting answer comment #" + commentId);
        AnswerComment comment = em.find(AnswerComment.class, commentId);
        em.remove(comment);
        Answer answer = comment.getAnswer();
        Set<AnswerComment> comments = answer.getAnswerComments();
        comments.remove(comment);
        answer.setAnswerComments(comments);
    }
}
