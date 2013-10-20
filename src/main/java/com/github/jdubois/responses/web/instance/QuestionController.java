package com.github.jdubois.responses.web.instance;

import com.github.jdubois.responses.model.*;
import com.github.jdubois.responses.service.*;
import com.github.jdubois.responses.service.exception.HtmlValidationException;
import com.github.jdubois.responses.service.exception.QOSException;
import com.github.jdubois.responses.web.instance.dto.CommentDto;
import com.github.jdubois.responses.web.util.ResponsesHtmlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Julien Dubois
 */
@Controller
public class QuestionController extends BaseController {

    public final static int ANSWERS_PAGINATION_SIZE = 5;
    private final Log log = LogFactory.getLog(QuestionController.class);

    private static final String HTML_QUESTION_VALIDATION_ERROR = "Une alerte de s&eacute;curit&eacute; s'est produite. " +
            "Votre question contient des caract&egrave;res HTML interdits, car ils risquent de compromettre la " +
            "s&eacute;curit&eacute; du syst&egrave;me.<br/>Cette alerte a &eacute;t&eacute; " +
            "enregistr&eacute;e et a &eacute;t&eacute; remont&eacute;e aux administrateurs.";

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private TagService tagService;

    @Autowired
    private AsyncExecutor asyncExecutor;

    @Autowired
    private ExpertService expertService;

    @Autowired
    private WatchService watchService;

    //Tag url splitting
    private Pattern tagSplitter = Pattern.compile("\\+");

    @RequestMapping(value = "/{instancename}/q/{questionId}/{questionUrl}", method = RequestMethod.GET)
    public String viewQuestion(@PathVariable("instancename") String instanceName,
                               @PathVariable("questionId") String questionId,
                               @RequestParam(required = false) Integer answerIndex,
                               @RequestParam(required = false) String newAnswer,
                               @RequestParam(required = false) String edit,
                               HttpServletRequest request) {

        Instance instance = this.putInstanceInRequest(instanceName, request);
        this.putSearchQueryInRequest(request);

        //User information
        User user = userService.getCurrentUser();
        if (user != null) {
            request.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
            request.setAttribute("user", user);
        }

        //Question
        Question question = questionService.viewQuestion(Long.valueOf(questionId));
        if (!question.getInstance().equals(instance)) {
            if (log.isInfoEnabled()) {
                log.info("[security] User " + user.getId() + " should not have tried to view question " +
                        questionId + " on instance " + instanceName);
            }
            return "exception/exception";
        }
        request.setAttribute("question", question);

        //Is the user the author of the question?
        if (user != null && user.equals(question.getUser())) {
            request.setAttribute("isAuthor", true);
        }

        //Watch
        boolean watched = false;
        if (user != null) {
            for (Watch watch : question.getWatchs()) {
                if (user.equals(watch.getUser())) {
                    watched = true;
                }
            }
        }
        request.setAttribute("watched", watched);

        //Support
        if (instance.getType() == Instance.TYPE_PRIVATE) {
            request.setAttribute("wfState", question.getWfState());
        }

        //New answer posted
        if (newAnswer != null) {
            request.setAttribute("message", "Votre r&eacute;ponse a bien &eacute;t&eacute; enregistr&eacute;e. Merci de votre participation!");
        }
        //Question or answer edited
        if (edit != null) {
            if ("question".equals(edit)) {
                request.setAttribute("message", "Votre question a bien &eacute;t&eacute; enregistr&eacute;e. Merci de votre participation!");
            } else if ("answer".equals(edit)) {
                request.setAttribute("message", "Votre r&eacute;ponse a bien &eacute;t&eacute; enregistr&eacute;e. Merci de votre participation!");
            }
        }

        //Pagination
        if (answerIndex == null) {
            answerIndex = 0;
        }
        request.setAttribute("answerIndex", answerIndex);
        Set<Answer> answers = question.getAnswers();
        List<Answer> pageAnswers = new ArrayList<Answer>();
        Iterator<Answer> it = answers.iterator();
        int index = 0;
        int indexMax = answerIndex * ANSWERS_PAGINATION_SIZE + ANSWERS_PAGINATION_SIZE;
        int indexMin = answerIndex * ANSWERS_PAGINATION_SIZE;
        while (it.hasNext() && index < indexMax) {
            Answer answer = it.next();
            if (index >= indexMin) {
                pageAnswers.add(answer);
            }
            index++;
        }
        request.setAttribute("answers", pageAnswers);

        Integer answerSize = question.getAnswersSize();
        Integer pagesNumber = (answerSize - 1) / ANSWERS_PAGINATION_SIZE;
        if (pagesNumber >= 50) {
            pagesNumber = 50;
        }
        request.setAttribute("answerSize", answerSize);
        request.setAttribute("pagesNumber", pagesNumber);

        String paginationUrl = request.getContextPath() + "/i/" + instanceName
                + "/q/" + questionId + "/" + question.getTitleAsUrl() + "?";

        request.setAttribute("paginationUrl", paginationUrl);

        return "question";
    }

