package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.model.Watch;
import com.github.jdubois.responses.service.EmailService;
import com.github.jdubois.responses.service.UserService;
import com.github.jdubois.responses.service.WatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Repository
public class WatchServiceImpl implements WatchService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public void alertUsers(Question question, String message) {
        Set<Watch> watchs = question.getWatchs();
        Instance instance = question.getInstance();
        String subject = "[Responses] Mise à jour de la question \"" + question.getTitle() + "\"";
        String url = ConfigurationServiceImpl.siteUrl + "/i/" + question.getInstance().getName() + "/q/" + question.getId() + "/" + question.getTitleAsUrl();
        String text = "<p>La question <b>\"" + question.getTitle() + "\"</b> vient d'être mise à jour.<br/></p><p>" +
                message + "</p><p><br/>" +
                "Voir cette question : <a href=\"" + url + "\">" + url + "</a>" +
                "<br/><br/> Ce message vous a été envoyé par Responses, vous pouvez modifiez vos alertes à tout moment dans la " +
                "rubrique \"Mes alertes\", dans votre menu utilisateur.</p>";

        User currentUser = userService.getCurrentUser();

        for (Watch watch : watchs) {
            User user = watch.getUser();
            if (!user.equals(currentUser)) { //Alert everyone except the current user, who already knows about this.
                emailService.asyncSendEmail(user.getEmail(), subject, text);
            }
        }
    }

    @Transactional
    public void watchQuestion(User user, Question question) {
        Query query = em.createNamedQuery("Watch.getWatch");
        query.setParameter("userId", user.getId());
        query.setParameter("questionId", question.getId());
        if (query.getResultList().size() == 0) {
            Watch watch = new Watch();
            watch.setUser(user);
            watch.setQuestion(question);
            em.persist(watch);
            question.getWatchs().add(watch);
        }
    }

    @Transactional
    public void watchQuestion(long questionId) {
        User user = this.userService.getCurrentUser();
        Question question = em.find(Question.class, questionId);
        this.watchQuestion(user, question);
    }

    @Transactional
    public void unWatchQuestion(long questionId) {
        User user = this.userService.getCurrentUser();
        Question question = em.find(Question.class, questionId);
        Query query = em.createNamedQuery("Watch.getWatch");
        query.setParameter("userId", user.getId());
        query.setParameter("questionId", question.getId());
        List<Watch> watchs = query.getResultList();
        for (Watch watch : watchs) {
            question.getWatchs().remove(watch);
            em.remove(watch);
        }
    }
}
