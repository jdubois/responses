package com.github.jdubois.responses.web.instance;

import com.github.jdubois.responses.service.AnswerService;
import com.github.jdubois.responses.service.QuestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Julien Dubois
 */
@Controller
public class VoteController extends BaseController {

    private final Log log = LogFactory.getLog(VoteController.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @RequestMapping(value = "/{instancename}/vote/question/{questionId}/{vote}", method = RequestMethod.GET)
    @ResponseBody
    public String voteForQuestion(@PathVariable("instancename") String instanceName, @PathVariable("questionId") String questionId,
                                  @PathVariable("vote") String vote) {

        try {
            int questionVotes = this.questionService.voteForQuestion(instanceName, Long.valueOf(questionId), Integer.valueOf(vote));
            return String.valueOf(questionVotes);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Exception while voting for question " + questionId + " : " + e.getMessage());
            }
            return "error";
        }
    }

    @RequestMapping(value = "/{instancename}/vote/answer/{answerId}/{vote}", method = RequestMethod.GET)
    @ResponseBody
    public String voteForAnswer(@PathVariable("instancename") String instanceName, @PathVariable("answerId") String answerId,
                                @PathVariable("vote") String vote) {

        try {
            int answerVotes = this.answerService.voteForAnswer(instanceName, Long.valueOf(answerId), Integer.valueOf(vote));
            return String.valueOf(answerVotes);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                e.printStackTrace();
                log.warn("Exception while voting for answer " + answerId + " : " + e.getMessage());
            }
            return "error";
        }
    }
}