    @RequestMapping(value = "/{instancename}/question/preview", method = RequestMethod.POST)
    public String previewQuestion(@RequestParam String previewTitle,
                                  @RequestParam String previewText,
                                  HttpServletRequest request) {

        request.setAttribute("title", previewTitle);
        request.setAttribute("text", previewText);
        return "preview_question";
    }

    @RequestMapping(value = "/{instancename}/question/{questionId}/tag/add/{tagText}", method = RequestMethod.GET)
    @ResponseBody
    public String addQuestionTag(@PathVariable("instancename") String instanceName,
                                 @PathVariable("questionId") long questionId,
                                 @PathVariable("tagText") String tagText,
                                 HttpServletRequest request) {

        try {
            String tagsArray[] = questionService.addQuestionTag(questionId, tagText);
            Instance instance = instanceService.getInstanceByName(instanceName);
            tagService.cleanCacheTagSummaryInfo(instance.getId(), tagsArray);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            return "Une erreur s'est produite";
        }
        return "";
    }

    @RequestMapping(value = "/{instancename}/question/{questionId}/tag/delete/{tagText}", method = RequestMethod.GET)
    @ResponseBody
    public String deleteQuestionTag(@PathVariable("instancename") String instanceName,
                                    @PathVariable("questionId") long questionId,
                                    @PathVariable("tagText") String tagText,
                                    HttpServletRequest request) {

        try {
            String tagsArray[] = questionService.deleteQuestionTag(questionId, tagText);
            Instance instance = instanceService.getInstanceByName(instanceName);
            tagService.cleanCacheTagSummaryInfo(instance.getId(), tagsArray);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            return "Une erreur s'est produite";
        }
        return "";
    }

    @RequestMapping(value = "/{instancename}/question/count", method = RequestMethod.GET)
    @ResponseBody
    public String countQuestionView(@PathVariable("instancename") String instanceName,
                                    @RequestParam("questionId") Long questionId,
                                    HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");
        if (userAgent.contains("Windows") || userAgent.contains("X11") || userAgent.contains("AppleWebKit")) {
            this.asyncExecutor.countQuestionView(questionId);
        }
        return "ok";
    }

