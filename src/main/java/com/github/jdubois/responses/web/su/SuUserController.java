package com.github.jdubois.responses.web.su;

import com.github.jdubois.responses.service.UserManagementService;
import com.github.jdubois.responses.service.UserService;
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
public class SuUserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserManagementService userManagementService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ModelAndView homepage(ModelAndView mv,
                                 @RequestParam(required = false) String index) {

        this.putUserInModel(mv);
        int indexInt = 0;
        if (index != null) {
            try {
                indexInt = Integer.parseInt(index);
            } catch (NumberFormatException e) {
                indexInt = 0;
            }
        }
        mv.addObject("users", userService.findAllUsers(indexInt));
        mv.addObject("index", indexInt);
        mv.setViewName("su/users");
        return mv;
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ModelAndView enableUser(ModelAndView mv,
                                   @RequestParam String userId,
                                   @RequestParam String enable) {

        int intUserId = 0;
        try {
            intUserId = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            mv.addObject("error", userId + " is not a number");
            return homepage(mv, "0");
        }
        boolean boolEnable = true;
        if (enable.equals("0")) {
            boolEnable = false;
            mv.addObject("message", "User #" + userId + " is disabled.");
        } else {
            mv.addObject("message", "User #" + userId + " is enabled.");
        }
        userManagementService.enableUser(intUserId, boolEnable);
        return homepage(mv, "0");
    }
}
