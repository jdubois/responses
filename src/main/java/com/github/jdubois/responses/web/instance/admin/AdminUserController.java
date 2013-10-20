package com.github.jdubois.responses.web.instance.admin;

import com.github.jdubois.responses.model.Company;
import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Role;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.CompanyService;
import com.github.jdubois.responses.service.UserManagementService;
import com.github.jdubois.responses.web.instance.dto.UserDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author Julien Dubois
 */
@Controller
public class AdminUserController extends AdminBaseController {

    private final Log log = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserManagementService userManagementService;

    protected ModelAndView genericPage(String instanceName) {
        ModelAndView mv = new ModelAndView();
        securityCheck(instanceName);
        putUserInModel(mv);
        Instance instance = putInstanceInModel(instanceName, mv);
        mv.addObject("company", companyService.getCompanyByInstanceID(instance.getId()));
        mv.setViewName("admin/instance_edit");
        return mv;
    }

    @RequestMapping(value = "/{instancename}/admin/addUser", method = RequestMethod.GET)
    public ModelAndView addUser(@PathVariable("instancename") String instanceName) {

        ModelAndView mv = genericPage(instanceName);
        mv.setViewName("admin/user_add");
        return mv;
    }

    @RequestMapping(value = "/{instancename}/admin/addUser", method = RequestMethod.POST)
    public ModelAndView doAddUser(@PathVariable("instancename") String instanceName,
                                  @RequestParam String email,
                                  @RequestParam String firstName,
                                  @RequestParam String lastName,
                                  @RequestParam(required = false) boolean isModerator,
                                  @RequestParam(required = false) boolean isSupport,
                                  @RequestParam(required = false) boolean isAdmin) {

        User user = this.userManagementService.createBusinessUser(email, firstName, lastName, isModerator, isSupport, isAdmin, instanceName);
        ModelAndView mv = this.addUser(instanceName);
        if (user == null) {
            mv.addObject("email", email);
            mv.addObject("firstName", firstName);
            mv.addObject("lastName", lastName);
            mv.addObject("error", "L'utilisateur n'a pas pu &ecirc;tre cr&eacute;&eacute; car l'adresse e-mail " +
                    "<b>" + email + "</b> est d&eacute;j&agrave; utilis&eacute;e par un autre utilisateur.");

        } else {
            mv.addObject("message", "L'utilisateur <b>" + firstName + " " + lastName + "</b> a &eacute;t&eacute; cr&eacute;&eacute; " +
                    "avec succ&egrave;s.");

        }
        return mv;
    }

    @RequestMapping(value = "/{instancename}/admin/editUser", method = RequestMethod.GET)
    public ModelAndView editUser(@PathVariable("instancename") String instanceName) {
        ModelAndView mv = new ModelAndView();
        securityCheck(instanceName);
        putUserInModel(mv);
        Instance instance = putInstanceInModel(instanceName, mv);
        Company company = companyService.getCompanyByInstanceID(instance.getId());
        List<User> users = userService.findCompanyUsers(company);
        mv.addObject("users", users);
        mv.setViewName("admin/user_edit");
        return mv;
    }

    @RequestMapping(value = "/{instancename}/admin/editUser", method = RequestMethod.POST)
    public ModelAndView doEditUser(@PathVariable("instancename") String instanceName,
                                   @RequestParam int userId,
                                   @RequestParam String email,
                                   @RequestParam String firstName,
                                   @RequestParam String lastName,
                                   @RequestParam(required = false) boolean isModerator,
                                   @RequestParam(required = false) boolean isSupport,
                                   @RequestParam(required = false) boolean isAdmin,
                                   @RequestParam boolean enabled) {

        securityCheck(instanceName);
        boolean success = false;
        try {
            success = userManagementService.updateUser(userId, email, firstName, lastName, isModerator, isSupport, isAdmin, enabled);
        } catch (Exception e) {
            ModelAndView mv = new ModelAndView();
            mv.setViewName("jsonView");
            mv.addObject("error", "Une erreur technique s'est produite.");
            return mv;
        }
        if (success) {
            ModelAndView mv = getUser(instanceName, userId);
            mv.addObject("message", "Mise &agrave; jour r&eacute;ussie.");
            return mv;
        } else {
            ModelAndView mv = new ModelAndView();
            mv.setViewName("jsonView");
            mv.addObject("error", "La mise &agrave; jour a &eacute;chou&eacute;e, cette adresse e-mail est" +
                    " est d&eacute;j&agrave; utilis&eacute;e.");

            return mv;
        }
    }

    @RequestMapping(value = "/{instancename}/admin/user/{userId}", method = RequestMethod.GET)
    public ModelAndView getUser(@PathVariable("instancename") String instanceName,
                                @PathVariable("userId") int userId) {

        ModelAndView mv = new ModelAndView();
        securityCheck(instanceName);
        User user = userService.findUserAndRolesById(userId);
        UserDto dto = new UserDto();
        if (user != null) {
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEnabled(user.isEnabled());
            for (Role role : user.getRoles()) {
                dto.addRole(role.getRole());
            }
        }
        mv.addObject("userDto", dto);
        mv.setViewName("jsonView");
        return mv;
    }
}
