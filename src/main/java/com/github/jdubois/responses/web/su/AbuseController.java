package com.github.jdubois.responses.web.su;

import com.github.jdubois.responses.service.ContactService;
import com.github.jdubois.responses.service.DeletionService;
import com.github.jdubois.responses.web.instance.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Julien Dubois
 */
@Controller
public class AbuseController extends BaseController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private DeletionService deletionService;

    @RequestMapping(value = "/abuse", method = RequestMethod.GET)
    public ModelAndView homepage(ModelAndView mv) {

        this.putUserInModel(mv);
        mv.addObject("abuses", contactService.getLatestAbuses());
        mv.setViewName("su/abuse");
        return mv;
    }

    @RequestMapping(value = "/abuse", method = RequestMethod.POST)
    public ModelAndView suppress(@RequestParam long questionId,
                                 @RequestParam long answerId,
                                 @RequestParam long questionCommentId,
                                 @RequestParam long answerCommentId) {

        ModelAndView mv = new ModelAndView();
        if (questionId != 0) {
            deletionService.deleteQuestion(questionId);
            mv.addObject("message", "Question #" + questionId + " deleted!");
        } else if (answerId != 0) {
            deletionService.deleteAnswer(answerId);
            mv.addObject("message", "Answer #" + answerId + " deleted!");
        } else if (questionCommentId != 0) {
            deletionService.deleteQuestionComment(questionCommentId);
            mv.addObject("message", "Question comment #" + questionCommentId + " deleted!");
        } else if (answerCommentId != 0) {
            deletionService.deleteAnswerComment(answerCommentId);
            mv.addObject("message", "Answer comment #" + answerCommentId + " deleted!");
        } else {
            mv.addObject("error", "No entity to delete!");
        }
        return homepage(mv);
    }
}
