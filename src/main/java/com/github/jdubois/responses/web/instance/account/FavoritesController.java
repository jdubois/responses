package com.github.jdubois.responses.web.instance.account;

import com.github.jdubois.responses.web.instance.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Julien Dubois
 */
@Controller
public class FavoritesController extends BaseController {

    @RequestMapping("/account/favorites")
    public ModelAndView homepage() {
        ModelAndView mv = new ModelAndView();
        this.putUserInModel(mv);
        return mv;
    }
}