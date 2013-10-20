package com.github.jdubois.responses.web.su;

import com.github.jdubois.responses.model.Company;
import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.service.*;
import com.github.jdubois.responses.web.instance.BaseController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class SuController extends BaseController {

    private final Log log = LogFactory.getLog(SuController.class);

    @Autowired
    private AsyncExecutor asyncExecutor;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private DeletionService deletionService;

    @Autowired
    private ReportingService reportingService;

    @Autowired
    private SearchEngineService searchEngineService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView homepage() {

        ModelAndView mv = new ModelAndView();
        this.putUserInModel(mv);
        mv.addObject("statistics", reportingService.getInstanceStatistics());
        mv.setViewName("su/dashboard");
        return mv;
    }

    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    public ModelAndView configuration() {

        ModelAndView mv = new ModelAndView();
        this.putUserInModel(mv);
        mv.setViewName("su/configuration");
        return mv;
    }

    @RequestMapping(value = "/configuration", method = RequestMethod.POST)
    public ModelAndView configurationUpdate(@RequestParam String action) {
        if ("rebuildLuceneIndex".equals(action)) {
            searchEngineService.rebuildLuceneIndex();
        } else if ("cleanUpApplication".equals(action)) {
            asyncExecutor.asyncCleanupApplication();
        }
        return this.configuration();
    }

    @RequestMapping(value = "/companies", method = RequestMethod.GET)
    public ModelAndView companies() {

        ModelAndView mv = new ModelAndView();
        this.putUserInModel(mv);
        mv.addObject("companies", companyService.getAllCompanies());
        mv.setViewName("su/companies");
        return mv;
    }

    @RequestMapping(value = "/companies", method = RequestMethod.POST)
    public ModelAndView updateCompanies(@RequestParam String action,
                                        @RequestParam(required = false) String companyName) {

        if ("addCompany".equals(action)) {
            Company company = new Company();
            company.setName(companyName);
            this.companyService.addCompany(company);
        }
        return this.companies();
    }

    @RequestMapping(value = "/company", method = RequestMethod.GET)
    public ModelAndView showCompany(@RequestParam int companyId) {

        ModelAndView mv = new ModelAndView();
        this.putUserInModel(mv);
        Company company = this.companyService.getCompany(companyId);
        mv.addObject("company", company);
        mv.setViewName("su/company");
        return mv;
    }

    @RequestMapping(value = "/instance")
    public ModelAndView manageInstance(@RequestParam String action,
                                       @RequestParam(required = false) Integer companyId,
                                       @RequestParam(required = false) String newInstanceName,
                                       @RequestParam(required = false) String newInstanceLongName,
                                       @RequestParam(required = false) Integer instanceId) {

        if ("add".equals(action)) {
            Instance instance = new Instance();
            instance.setName(newInstanceName);
            instance.setLongName(newInstanceLongName);
            instance.setType(Instance.TYPE_PRIVATE);
            this.instanceService.addInstance(companyId, instance);
        } else if ("delete".equals(action)) {
            this.deletionService.deleteInstance(instanceId);
        } else if (("edit").equals(action)) {
            this.instanceService.editInstance(instanceId, newInstanceLongName);
        }
        return this.showCompany(companyId);
    }
}
