package com.github.jdubois.responses.service.impl;


import com.github.jdubois.responses.model.*;
import com.github.jdubois.responses.service.*;
import com.github.jdubois.responses.service.exception.HtmlValidationException;
import com.github.jdubois.responses.service.exception.QOSException;
import com.github.jdubois.responses.service.exception.ResponsesSecurityException;
import com.github.jdubois.responses.web.instance.exception.QuestionNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

/**
 * @author Julien Dubois
 */
@Repository("QuestionService")
@SuppressWarnings("unchecked")
public class QuestionServiceImpl implements QuestionService {

    private final static int PAGE_SIZE = 20;

    //constants for the top questions
    public static final int TYPE_POPULAR = 0;
    public static final int TYPE_VIEWS = 1;
    public static final int WHEN_TODAY = 0;
    public static final int WHEN_WEEK = 1;
    public static final int WHEN_MONTH = 2;
    public static final int WHEN_EVER = 3;

    private final Log log = LogFactory.getLog(QuestionServiceImpl.class);

    private PeriodFormatter formatterYearFR;

    private PeriodFormatter formatterWeekFR;

    private PeriodFormatter formatterHourFR;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TagSummaryService tagSummaryService;

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private AntiSamyService antiSamyService;

    @Autowired
    private WatchService watchService;

    @Autowired
    private AnswerService answerService;

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    void init() {
        this.formatterYearFR = new PeriodFormatterBuilder()
                .printZeroNever()
                .appendYears()
                .appendSuffix(" an", " ans")
                .appendSeparator(" et ")
                .appendMonths()
                .appendSuffix(" mois")
                .toFormatter();

        this.formatterWeekFR = new PeriodFormatterBuilder()
                .printZeroNever()
                .appendWeeks()
                .appendSuffix(" semaine", " semaines")
                .appendSeparator(" et ")
                .appendDays()
                .appendSuffix(" jour", " jours")
                .toFormatter();

        this.formatterHourFR = new PeriodFormatterBuilder()
                .appendHours()
                .appendSuffix(" heure", " heures")
                .appendSeparator(" et ")
                .appendMinutes()
                .appendSuffix(" minute", " minutes")
                .toFormatter();
    }

    @Transactional
    @Secured("ROLE_USER")
    public long askQuestion(int instanceId, String title, String text, String[] tagsArray) throws HtmlValidationException, QOSException {
        Instance instance = instanceService.getInstance(instanceId);
        User user = userService.getCurrentUser();
        if (instance.getType() == Instance.TYPE_PUBLIC) {
            Query qLastQuestion = em.createNamedQuery("Question.getLastQuestionDateForUser");
            qLastQuestion.setParameter("userId", user.getId());
            List<Date> lastDates = qLastQuestion.getResultList();
            if (lastDates.size() == 1) {
                Date lastDate = lastDates.get(0);
                if (lastDate != null) {
                    Calendar spamCal = Calendar.getInstance();
                    spamCal.add(Calendar.MINUTE, -2);
                    if (lastDate.after(spamCal.getTime())) {
                        if (log.isInfoEnabled()) {
                            log.info("Question antispam for question by user #" + user.getId());
                        }
                        throw new QOSException("Last question from this user was less than 2 minutes ago.");
                    }
                }
            }
        }
        Question question = new Question();
        question.setTitle(title);
        antiSamyService.cleanHtml(text);
        question.setText(text); //We store the original text, not the sanitized one
        question.setUser(user);
        Date now = Calendar.getInstance().getTime();
        question.setCreationDate(now);
        question.setUpdateDate(now);
        question.setVotesSize(0);
        question.setAnswersSize(0);
        question.setInstance(instance);
        question.setWfState(Workflow.STATE_NONE);
        question.setWfDate(now);
        question.setWfAssignedUser(-1);

        Set<Tag> tags = new TreeSet<Tag>();
        for (String tagText : tagsArray) {
            Tag tag = tagService.getTagFromText(instanceId, tagText);
            if (tag == null) {
                tag = tagService.addTag(instanceId, tagText);
            }
            if (tag != null && !tags.contains(tag)) { //to remove duplicates in the array
                tag.setSize(tag.getSize() + 1);
                tags.add(tag);
            }
        }
        question.setTags(tags);
        String newTagsArray[] = new String[tags.size()];
        int index = 0;
        for (Tag tag : tags) {
            newTagsArray[index++] = tag.getText();
        }
        tagSummaryService.tagSummaryCalculator(instanceId, newTagsArray, true);
        question.setWatchs(new HashSet());
        this.em.persist(question);
        return question.getId();
    }


