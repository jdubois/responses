package com.github.jdubois.responses.web.instance;

import com.github.jdubois.responses.model.*;
import com.github.jdubois.responses.service.AnswerService;
import com.github.jdubois.responses.service.QuestionService;
import com.github.jdubois.responses.service.exception.HtmlValidationException;
import com.github.jdubois.responses.web.instance.dto.CommentDto;
import com.github.jdubois.responses.web.util.ResponsesHtmlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Controller
public class AnswerController extends BaseController {

    private static final String HTML_ANSWER_VALIDATION_ERROR = "Une alerte de s&eacute;curit&eacute; s'est produite. " +
            "Votre r&eacute;ponse contient des caract&egrave;res HTML interdits, car ils risquent de compromettre la " +
            "s&eacute;curit&eacute; du syst&egrave;me.<br/>Cette alerte a &eacute;t&eacute; " +
            "enregistr&eacute;e et a &eacute;t&eacute; remont&eacute;e aux administrateurs.";

    private final Log log = LogFactory.getLog(AnswerController.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @RequestMapping(value = "/{instancename}/answer/best", method = RequestMethod.GET)
    @ResponseBody
    public String bestAnswer(@PathVariable("instancename") String instanceName,
                             @RequestParam("answerId") long answerId) {

        try {
            return answerService.bestAnswer(instanceName, answerId);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Exception while selecting best answer " + answerId + " : " + e.getMessage());
            }
            return "error";
        }
    }

    @RequestMapping(value = "/{instancename}/answer/comment", method = RequestMethod.GET)
    public ModelAndView commentAnswer(@PathVariable("instancename") String instanceName,
                                      @RequestParam("answerId") long answerId,
                                      @RequestParam("value") String value) {

        try {
            String cleanText = ResponsesHtmlUtils.htmlEscape(value);
            answerService.commentAnswer(instanceName, answerId, cleanText);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Exception while commenting answer " + answerId + " : " + e.getMessage());
            }
        }
        return showCommentsAnswer(instanceName, answerId);
    }

    @RequestMapping(value = "/{instancename}/answer/showComments", method = RequestMethod.GET)
    public ModelAndView showCommentsAnswer(@PathVariable("instancename") String instanceName,
                                           @RequestParam("answerId") long answerId) {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("jsonView");
        Answer answer = answerService.getAnswer(instanceName, answerId);
        Set<AnswerComment> comments = answer.getAnswerComments();

        List<CommentDto> dtos = new ArrayList<CommentDto>();
        for (AnswerComment comment : comments) {
            User user = comment.getUser();
            CommentDto dto = new CommentDto();
            dto.setId(comment.getId());
            dto.setValue(comment.getValue());
            dto.setUserId(user.getId());
            dto.setUserUrl(user.getProfileUrl());
            dto.setUserFirstName(user.getFirstName());
            dto.setUserLastName(user.getLastName());
            dtos.add(dto);
        }
        mv.addObject(dtos);
        return mv;
    }

    @RequestMapping(value = "/{instancename}/answer/new", method = RequestMethod.GET)
    public String answerQuestion(@PathVariable("instancename") String instanceName,
                                 @RequestParam Long questionId,
                                 HttpServletRequest request) {

        Instance instance = putInstanceInRequest(instanceName, request);
        request.setAttribute("questionId", questionId);
        User user = putUserInRequest(request);
        if (user != null) {
            Question question = questionService.viewQuestion(Long.valueOf(questionId));
            if (!question.getInstance().equals(instance)) {
                if (log.isInfoEnabled()) {
                    log.info("[security] User " + user.getId() + " should not have tried to answer question " +
                            questionId + " on instance " + instanceName);
                }
                return "exception/exception";
            }
            request.setAttribute("question", question);
        }
        return "answer_question";
    }

    @RequestMapping(value = "/{instancename}/answer/new", method = RequestMethod.POST)
    public String answerQuestion(@PathVariable("instancename") String instanceName,
                                 @RequestParam("questionIdPost") String questionId,
                                 @RequestParam("answerText") String text,
                                 HttpServletRequest request) {

        Instance instance = putInstanceInRequest(instanceName, request);
        try {
            answerService.answerQuestion(instance, Long.valueOf(questionId), text);
            Question question = questionService.viewQuestion(Long.valueOf(questionId));
            request.setAttribute("question", question);
            return "answer_question_redirect";
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                User user = userService.getCurrentUser();
                if (!(e instanceof HtmlValidationException)) {
                    e.printStackTrace();
                }
                log.warn("Answer could not be submitted : \"" + e.getMessage() + "\", for user=" + user.getId());
            }
            request.setAttribute("error", "Une erreur s'est produite. La r&eacute;ponse n'a pas pu &ecirc;tre enregistr&eacute;e.");
        }
        request.setAttribute("answerText", text);
        return "answer_question";
    }

    @RequestMapping(value = "/{instancename}/answer/edit", method = RequestMethod.GET)
    public String editAnswer(@PathVariable("instancename") String instanceName,
                             @RequestParam Long answerId,
                             HttpServletRequest request) {

        Instance instance = putInstanceInRequest(instanceName, request);
        User user = putUserInRequest(request);
        Answer answer = answerService.getAnswer(instanceName, Long.valueOf(answerId));
        Question question = answer.getQuestion();
        if (!question.getInstance().equals(instance) || !answer.getUser().equals(user)) {
            if (log.isInfoEnabled()) {
                log.info("[security] User " + user.getId() + " should not have tried to edit answer " +
                        answerId + " on instance " + instanceName);
            }
            return "exception/exception";
        }
        request.setAttribute("question", question);
        request.setAttribute("answer", answer);
        request.setAttribute("answerText", answer.getText());
        return "edit_answer";
    }

    @RequestMapping(value = "/{instancename}/answer/edit", method = RequestMethod.POST)
    public String doEditAnswer(@PathVariable("instancename") String instanceName,
                               @RequestParam String questionUrl,
                               @RequestParam Long answerId,
                               @RequestParam String answerText,
                               HttpServletRequest request) {

        Instance instance = putInstanceInRequest(instanceName, request);
        User user = putUserInRequest(request);
        Answer answer = answerService.getAnswer(instanceName, Long.valueOf(answerId));
        Question question = answer.getQuestion();
        if (!question.getInstance().equals(instance) || !answer.getUser().equals(user)) {
            if (log.isInfoEnabled()) {
                log.info("[security] User " + user.getId() + " should not have tried to edit answer " +
                        answerId + " on instance " + instanceName);
            }
            return "exception/exception";
        }
        try {
            answerService.editAnswer(answerId, answerText);
            request.setAttribute("questionUrl", questionUrl);
            return "edit_answer_redirect";
        } catch (HtmlValidationException hve) {
            request.setAttribute("error", HTML_ANSWER_VALIDATION_ERROR);
            request.setAttribute("question", question);
            request.setAttribute("answer", answer);
            request.setAttribute("answerText", answerText);
            return "edit_answer";
        }
    }

    @RequestMapping(value = "/{instancename}/answer/preview", method = RequestMethod.POST)
    public String previewQuestion(@RequestParam String previewText,
                                  HttpServletRequest request) {

        request.setAttribute("text", previewText);
        return "preview_answer";
    }
}