    @RequestMapping(value = "/{instancename}/question/comment", method = RequestMethod.GET)
    public ModelAndView commentQuestion(@PathVariable("instancename") String instanceName,
                                        @RequestParam("questionId") long questionId,
                                        @RequestParam("value") String value) {

        try {
            String cleanText = ResponsesHtmlUtils.htmlEscape(value);
            questionService.commentQuestion(instanceName, questionId, cleanText);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Exception while commenting question " + questionId + " : " + e.getMessage());
            }
        }
        return this.showCommentsQuestion(instanceName, questionId);
    }

    @RequestMapping(value = "/{instancename}/question/showComments", method = RequestMethod.GET)
    public ModelAndView showCommentsQuestion(@PathVariable("instancename") String instanceName,
                                             @RequestParam("questionId") long questionId) {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("jsonView");
        Question question = questionService.viewQuestion(Long.valueOf(questionId));
        Set<QuestionComment> comments = question.getQuestionComments();

        List<CommentDto> dtos = new ArrayList<CommentDto>();
        for (QuestionComment comment : comments) {
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

    @RequestMapping(value = "/{instancename}/question/ask", method = RequestMethod.GET)
    public String askQuestion(@PathVariable("instancename") String instanceName,
                              HttpServletRequest request) {

        putUserInRequest(request);
        putInstanceInRequest(instanceName, request);
        return "ask_question";
    }

    @RequestMapping(value = "/{instancename}/tagged/{taglist}/question/ask", method = RequestMethod.GET)
    public String askQuestionTagged(@PathVariable("instancename") String instanceName,
                                    @PathVariable("taglist") String taglist,
                                    HttpServletRequest request) {

        request.setAttribute("selectedTagsList", taglist);
        putUserInRequest(request);
        putInstanceInRequest(instanceName, request);
        return "ask_question";
    }

    @RequestMapping(value = "/{instancename}/question/ask", method = RequestMethod.POST)
    public String askQuestionSubmit(@PathVariable("instancename") String instanceName,
                                    @RequestParam("questionTitle") String questionTitle,
                                    @RequestParam(required = false) String questionText,
                                    @RequestParam(required = false) String askTags,
                                    HttpServletRequest request) {

        Instance instance = putInstanceInRequest(instanceName, request);
        try {
            String[] tagsArray;
            if (("".equals(askTags))) {
                tagsArray = new String[0];
            } else {
                tagsArray = tagSplitter.split(askTags);
            }
            if (tagsArray.length > 5) {
                request.setAttribute("error", "Vous ne pouvez pas utiliser plus de 5 &eacute;tiquettes sur une question.");
            } else {
                String escapeHtmlquestionTitle = ResponsesHtmlUtils.htmlEscape(questionTitle);
                long questionId = questionService.askQuestion(instance.getId(), escapeHtmlquestionTitle, questionText, tagsArray);
                instanceService.incrementInstanceSize(instance.getId());
                tagService.cleanCacheTagSummaryInfo(instance.getId(), tagsArray);
                watchService.watchQuestion(questionId);
                request.setAttribute("message", "La question a &eacute;t&eacute; pos&eacute;e.");
                return "ask_question_redirect";
            }
        } catch (HtmlValidationException hve) {
            request.setAttribute("error", HTML_QUESTION_VALIDATION_ERROR);
        } catch (QOSException qose) {
            if (log.isWarnEnabled()) {
                User user = userService.getCurrentUser();
                log.warn("Quality Of Service error : \"" + qose.getMessage() + "\", for user=" + user.getId());
            }
            request.setAttribute("error", "Une alerte de s&eacute;curit&eacute; s'est produite. " +
                    "Afin d'&eacute;viter que certains utilisateurs n'innondent le syst&egrave;me de questions, vous ne " +
                    "pouvez pas poser plus d'une question toutes les 2 minutes.<br/>Cette alerte a &eacute;t&eacute; " +
                    "enregistr&eacute;e et a &eacute;t&eacute; remont&eacute;e aux administrateurs.");
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                User user = userService.getCurrentUser();
                log.warn("Question could not be asked : \"" + e.getMessage() + "\", for user=" + user.getId());
                e.printStackTrace();
            }
            request.setAttribute("error", "Une erreur s'est produite. La question n'a pas pu &ecirc;tre pos&eacute;e.");
        }
        request.setAttribute("questionTitle", questionTitle);
        request.setAttribute("questionText", questionText);
        if (askTags.endsWith("+")) {
            askTags = askTags.substring(0, askTags.length() - 1);
        }
        request.setAttribute("selectedTagsList", askTags);
        return "ask_question";
    }

    @RequestMapping(value = "/{instancename}/tagged/{taglist}/question/ask", method = RequestMethod.POST)
    public String askQuestionTaggedSubmit(@PathVariable("instancename") String instanceName,
                                          @PathVariable("taglist") String taglist,
                                          @RequestParam("questionTitle") String questionTitle,
                                          @RequestParam(required = false) String questionText,
                                          @RequestParam(required = false) String askTags,
                                          HttpServletRequest request) {

        request.setAttribute("taglist", taglist);
        return askQuestionSubmit(instanceName, questionTitle, questionText, askTags, request);
    }

    @RequestMapping(value = "/{instancename}/question/edit", method = RequestMethod.GET)
    public String editQuestion(@PathVariable("instancename") String instanceName,
                               @RequestParam Long questionId,
                               HttpServletRequest request) {

        User user = putUserInRequest(request);
        Instance instance = putInstanceInRequest(instanceName, request);
        Question question = questionService.getQuestion(Long.valueOf(questionId));
        if (!question.getInstance().equals(instance) || !question.getUser().equals(user)) {
            if (log.isInfoEnabled()) {
                log.info("[security] User " + user.getId() + " should not have tried to edit question " +
                        questionId + " on instance " + instanceName);
            }
            return "exception/exception";
        }
        request.setAttribute("question", question);
        request.setAttribute("questionTitle", question.getTitle());
        request.setAttribute("questionText", question.getText());
        return "edit_question";
    }

    @RequestMapping(value = "/{instancename}/question/edit", method = RequestMethod.POST)
    public String doEditQuestion(@PathVariable("instancename") String instanceName,
                                 @RequestParam Long questionId,
                                 @RequestParam String questionUrl,
                                 @RequestParam String questionTitle,
                                 @RequestParam String questionText,
                                 HttpServletRequest request) {

        Instance instance = putInstanceInRequest(instanceName, request);
        User user = userService.getCurrentUser();
        Question question = questionService.getQuestion(Long.valueOf(questionId));
        if (!question.getInstance().equals(instance) || !question.getUser().equals(user)) {
            if (log.isInfoEnabled()) {
                log.info("[security] User " + user.getId() + " should not have tried to edit question " +
                        questionId + " on instance " + instanceName);
            }
            return "exception/exception";
        }
        try {
            String escapeHtmlquestionTitle = ResponsesHtmlUtils.htmlEscape(questionTitle);
            questionService.editQuestion(questionId, escapeHtmlquestionTitle, questionText);
            request.setAttribute("questionUrl", questionUrl);
            return "edit_question_redirect";
        } catch (HtmlValidationException hve) {
            request.setAttribute("error", HTML_QUESTION_VALIDATION_ERROR);
            request.setAttribute("question", question);
            request.setAttribute("questionTitle", questionTitle);
            request.setAttribute("questionText", questionText);
            return "edit_question";
        }
    }

    @RequestMapping(value = "/{instancename}/question/support/{questionId}", method = RequestMethod.GET)
    public String viewSupport(@PathVariable("instancename") String instanceName,
                              @PathVariable("questionId") Long questionId,
                              HttpServletRequest request) {

        Set<Workflow> workflows = questionService.getWorkflows(instanceName, questionId);
        return commonWorkflow(instanceName, questionId, request, workflows);
    }

    @RequestMapping(value = "/{instancename}/question/support/{questionId}", method = RequestMethod.POST)
    public String supportWorkflow(@PathVariable("instancename") String instanceName,
                                  @PathVariable("questionId") Long questionId,
                                  @RequestParam int step,
                                  @RequestParam String userId,
                                  HttpServletRequest request) {

        Set<Workflow> workflows = questionService.updateWorkflow(instanceName, questionId, step, userId);

        return commonWorkflow(instanceName, questionId, request, workflows);
    }

    private String commonWorkflow(String instanceName, Long questionId, HttpServletRequest request, Set<Workflow> workflows) {
        request.setAttribute("instanceName", instanceName);
        request.setAttribute("workflows", workflows);
        if (workflows.iterator().hasNext()) {
            Workflow workflow = workflows.iterator().next();
            request.setAttribute("workflow", workflow);
            if (workflow.getState() == Workflow.STATE_VALIDATED ||
                    workflow.getState() == Workflow.STATE_ASSIGNED ||
                    workflow.getState() == Workflow.STATE_REJECTED) {

                List<User> experts = expertService.getExpertsForInstance(instanceName);
                request.setAttribute("experts", experts);
            }
            if (workflow.getState() == Workflow.STATE_ASSIGNED ||
                    workflow.getState() == Workflow.STATE_REJECTED) {

                request.setAttribute("currentUser", userService.getCurrentUser());
            }
        }
        request.setAttribute("questionId", questionId);

        return "support";
    }

    @RequestMapping(value = "/{instancename}/question/answers/{questionId}", method = RequestMethod.GET)
    public String getAnswers(@PathVariable("instancename") String instanceName,
                             @PathVariable("questionId") Long questionId,
                             HttpServletRequest request) {

        int instanceId = instanceService.findInstanceIdByName(instanceName);
        request.setAttribute("instanceId", instanceId);
        request.setAttribute("instanceName", instanceName);

        Question question = questionService.viewQuestion(questionId);
        request.setAttribute("question", question);

        return "answers";
    }


    @RequestMapping(value = "/{instancename}/question/latest", method = RequestMethod.GET)
    public String latestQuestions(@PathVariable("instancename") String instanceName, HttpServletRequest request) {
        int instanceId = genericDisplayQuestion(instanceName, request);

        //Latest questions
        Collection<Question> questions = this.questionService.showLatestQuestions(instanceId, true, false, 0);
        request.setAttribute("questions", questions);

        return "questions";
    }

    private int genericDisplayQuestion(String instanceName, HttpServletRequest request) {
        Instance instance = putInstanceInRequest(instanceName, request);

        request.setAttribute("questionIndex", 0);
        return instance.getId();
    }
}
