package com.github.jdubois.responses.web.instance.admin;

import com.github.jdubois.responses.model.Company;
import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.CompanyService;
import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.service.UserService;
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
public class AdministrationController extends AdminBaseController {

    private final Log log = LogFactory.getLog(AdministrationController.class);

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @RequestMapping(value = "/{instancename}/admin", method = RequestMethod.GET)
    public ModelAndView homepage(@PathVariable("instancename") String instanceName) {

        ModelAndView mv = new ModelAndView();
        securityCheck(instanceName);
        putUserInModel(mv);
        putInstanceInModel(instanceName, mv);
        mv.setViewName("admin/instance_edit");
        return mv;
    }

    @RequestMapping(value = "/{instancename}/admin", method = RequestMethod.POST)
    public ModelAndView updateInstance(@PathVariable("instancename") String instanceName,
                                       @RequestParam String instanceLongName,
                                       @RequestParam String instanceDescription) {

        ModelAndView mv = this.homepage(instanceName);
        instanceService.editInstance(instanceName, instanceLongName, instanceDescription);
        mv.addObject("message", "Instance mise &agrave; jour.");
        return mv;
    }

    @RequestMapping(value = "/{instancename}/admin/manageInstance", method = RequestMethod.GET)
    public ModelAndView manageInstance(@PathVariable("instancename") String instanceName) {

        ModelAndView mv = new ModelAndView();
        securityCheck(instanceName);
        putUserInModel(mv);
        Instance instance = putInstanceInModel(instanceName, mv);
        Company company = companyService.getCompanyByInstanceID(instance.getId());
        commonManageInstance(mv, instance, company);
        return mv;
    }

    private void commonManageInstance(ModelAndView mv, Instance instance, Company company) {
        List<User> availableUsers = userService.findCompanyUsers(company);
        List<User> selectedUsers = userService.findUsersByInstanceId(instance.getId());
        availableUsers.removeAll(selectedUsers);
        mv.addObject("availableUsers", availableUsers);
        mv.addObject("selectedUsers", selectedUsers);
        mv.setViewName("admin/instance_manage");
    }

    @RequestMapping(value = "/{instancename}/admin/manageInstance", method = RequestMethod.POST)
    public ModelAndView manageInstance(@PathVariable("instancename") String instanceName,
                                       @RequestParam String action,
                                       @RequestParam String userIds) {

        ModelAndView mv = new ModelAndView();
        securityCheck(instanceName);
        putUserInModel(mv);
        Instance instance = putInstanceInModel(instanceName, mv);
        Company company = companyService.getCompanyByInstanceID(instance.getId());
        String[] ids = userIds.split(" ");
        int[] intIds = new int[ids.length];
        for (int i = 0; i < ids.length; i++) {
            intIds[i] = Integer.parseInt(ids[i]);
        }
        if ("add".equals(action)) {
            instanceService.addUsersToInstance(intIds, instance.getId());
            mv.addObject("message", "Les acc&egrave;s ont &eacute;t&eacute; ajout&eacute;s.");
        } else if ("remove".equals(action)) {
            instanceService.removeUsersFromInstance(intIds, instance.getId());
            mv.addObject("message", "Les acc&egrave;s ont &eacute;t&eacute; supprim&eacute;s.");
        } else {
            mv.addObject("error", "Les acc&egrave;s ont &eacute;t&eacute; mis &agrave; jour.");
        }
        commonManageInstance(mv, instance, company);
        return mv;
    }

    @RequestMapping(value = "/{instancename}/admin/editCompany", method = RequestMethod.GET)
    public ModelAndView editCompany(@PathVariable("instancename") String instanceName) {

        ModelAndView mv = this.homepage(instanceName);
        Company company = userService.getCurrentUser().getCompany();
        mv.addObject("company", company);
        mv.setViewName("admin/company_edit");
        return mv;
    }
}
