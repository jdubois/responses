package com.github.jdubois.responses.web.instance.account;

import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.QuestionService;
import com.github.jdubois.responses.web.instance.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * @author Julien Dubois
 */
@Controller
public class WatchsController extends BaseController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping(value = "/{instancename}/account/watch", method = RequestMethod.GET)
    public ModelAndView homepage(@PathVariable("instancename") String instanceName,
                                 @RequestParam(required = false) Integer questionIndex,
                                 HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        User user = this.putUserInModel(mv);
        this.putInstanceInModel(instanceName, mv);

        int size = (int) this.questionService.countWatchedQuestions(user);
        Collection<Question> questions;
        if (questionIndex == null) {
            questions = this.questionService.listWatchedQuestions(user, 0);
            request.setAttribute("questionIndex", 0);
        } else {
            questions = this.questionService.listWatchedQuestions(user, questionIndex);
            request.setAttribute("questionIndex", questionIndex);
        }
        paginationCalculation(request, size);
        String paginationUrl = request.getContextPath() + "/i/" + instanceName + "/account/watch?";
        request.setAttribute("paginationUrl", paginationUrl);
        mv.addObject("questions", questions);
        mv.setViewName("account/watch");
        return mv;
    }
}