    /**
     * Add a Tag to an existing question.
     */
    @Transactional
    public String[] addQuestionTag(long questionId, String tagText) {
        User user = userService.getCurrentUser();
        Question question = em.find(Question.class, questionId);
        if (user == null || !user.equals(question.getUser())) {
            log.warn("User " + user + " tried to add a tag to question " + question.getId());
            return new String[0];
        } else {
            int instanceId = question.getInstance().getId();
            Set<Tag> tags = question.getTags();
            if (tags.size() < 5) {
                Tag tagToAdd = tagService.getTagFromText(instanceId, tagText);
                if (tagToAdd == null) {
                    tagToAdd = tagService.addTag(instanceId, tagText);
                }
                if (tags.contains(tagToAdd)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Tag " + tagText + " is already applied on question " + questionId);
                    }
                    return new String[0];
                } else {
                    tagSummaryService.cleanQuestionTags(tags, instanceId);
                    tags.add(tagToAdd);
                    question.setTags(tags);
                    tagToAdd.setSize(tagToAdd.getSize() + 1);
                    String newTagsArray[] = new String[tags.size()];
                    int index = 0;
                    for (Tag tag : tags) {
                        newTagsArray[index++] = tag.getText();
                    }
                    tagSummaryService.tagSummaryCalculator(instanceId, newTagsArray, true);

                    //Update the expertize of the users who have answered questions
                    for (Answer answer : question.getAnswers()) {
                        int totalPoints = answer.getVotesSize() * Expertize.VOTE_VALUE;
                        if (question.getBestAnswerId() == answer.getId()) {
                            totalPoints += Expertize.BEST_ANSWER_VALUE;
                        }
                        User answerUser = answer.getUser();
                        Map<Integer, Expertize> expertizes = answerUser.getExpertizes();
                        Expertize expertize = expertizes.get(tagToAdd.getId());
                        if (expertize != null) {
                            expertize.setPoints(expertize.getPoints() + totalPoints);
                        } else {
                            expertize = new Expertize();
                            expertize.setTagId(tagToAdd.getId());
                            expertize.setUser(answerUser);
                            expertize.setPoints(totalPoints);
                            em.persist(expertize);
                            expertizes.put(tagToAdd.getId(), expertize);
                        }
                    }

                    return newTagsArray;
                }
            }
            return new String[0];
        }
    }

    /**
     * Remove a tag from an existing question.
     */
    @Transactional
    public String[] deleteQuestionTag(long questionId, String tagText) {
        User user = userService.getCurrentUser();
        Question question = em.find(Question.class, questionId);
        if (user == null || !user.equals(question.getUser())) {
            log.warn("User " + user + " tried to delete a tag to question " + question.getId());
            return new String[0];
        } else {
            int instanceId = question.getInstance().getId();
            Set<Tag> tags = question.getTags();
            String tagsArray[] = tagSummaryService.cleanQuestionTags(tags, instanceId);

            Tag tagToDelete = tagService.getTagFromText(instanceId, tagText);

            tags.remove(tagToDelete);
            question.setTags(tags);
            tagToDelete.setSize(tagToDelete.getSize() - 1);
            String newTagsArray[] = new String[tags.size()];
            int index = 0;
            for (Tag tag : tags) {
                newTagsArray[index++] = tag.getText();
            }
            tagSummaryService.tagSummaryCalculator(instanceId, newTagsArray, true);

            //Update the expertize of the users who have answered questions
            for (Answer answer : question.getAnswers()) {
                int totalPoints = answer.getVotesSize() * Expertize.VOTE_VALUE;
                if (question.getBestAnswerId() == answer.getId()) {
                    totalPoints += Expertize.BEST_ANSWER_VALUE;
                }
                User answerUser = answer.getUser();
                Map<Integer, Expertize> expertizes = answerUser.getExpertizes();
                Expertize expertize = expertizes.get(tagToDelete.getId());
                if (expertize != null) {
                    expertize.setPoints(expertize.getPoints() - totalPoints);
                }
            }

            return tagsArray;
        }
    }

    /**
     * Show the latest questions for an instance.
     * <p/>
     * - updated=true : all the questions that have been recently updated
     * - updated=false : all the questions that have been recently created
     * - unanswered=true : all the questions that are unanswered, and have been recently updated (if they are unanswered, they are probably also recently created)
     */
    @Transactional(readOnly = true)
    public Collection<Question> showLatestQuestions(int instanceId, boolean updated, boolean unanswered, int questionIndex) {
        Query query;
        if (!unanswered) {
            if (updated) {
                query = em.createNamedQuery("Question.getLatestQuestions");
            } else {
                query = em.createNamedQuery("Question.getLatestQuestionsCreated");
            }
        } else {
            query = em.createNamedQuery("Question.getLatestQuestionsUnanswered");
        }
        query.setParameter("instanceId", instanceId);
        if (questionIndex > 0) {
            query.setFirstResult(questionIndex * PAGE_SIZE);
        }
        query.setMaxResults(PAGE_SIZE);
        Collection<Question> questions = (Collection<Question>) query.getResultList();
        hydrateQuestions(questions);
        return questions;
    }

    @Transactional(readOnly = true)
    public Collection<Question> showQuestionsForTags(int instanceId, boolean updated, boolean unanswered, String[] tagsArray, int questionIndex) {
        Integer[] tagIds = transformTagsArrayToTagsIdArray(instanceId, tagsArray);
        Query query;
        if (tagIds.length == 1) {
            if (!unanswered) {
                if (updated) {
                    query = em.createNamedQuery("Question.getQuestionsFor1Tag");
                } else {
                    query = em.createNamedQuery("Question.getQuestionsFor1TagCreated");
                }
            } else {
                query = em.createNamedQuery("Question.getQuestionsFor1TagUnanswered");
            }
            query.setParameter("tagId1", tagIds[0]);
        } else if (tagIds.length == 2) {
            if (!unanswered) {
                if (updated) {
                    query = em.createNamedQuery("Question.getQuestionsFor2Tag");
                } else {
                    query = em.createNamedQuery("Question.getQuestionsFor2TagCreated");
                }
            } else {
                query = em.createNamedQuery("Question.getQuestionsFor2TagUnanswered");
            }
            query.setParameter("tagId1", tagIds[0]);
            query.setParameter("tagId2", tagIds[1]);
        } else if (tagIds.length == 3) {
            if (!unanswered) {
                if (updated) {
                    query = em.createNamedQuery("Question.getQuestionsFor3Tag");
                } else {
                    query = em.createNamedQuery("Question.getQuestionsFor3TagCreated");
                }
            } else {
                query = em.createNamedQuery("Question.getQuestionsFor3TagUnanswered");
            }
            query.setParameter("tagId1", tagIds[0]);
            query.setParameter("tagId2", tagIds[1]);
            query.setParameter("tagId3", tagIds[2]);
        } else if (tagIds.length == 4) {
            if (!unanswered) {
                if (updated) {
                    query = em.createNamedQuery("Question.getQuestionsFor4Tag");
                } else {
                    query = em.createNamedQuery("Question.getQuestionsFor4TagCreated");
                }
            } else {
                query = em.createNamedQuery("Question.getQuestionsFor4TagUnanswered");
            }
            query.setParameter("tagId1", tagIds[0]);
            query.setParameter("tagId2", tagIds[1]);
            query.setParameter("tagId3", tagIds[2]);
            query.setParameter("tagId4", tagIds[3]);
        } else if (tagIds.length == 5) {
            if (!unanswered) {
                if (updated) {
                    query = em.createNamedQuery("Question.getQuestionsFor5Tag");
                } else {
                    query = em.createNamedQuery("Question.getQuestionsFor5TagCreated");
                }
            } else {
                query = em.createNamedQuery("Question.getQuestionsFor5TagUnanswered");
            }
            query.setParameter("tagId1", tagIds[0]);
            query.setParameter("tagId2", tagIds[1]);
            query.setParameter("tagId3", tagIds[2]);
            query.setParameter("tagId4", tagIds[3]);
            query.setParameter("tagId5", tagIds[4]);
        } else {
            log.warn("Number of tags exceeded! Reverting to normal query.");
            query = em.createNamedQuery("Question.getLatestQuestions");
        }

        query.setParameter("instanceId", instanceId);

        if (questionIndex > 0) {
            query.setFirstResult(questionIndex * PAGE_SIZE);
        }
        query.setMaxResults(PAGE_SIZE);
        Collection<Question> questions = (Collection<Question>) query.getResultList();
        hydrateQuestions(questions);
        return questions;
    }

    private Integer[] transformTagsArrayToTagsIdArray(int instanceId, String[] tagsArray) {
        Integer[] tagIds = new Integer[tagsArray.length];
        int index = 0;
        for (String tagName : tagsArray) {
            int tagId = this.tagService.getTagIdFromText(instanceId, tagName);
            if (tagId != 0) { // Do not search on non existing tags
                tagIds[index++] = tagId;
            }
        }
        return tagIds;
    }

    @Transactional
    public Collection<Question> showTopQuestions(int instanceId, int type, int when) {
        return showTopQuestions(instanceId, type, when, new String[0]);
    }

    @Transactional
    public Collection<Question> showTopQuestions(int instanceId, int type, int when, String[] tagsArray) {
        Integer[] tagIds = transformTagsArrayToTagsIdArray(instanceId, tagsArray);
        Query query;
        if (when < WHEN_EVER) {
            if (type == TYPE_POPULAR) {
                if (tagIds.length == 0) {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularForDate");
                } else if (tagIds.length == 1) {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularFor1TagForDate");
                } else if (tagIds.length == 2) {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularFor2TagForDate");
                } else if (tagIds.length == 3) {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularFor3TagForDate");
                } else if (tagIds.length == 4) {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularFor4TagForDate");
                } else {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularFor5TagForDate");
                }
            } else {
                if (tagIds.length == 0) {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsForDate");
                } else if (tagIds.length == 1) {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsFor1TagForDate");
                } else if (tagIds.length == 2) {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsFor2TagForDate");
                } else if (tagIds.length == 3) {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsFor3TagForDate");
                } else if (tagIds.length == 4) {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsFor4TagForDate");
                } else {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsFor5TagForDate");
                }
            }
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR, 0);
            if (when == WHEN_TODAY) {
                cal.add(Calendar.DATE, -1);
            } else if (when == WHEN_WEEK) {
                cal.add(Calendar.DATE, -7);
            } else if (when == WHEN_MONTH) {
                cal.add(Calendar.MONTH, -1);
            }
            query.setParameter("creationDate", cal.getTime());
        } else {
            if (type == TYPE_POPULAR) {
                if (tagIds.length == 0) {
                    query = em.createNamedQuery("Question.getTopQuestionsPopular");
                } else if (tagIds.length == 1) {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularFor1Tag");
                } else if (tagIds.length == 2) {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularFor2Tag");
                } else if (tagIds.length == 3) {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularFor3Tag");
                } else if (tagIds.length == 4) {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularFor4Tag");
                } else {
                    query = em.createNamedQuery("Question.getTopQuestionsPopularFor5Tag");
                }
            } else {
                if (tagIds.length == 0) {
                    query = em.createNamedQuery("Question.getTopQuestionsViews");
                } else if (tagIds.length == 1) {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsFor1Tag");
                } else if (tagIds.length == 2) {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsFor2Tag");
                } else if (tagIds.length == 3) {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsFor3Tag");
                } else if (tagIds.length == 4) {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsFor4Tag");
                } else {
                    query = em.createNamedQuery("Question.getTopQuestionsViewsFor5Tag");
                }
            }
        }
        query.setParameter("instanceId", instanceId);
        if (tagIds.length > 0) {
            query.setParameter("tagId1", tagIds[0]);
        }
        if (tagIds.length > 1) {
            query.setParameter("tagId2", tagIds[1]);
        }
        if (tagIds.length > 2) {
            query.setParameter("tagId3", tagIds[2]);
        }
        if (tagIds.length > 3) {
            query.setParameter("tagId4", tagIds[3]);
        }
        if (tagIds.length > 4) {
            query.setParameter("tagId5", tagIds[4]);
        }
        query.setFirstResult(0);
        query.setMaxResults(PAGE_SIZE);
        Collection<Question> questions = (Collection<Question>) query.getResultList();
        hydrateQuestions(questions);
        return questions;
    }

    @Transactional
    public int voteForQuestion(String instanceName, long questionId, int value) {
        if (log.isDebugEnabled()) {
            log.debug("Vote for question=" + questionId + "|value=" + value);
        }

        Question question = em.find(Question.class, questionId);
        Instance instance = instanceService.getInstanceByName(instanceName);
        int questionVotes = question.getVotesSize();
        User user = userService.getCurrentUser();
        if (!question.getInstance().equals(instance)) {
            throw new ResponsesSecurityException("User " + user.getId() + " has tried to vote for question " +
                    question.getId() + " on instance " + instance.getName());
        }
        if (user == null) {
            return questionVotes;
        } else if (user.equals(question.getUser())) {
            return questionVotes; // the user cannot vote on a question he asked
        }

        //check the vote value
        if ((value != 1) && (value != -1) && (value != 0)) {
            log.warn("Trying to vote with value=" + value + " for User=[" + user.toString() +
                    "] and Question[" + question.toString() + "]");
            throw new IllegalArgumentException("Vote value is not legal");
        }

        //Find an existing vote
        QuestionVote vote = question.getQuestionVotes().get(user.getId());

        //If no vote exists, create a new one
        if (vote == null) {
            if (value != 0) {
                vote = new QuestionVote();
                vote.setQuestion(question);
                vote.setUserId(user.getId());
                vote.setValue(value);
                questionVotes += value;
                em.persist(vote);
                question.getQuestionVotes().put(user.getId(), vote);
            }
        } else { // otherwise, use the existing vote
            //remove the vote from the question votes
            questionVotes -= vote.getValue();
            if (value == 0) {  // voted 0, suppress the vote
                em.remove(vote);
                question.getQuestionVotes().remove(user.getId());
            } else {
                questionVotes += value;
                vote.setValue(value);
                question.getQuestionVotes().put(user.getId(), vote);
            }
        }
        question.setVotesSize(questionVotes);
        return questionVotes;
    }

    /**
     * View a question.
     */
    @Transactional(readOnly = true)
    public Question viewQuestion(Long questionId) {
        User user = userService.getCurrentUser();
        if (log.isDebugEnabled()) {
            log.debug("Get question id " + questionId);
        }
        Question question = em.find(Question.class, questionId);
        if (question == null) {
            throw new QuestionNotFoundException("Question id=" + questionId);
        }
        Hibernate.initialize(question.getWatchs());
        Hibernate.initialize(question.getQuestionComments());

        DateTime now = new DateTime();
        for (Answer answer : question.getAnswers()) {
            Hibernate.initialize(answer.getAnswerComments());
            Date date = answer.getCreationDate();
            answer.setPeriod(this.formatDateAsPeriod(now, date));

            if (user != null) {
                AnswerVote av = answer.getAnswerVotes().get(user.getId());
                if (av == null) {
                    answer.setCurrentUserVote(0);
                } else {
                    answer.setCurrentUserVote(av.getValue());
                }
            }
        }
        hydrateQuestion(question);
        return question;
    }

    /**
     * Edit a question.
     */
    @Transactional(readOnly = true)
    public Question getQuestion(Long questionId) {
        Question question = em.find(Question.class, questionId);
        if (question == null) {
            throw new QuestionNotFoundException("Question id=" + questionId);
        }
        return question;
    }

    @Transactional
    public void editQuestion(Long questionId, String title, String text) throws HtmlValidationException {
        Question question = em.find(Question.class, questionId);
        question.setTitle(title);
        antiSamyService.cleanHtml(text);
        question.setText(text); //We store the orginal text, not the sanitized one
        question.setUpdateDate(Calendar.getInstance().getTime());
        String message = WatchService.EDIT_QUESTION + text;
        watchService.alertUsers(question, message);
    }

    @Transactional
    public void commentQuestion(String instanceName, long questionId, String value) {
        Question question = em.find(Question.class, questionId);
        User currentUser = userService.getCurrentUser();
        Instance instance = instanceService.getInstanceByName(instanceName);
        if (!question.getInstance().equals(instance)) {
            throw new ResponsesSecurityException("User " + currentUser.getId() + " tried to comment answer " +
                    question.getId() + " on instance " + instance.getName());
        }
        QuestionComment comment = new QuestionComment();
        comment.setQuestion(question);
        comment.setUser(currentUser);
        comment.setValue(value);
        Date now = Calendar.getInstance().getTime();
        comment.setCreationDate(now);
        em.persist(comment);
        question.getQuestionComments().add(comment);
        question.setUpdateDate(now);
        String message = WatchService.NEW_QUESTION_COMMENT + value;
        watchService.alertUsers(question, message);
        watchService.watchQuestion(questionId);
    }

    @Transactional
    public void countQuestionView(Long questionId) {
        Question question = em.find(Question.class, questionId);
        question.setViews(question.getViews() + 1);
    }

    @Transactional(readOnly = true)
    public long countWatchedQuestions(User user) {
        Query query = em.createQuery("select count(q) from Question q join q.watchs w where w.user.id = :userId");
        query.setParameter("userId", user.getId());
        long result = (Long) query.getResultList().get(0);
        return result;
    }

    @Transactional(readOnly = true)
    public Collection<Question> listWatchedQuestions(User user, int questionIndex) {
        Query query = em.createQuery("select q from Question q join q.watchs w where w.user.id = :userId order by q.creationDate desc");
        query.setParameter("userId", user.getId());
        query.setFirstResult(questionIndex * PAGE_SIZE);
        query.setMaxResults(PAGE_SIZE);
        List<Question> questions = query.getResultList();
        hydrateQuestions(questions);
        return questions;
    }

    @Transactional(readOnly = true)
    public Set<Workflow> getWorkflows(String instanceName, Long questionId) {
        Instance instance = instanceService.getInstanceByName(instanceName);
        Question question = em.find(Question.class, questionId);
        if (!question.getInstance().equals(instance)) {
            User user = userService.getCurrentUser();
            throw new ResponsesSecurityException("User " + user.getId() + " tried to view question " +
                    question.getId() + " on instance " + instance.getId());
        }
        Hibernate.initialize(question.getWorkflows());
        return question.getWorkflows();
    }

    @Transactional
    public Set<Workflow> updateWorkflow(String instanceName, Long questionId, int step, String userId) {
        Instance instance = instanceService.getInstanceByName(instanceName);
        Question question = em.find(Question.class, questionId);
        User user = userService.getCurrentUser();
        if (!question.getInstance().equals(instance)) {
            throw new ResponsesSecurityException("User " + user.getId() + " tried to view question " +
                    question.getId() + " on instance " + instance.getId());
        }
        Set<Workflow> workflows = question.getWorkflows();
        if (workflows.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Start workflow for question " + question.getId() + " by user " + user.getId());
            }
            addWorkflow(Workflow.STATE_START, question, user, workflows);
        } else {
            Workflow lastWf = workflows.iterator().next();
            if (log.isDebugEnabled()) {
                log.debug("Last workflow : " + lastWf.getState());
            }
            if (lastWf.getState() == Workflow.STATE_START) {
                if (step == Workflow.STATE_VALIDATED) {
                    if (log.isDebugEnabled()) {
                        log.debug("Validate workflow for question " + question.getId() + " by user " + user.getId());
                    }
                    addWorkflow(Workflow.STATE_VALIDATED, question, user, workflows);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Reject workflow for question " + question.getId() + " by user " + user.getId());
                    }
                    addWorkflow(Workflow.STATE_NOT_VALIDATED, question, user, workflows);
                }
            } else if (lastWf.getState() == Workflow.STATE_NOT_VALIDATED) {
                if (step == Workflow.STATE_START) {
                    if (log.isDebugEnabled()) {
                        log.debug("Restart workflow for question " + question.getId() + " by user " + user.getId());
                    }
                    addWorkflow(Workflow.STATE_START, question, user, workflows);
                }
            } else if (lastWf.getState() == Workflow.STATE_VALIDATED ||
                    lastWf.getState() == Workflow.STATE_ASSIGNED ||
                    lastWf.getState() == Workflow.STATE_REJECTED) {

                if (step == Workflow.STATE_ASSIGNED) {
                    int userIdAsInt = 0;
                    User assignedUser = null;
                    try {
                        userIdAsInt = Integer.valueOf(userId);
                        assignedUser = userService.findUserById(userIdAsInt);
                    } catch (NumberFormatException e) {
                        log.debug("User id is not an int : " + e.getMessage());
                    }
                    if (assignedUser != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Assign workflow for question " + question.getId() + " to user " +
                                    assignedUser.getId() + " by user " + user.getId());
                        }
                        Workflow workflow =
                                addWorkflow(Workflow.STATE_ASSIGNED, question, user, workflows);

                        question.setWfAssignedUser(assignedUser.getId());
                        workflow.setAssignedUser(assignedUser);
                    }
                } else if (step == Workflow.STATE_RESOLVED) {
                    if (lastWf.getState() == Workflow.STATE_ASSIGNED ||
                            lastWf.getState() == Workflow.STATE_REJECTED) {
                        User assignedUser = lastWf.getUser();
                        if (assignedUser.equals(user)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Resolve workflow for question " + question.getId() +
                                        " by user " + user.getId());
                            }
                            addWorkflow(Workflow.STATE_RESOLVED, question, user, workflows);
                        }
                    }
                }
            } else if (lastWf.getState() == Workflow.STATE_RESOLVED) {
                if (step == Workflow.STATE_REJECTED) {
                    if (log.isDebugEnabled()) {
                        log.debug("Reject workflow for question " + question.getId() +
                                " by user " + user.getId());
                    }
                    addWorkflow(Workflow.STATE_REJECTED, question, user, workflows);
                } else if (step == Workflow.STATE_END) {
                    if (log.isDebugEnabled()) {
                        log.debug("End workflow for question " + question.getId() +
                                " by user " + user.getId());
                    }
                    addWorkflow(Workflow.STATE_END, question, user, workflows);
                }
            }
        }
        return workflows;
    }

    private Workflow addWorkflow(int state, Question question, User user, Set<Workflow> workflows) {
        Workflow workflow = new Workflow();
        workflow.setQuestion(question);
        workflow.setState(state);
        Date now = Calendar.getInstance().getTime();
        workflow.setStateDate(now);
        workflow.setUser(user);
        em.persist(workflow);
        workflows.add(workflow);
        question.setWfState(state);
        question.setWfDate(now);
        question.setWfAssignedUser(-1);
        question.setWorkflows(workflows);
        return workflow;
    }

    /**
     * Hydrate questions with transient data.
     */
    public void hydrateQuestions(Collection<Question> questions) {
        DateTime now = new DateTime();
        User user = userService.getCurrentUser();
        for (Question question : questions) {
            hydrateQuestion(now, user, question);
        }
    }

    private void hydrateQuestion(Question question) {
        DateTime now = new DateTime();
        User user = userService.getCurrentUser();
        hydrateQuestion(now, user, question);
    }

    private void hydrateQuestion(DateTime now, User user, Question question) {
        //Period calculation
        Date date = question.getUpdateDate();
        question.setPeriod(this.formatDateAsPeriod(now, date));

        //Current user vote
        if (user != null) {
            QuestionVote qv = question.getQuestionVotes().get(user.getId());
            if (qv == null) {
                question.setCurrentUserVote(0);
            } else {
                question.setCurrentUserVote(qv.getValue());
            }
        }
    }

    private String formatDateAsPeriod(DateTime now, Date dateToFormat) {
        String text;
        DateTime date = new DateTime(dateToFormat);
        Period period = new Period(date, now);
        if (period.getYears() > 0 || period.getMonths() > 0) {
            text = this.formatterYearFR.print(period);
        } else if (period.getWeeks() > 0 || period.getDays() > 0) {
            text = this.formatterWeekFR.print(period);
        } else if (period.getHours() > 0 || period.getMinutes() > 0) {
            text = this.formatterHourFR.print(period);
        } else {
            text = " moins d'une minute";
        }
        return text;
    }
}
