package com.github.jdubois.responses.service;

import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.model.User;

/**
 * @author Julien Dubois
 */
public interface WatchService {

    public static final String NEW_ANSWER = "Une nouvelle r&eacute;ponse a &eacute;t&eacute; publi&eacute;e: <br/>";
    public static final String BEST_ANSWER = "La meilleure r&eacute;ponse a &eacute;t&eacute; selectionn&eacute;e: <br/><br/>";
    public static final String NEW_ANSWER_COMMENT = "Un nouveau commentaire a &eacute;t&eacute; ajout&eacute; &agrave; une r&eacute;ponse: <br/><br/>";
    public static final String NEW_QUESTION_COMMENT = "Un nouveau commentaire a &eacute;t&eacute; ajout&eacute; &agrave; la question: <br/><br/>";
    public static final String EDIT_QUESTION = "La question vient d'&ecirc;tre &eacute;dit&eacute;e: <br/><br/>";
    public static final String EDIT_ANSWER = "Une r&eacute;ponse vient d'&ecirc;tre &eacute;dit&eacute;e: <br/><br/>";

    void alertUsers(Question question, String message);

    void watchQuestion(User user, Question question);

    void watchQuestion(long questionId);

    void unWatchQuestion(long questionId);
}
