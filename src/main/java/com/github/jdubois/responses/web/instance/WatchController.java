package com.github.jdubois.responses.web.instance;

import com.github.jdubois.responses.service.WatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Julien Dubois
 */
@Controller
public class WatchController {

    @Autowired
    private WatchService watchService;

    @RequestMapping(value = "/{instancename}/watch", method = RequestMethod.GET)
    @ResponseBody
    public String watchQuestion(@PathVariable("instancename") String instanceName,
                                @RequestParam("questionId") long questionId,
                                @RequestParam("value") int value) {

        if (value == 1) {
            this.watchService.watchQuestion(questionId);
            return "watch";
        } else {
            this.watchService.unWatchQuestion(questionId);
            return "unwatch";
        }
    }
}
