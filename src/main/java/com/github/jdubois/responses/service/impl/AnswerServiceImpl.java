package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.*;
import com.github.jdubois.responses.service.*;
import com.github.jdubois.responses.service.exception.HtmlValidationException;
import com.github.jdubois.responses.service.exception.ResponsesSecurityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Repository("AnswerManager")
@Secured("ROLE_USER")
public class AnswerServiceImpl implements AnswerService {

    private final Log log = LogFactory.getLog(AnswerServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ExpertizeService expertizeService;

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private AntiSamyService antiSamyService;

    @Autowired
    private WatchService watchService;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void answerQuestion(Instance instance, long questionId, String text) throws HtmlValidationException {
        antiSamyService.cleanHtml(text);
        User user = this.userService.getCurrentUser();
        Question question = em.find(Question.class, questionId);
        if (question.getInstance().getId() != instance.getId()) {
            log.error("User \"" + user.getId() + "\" should not have answered question \"" + questionId + "\" on instance \"" + instance.getId() + "\"");
        } else {
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setText(text); //We store the orginal text, not the sanitized one
            answer.setUser(user);
            answer.setVotesSize(0);
            answer.setCreationDate(Calendar.getInstance().getTime());
            em.persist(answer);
            question.getAnswers().add(answer);
            question.setAnswersSize(question.getAnswersSize() + 1);
            question.setUpdateDate(Calendar.getInstance().getTime());
            String message = WatchService.NEW_ANSWER + text;
            watchService.alertUsers(question, message);
            watchService.watchQuestion(questionId);
        }
    }

    @Transactional
    public void editAnswer(Long answerId, String answerText) throws HtmlValidationException {
        Answer answer = em.find(Answer.class, answerId);
        antiSamyService.cleanHtml(answerText);
        answer.setText(answerText); //We store the orginal text, not the sanitized one
        Question question = answer.getQuestion();
        question.setUpdateDate(Calendar.getInstance().getTime());
        String message = WatchService.EDIT_ANSWER + answerText;
        watchService.alertUsers(question, message);
    }

    @Transactional
    @Secured("ROLE_USER")
    public int voteForAnswer(String instanceName, long answerId, int value) {
        if (log.isDebugEnabled()) {
            log.debug("Vote for Answer=" + answerId + "|value=" + value);
        }

        Answer answer = em.find(Answer.class, answerId);
        int answerVotes = answer.getVotesSize();

        User user = userService.getCurrentUser();
        Instance instance = instanceService.getInstanceByName(instanceName);
        if (!answer.getQuestion().getInstance().equals(instance)) {
            throw new ResponsesSecurityException("User " + user.getId() + " tried to vote for answer " +
                    answer.getId() + " on instance " + instance.getName());
        }
        if (user == null) {
            return answerVotes;
        } else if (user.equals(answer.getUser())) {
            return answerVotes; // the user cannot vote on a question he asked
        }

        //check the vote value
        if ((value != 1) && (value != -1) && (value != 0)) {
            log.warn("Trying to vote with value=" + value + " for User=[" + user.toString() +
                    "] and Answer[" + answer.toString() + "]");
            throw new IllegalArgumentException("Vote value is not legal");
        }

        //Find an existing vote
        AnswerVote vote = answer.getAnswerVotes().get(user.getId());
        //Expertize
        User answerUser = answer.getUser();
        Set<Tag> questionTags = answer.getQuestion().getTags();
        //If no vote exists, create a new one
        if (vote == null) {
            if (value != 0) {
                vote = new AnswerVote();
                vote.setAnswer(answer);
                vote.setUserId(user.getId());
                vote.setValue(value);
                answerVotes += value;
                em.persist(vote);
                answer.getAnswerVotes().put(user.getId(), vote);
            }
        } else { // otherwise, use the existing vote
            //remove the vote from the question votes
            answerVotes -= vote.getValue();
            //remove expertize
            if (vote.getValue() > 0) {
                expertizeService.removeExpertize(answerUser, questionTags, Expertize.VOTE_VALUE);
            } else if (vote.getValue() < 0) {
                expertizeService.addExpertize(answerUser, questionTags, Expertize.VOTE_VALUE);
            }
            if (value == 0) {  // voted 0, suppress the vote
                em.remove(vote);
                answer.getAnswerVotes().remove(user.getId());
            } else {
                answerVotes += value;
                vote.setValue(value);
                answer.getAnswerVotes().put(user.getId(), vote);
            }
        }
        answer.setVotesSize(answerVotes);
        //add expertize
        if (value > 0) {
            expertizeService.addExpertize(answerUser, questionTags, Expertize.VOTE_VALUE);
        } else if (value < 0) {
            expertizeService.removeExpertize(answerUser, questionTags, Expertize.VOTE_VALUE);
        }

        return answerVotes;
    }

    @Transactional
    public String bestAnswer(String instanceName, long answerId) {
        Answer answer = em.find(Answer.class, answerId);
        User currentUser = userService.getCurrentUser();
        Question question = answer.getQuestion();
        Instance instance = instanceService.getInstanceByName(instanceName);
        if (!answer.getQuestion().getInstance().equals(instance)) {
            throw new ResponsesSecurityException("User " + currentUser.getId() + " tried to select best answer " +
                    answer.getId() + " on instance " + instance.getName());
        }
        if (!question.getUser().equals(currentUser)) {
            if (log.isInfoEnabled()) {
                log.info("User " + currentUser.getId() + " does not have the right to answer question " +
                        question.getId());
            }
            return "userError";
        }
        User answerUser = answer.getUser();
        Set<Tag> questionTags = question.getTags();
        question.setUpdateDate(Calendar.getInstance().getTime());
        if (question.getBestAnswerId() == answer.getId()) {
            question.setBestAnswerId(-1);
            expertizeService.removeExpertize(answerUser, questionTags, Expertize.BEST_ANSWER_VALUE);
            return "unselect";
        } else {
            question.setBestAnswerId(answer.getId());
            expertizeService.addExpertize(answerUser, questionTags, Expertize.BEST_ANSWER_VALUE);
            String message = WatchService.BEST_ANSWER + answer.getText();
            this.watchService.alertUsers(question, message);
            return "ok";
        }
    }

    @Transactional
    public String commentAnswer(String instanceName, long answerId, String value) {
        User currentUser = userService.getCurrentUser();
        Answer answer = getAnswer(instanceName, answerId, currentUser);
        Question question = answer.getQuestion();
        AnswerComment comment = new AnswerComment();
        comment.setAnswer(answer);
        comment.setUser(currentUser);
        comment.setValue(value);
        comment.setCreationDate(Calendar.getInstance().getTime());
        em.persist(comment);
        answer.getAnswerComments().add(comment);
        question.setUpdateDate(Calendar.getInstance().getTime());
        String message = WatchService.NEW_ANSWER_COMMENT + value;
        watchService.alertUsers(question, message);
        watchService.watchQuestion(question.getId());
        return "ok";
    }

    @Transactional(readOnly = true)
    public Answer getAnswer(String instanceName, long answerId) {
        User currentUser = userService.getCurrentUser();
        Answer answer = getAnswer(instanceName, answerId, currentUser);
        Hibernate.initialize(answer.getAnswerComments());
        return answer;
    }

    private Answer getAnswer(String instanceName, long answerId, User currentUser) {
        Answer answer = em.find(Answer.class, answerId);
        Instance instance = instanceService.getInstanceByName(instanceName);
        if (!answer.getQuestion().getInstance().equals(instance)) {
            throw new ResponsesSecurityException("User " + currentUser.getId() + " tried to comment answer " +
                    answer.getId() + " on instance " + instance.getName());
        }
        return answer;
    }
}
