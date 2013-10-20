package com.github.jdubois.responses.web.support;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.EmailService;
import com.github.jdubois.responses.service.ExpertService;
import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Controller
public class SupportController {

    private final Log log = LogFactory.getLog(SupportController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private ExpertService expertService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView homepage(@RequestParam(required = false) Integer selectedInstance,
                                 @RequestParam(required = false) Integer selectedState,
                                 @RequestParam(required = false) Integer selectedAssignedUser,
                                 @RequestParam(required = false) Integer selectedIndex,
                                 HttpServletRequest request) {

        ModelAndView mv = new ModelAndView();
        User user = userService.getCurrentUser();

        Set<Instance> instances = instanceService.getInstancesForUser(user);
        if (instances.size() > 0) {
            mv.addObject("instances", instances);
            mv.addObject("experts", expertService.getExpertsForInstances(instances));

            Collection<Instance> selectedInstances;
            if (selectedInstance != null && selectedInstance != -1) {
                Instance instance = instanceService.getInstance(selectedInstance);
                selectedInstances = new ArrayList<Instance>();
                selectedInstances.add(instance);
            } else {
                selectedInstance = -1;
                selectedInstances = instances;
            }
            mv.addObject("selectedInstance", selectedInstance);
            if (selectedState == null) {
                selectedState = -1;
            }
            mv.addObject("selectedState", selectedState);
            if (selectedAssignedUser == null) {
                selectedAssignedUser = -1;
            }
            mv.addObject("selectedAssignedUser", selectedAssignedUser);
            if (selectedIndex == null) {
                selectedIndex = 0;
            }
            mv.addObject("selectedIndex", selectedIndex);

            List<Question> questions = expertService.getQuestions(selectedInstances, selectedState,
                    selectedAssignedUser, selectedIndex);

            mv.addObject("questions", questions);
        }
        mv.setViewName("support/support_center");
        return mv;
    }

}