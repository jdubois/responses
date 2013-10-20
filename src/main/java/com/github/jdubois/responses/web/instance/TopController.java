package com.github.jdubois.responses.web.instance;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.service.QuestionService;
import com.github.jdubois.responses.service.impl.QuestionServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * @author Julien Dubois
 */
@Controller
public class TopController extends BaseController {

    private final Log log = LogFactory.getLog(TopController.class);

    @Autowired
    private QuestionService questionService;

    //Tag url splitting
    private Pattern tagSplitter = Pattern.compile("\\+");

    @RequestMapping(value = "/{instancename}/top-questions", method = RequestMethod.GET)
    public String topQuestions(@PathVariable("instancename") String instanceName,
                               @RequestParam(required = false) Integer type,
                               @RequestParam(required = false) Integer when,
                               HttpServletRequest request) {

        return topQuestions(instanceName, type, when, null, request);
    }

    @RequestMapping(value = "/{instancename}/top-questions/{taglist}", method = RequestMethod.GET)
    public String topQuestions(@PathVariable("instancename") String instanceName,
                               @RequestParam(required = false) Integer type,
                               @RequestParam(required = false) Integer when,
                               @PathVariable("taglist") String tagList,
                               HttpServletRequest request) {

        if (type == null) {
            type = QuestionServiceImpl.TYPE_VIEWS;
        }
        if (when == null) {
            when = QuestionServiceImpl.WHEN_MONTH;
        }
        request.setAttribute("type", type);
        request.setAttribute("when", when);
        Instance instance = putInstanceInRequest(instanceName, request);
        putUserInRequest(request);
        Collection<Question> questions;
        if (tagList == null) {
            questions = questionService.showTopQuestions(instance.getId(), type, when);
        } else {
            request.setAttribute("selectedTagsList", tagList);
            String[] selTagsArray = tagSplitter.split(tagList);
            Arrays.sort(selTagsArray);
            questions = questionService.showTopQuestions(instance.getId(), type, when, selTagsArray);
        }
        request.setAttribute("questions", questions);

        return "top-questions";
    }

}
