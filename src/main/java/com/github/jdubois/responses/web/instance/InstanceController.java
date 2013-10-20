package com.github.jdubois.responses.web.instance;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.model.Tag;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.*;
import com.github.jdubois.responses.service.dto.SearchQuestionResult;
import com.github.jdubois.responses.service.dto.TagSummaryInformation;
import com.github.jdubois.responses.service.impl.ConfigurationServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Julien Dubois
 */
@Controller
public class InstanceController extends BaseController {

    private final Log log = LogFactory.getLog(InstanceController.class);

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserTagService userTagService;

    @Autowired
    private SearchEngineService searchEngineService;

    @Autowired
    private WatchService watchService;

    //Tag url splitting
    private Pattern tagSplitter = Pattern.compile("\\+");

    @RequestMapping(value = "/{instancename}", method = RequestMethod.GET)
    public String getInstance(@PathVariable("instancename") String instanceName,
                              @RequestParam(required = false) String newQuestion,
                              HttpServletRequest request) {

        getInstanceAjax(instanceName, request);
        int instanceId = ((Instance) request.getAttribute("instance")).getId();
        //RSS Url
        request.setAttribute("rssUrl", ConfigurationServiceImpl.siteUrl + "/rss?instanceId=" + instanceId);

        //Related tags
        TagSummaryInformation info = this.tagService.getTagSummaryFor(instanceId);
        request.setAttribute("popularTags", info.getRelatedTags());

        //New question
        newQuestionMessage(newQuestion, request);

        return "instance";
    }

    private void newQuestionMessage(String newQuestion, HttpServletRequest request) {
        if (newQuestion != null) {
            request.setAttribute("message", "Votre question a bien &eacute;t&eacute; enregistr&eacute;e. Merci de votre participation!");
        }
    }

    @RequestMapping(value = "/{instancename}/ajax", method = RequestMethod.GET)
    public String getInstanceAjax(@PathVariable("instancename") String instanceName, HttpServletRequest request) {
        int instanceId = genericDisplayInstance(instanceName, request);

        //Last questions
        Integer questionIndex = (Integer) request.getAttribute("questionIndex");
        Collection<Question> questions = questionService.showLatestQuestions(instanceId, true, false, questionIndex);
        request.setAttribute("questions", questions);

        //Pagination
        Integer questionSize = (int) instanceService.getInstanceSize(instanceId);
        paginationCalculation(request, questionSize);
        String paginationUrl = request.getContextPath() + "/i/" + instanceName + "?";
        request.setAttribute("paginationUrl", paginationUrl);

        return "questions";
    }

    @RequestMapping(value = "/{instancename}/tagged/{taglist}", method = RequestMethod.GET)
    public String getTag(@PathVariable("instancename") String instanceName,
                         @PathVariable("taglist") String tagList,
                         @RequestParam(required = false) String newQuestion,
                         HttpServletRequest request) {

        getTagAjax(instanceName, tagList, request);
        int instanceId = ((Instance) request.getAttribute("instance")).getId();
        //RSS Url
        request.setAttribute("rssUrl", ConfigurationServiceImpl.siteUrl + "/rss?instanceId=" + instanceId + "&tagged=" + tagList.replace("+", "%2b"));

        //New question
        newQuestionMessage(newQuestion, request);

        return "instance";
    }

    @RequestMapping(value = "/{instancename}/tagged/{taglist}/ajax", method = RequestMethod.GET)
    public String getTagAjax(@PathVariable("instancename") String instanceName,
                             @PathVariable("taglist") String tagList,
                             HttpServletRequest request) {
        int instanceId = genericDisplayInstance(instanceName, request);

        //Tag management
        request.setAttribute("selectedTagsList", tagList);
        String[] selTagsArray = tagSplitter.split(tagList);
        Arrays.sort(selTagsArray);
        if (selTagsArray.length == 0 || selTagsArray.length > 5) {
            return "instance";
        }
        request.setAttribute("selectedTags", selTagsArray);

        //Pagination
        TagSummaryInformation tagSummaryInfo;
        if (selTagsArray.length == 1) {
            tagSummaryInfo = this.tagService.getTagSummaryFor(instanceId, selTagsArray[0]);
        } else if (selTagsArray.length == 2) {
            tagSummaryInfo = this.tagService.getTagSummaryFor(instanceId, selTagsArray[0], selTagsArray[1]);
        } else if (selTagsArray.length == 3) {
            tagSummaryInfo = this.tagService.getTagSummaryFor(instanceId, selTagsArray[0], selTagsArray[1], selTagsArray[2]);
        } else if (selTagsArray.length == 4) {
            tagSummaryInfo = this.tagService.getTagSummaryFor(instanceId, selTagsArray[0], selTagsArray[1], selTagsArray[2], selTagsArray[3]);
        } else {
            tagSummaryInfo = this.tagService.getTagSummaryFor(instanceId, selTagsArray[0], selTagsArray[1], selTagsArray[2], selTagsArray[3], selTagsArray[4]);
        }
        Integer questionSize = tagSummaryInfo.getSize();
        request.setAttribute("popularTags", tagSummaryInfo.getRelatedTags());
        paginationCalculation(request, questionSize);
        String paginationUrl = request.getContextPath() + "/i/" + instanceName + "/tagged/" + tagList + "?";
        request.setAttribute("paginationUrl", paginationUrl);

        Integer questionIndex = (Integer) request.getAttribute("questionIndex");
        Collection<Question> questions = this.questionService.showQuestionsForTags(instanceId, true, false, selTagsArray, questionIndex);
        request.setAttribute("questions", questions);
        return "questions";
    }

    @RequestMapping(value = "/{instancename}/tagged", method = RequestMethod.GET)
    public String getTag(@PathVariable("instancename") String instanceName,
                         HttpServletRequest request) {

        return getInstance(instanceName, null, request);
    }


    @RequestMapping(value = "/{instancename}/unanswered", method = RequestMethod.GET)
    public String unanswered(@PathVariable("instancename") String instanceName,
                             HttpServletRequest request) {

        return unanswered(instanceName, null, request);
    }

    @RequestMapping(value = "/{instancename}/unanswered/{taglist}", method = RequestMethod.GET)
    public String unanswered(@PathVariable("instancename") String instanceName,
                             @PathVariable("taglist") String tagList,
                             HttpServletRequest request) {
        Instance instance = putInstanceInRequest(instanceName, request);
        this.putUserInRequest(request);

        Collection<Question> questions;
        if (tagList == null) {
            questions = questionService.showLatestQuestions(instance.getId(), true, true, 0);
        } else {
            request.setAttribute("selectedTagsList", tagList);
            String[] selTagsArray = tagSplitter.split(tagList);
            Arrays.sort(selTagsArray);
            questions = questionService.showQuestionsForTags(instance.getId(), true, true, selTagsArray, 0);
        }
        request.setAttribute("questions", questions);

        return "questions";
    }

    @RequestMapping(value = "/{instancename}/new-questions", method = RequestMethod.GET)
    public String newQuestions(@PathVariable("instancename") String instanceName,
                               HttpServletRequest request) {

        return newQuestions(instanceName, null, request);
    }

    @RequestMapping(value = "/{instancename}/new-questions/{taglist}", method = RequestMethod.GET)
    public String newQuestions(@PathVariable("instancename") String instanceName,
                               @PathVariable("taglist") String tagList,
                               HttpServletRequest request) {

        Instance instance = putInstanceInRequest(instanceName, request);
        putUserInRequest(request);
        Collection<Question> questions;
        if (tagList == null) {
            questions = questionService.showLatestQuestions(instance.getId(), false, false, 0);
        } else {
            request.setAttribute("selectedTagsList", tagList);
            String[] selTagsArray = tagSplitter.split(tagList);
            Arrays.sort(selTagsArray);
            questions = questionService.showQuestionsForTags(instance.getId(), false, false, selTagsArray, 0);
        }
        request.setAttribute("questions", questions);

        return "questions";
    }

    @RequestMapping(value = "/{instancename}/suggest", method = RequestMethod.GET)
    @ResponseBody
    public String suggest(@PathVariable("instancename") String instanceName, @RequestParam String q) {
        int instanceId = instanceService.findInstanceIdByName(instanceName);
        List<Object[]> suggests = this.searchEngineService.suggestQuestions(instanceId, q);
        StringBuffer results = new StringBuffer();
        for (Object[] suggest : suggests) {
            String result = (String) suggest[0];
            results.append(result);
            results.append("\n");
        }
        return results.toString();
    }

    @RequestMapping(value = "/{instancename}/search", method = RequestMethod.GET)
    public String search(@PathVariable("instancename") String instanceName, @RequestParam String q,
                         HttpServletRequest request) {

        int instanceId = genericDisplayInstance(instanceName, request);

        boolean showNegativeQuestions = false;
        String tmp = request.getParameter("showNegativeQuestions");
        if (tmp != null) {
            showNegativeQuestions = true;
        }

        int sort = SearchEngineService.SORT_BY_DEFAULT;
        tmp = request.getParameter("sort");
        try {
            sort = Integer.valueOf(tmp);
        } catch (NumberFormatException e) {
            sort = 0;
        }
        if (sort < 0 || sort > 3) {
            sort = 0;
        }

        Integer questionIndex = (Integer) request.getAttribute("questionIndex");
        SearchQuestionResult results;
        if (q.equals("") || q.equals("*") || q.equals("?")) {
            results = new SearchQuestionResult();
            results.setResultSize(0);
            results.setQuestions(new ArrayList<Question>());
        } else {
            if (!showNegativeQuestions && sort == SearchEngineService.SORT_BY_DEFAULT && questionIndex == 0) {
                results = this.searchEngineService.searchQuestions(instanceId, q);
            } else {
                results = this.searchEngineService.searchQuestions(instanceId, q, showNegativeQuestions,
                        sort, questionIndex);
            }
        }

        request.setAttribute("searchQuery", q);
        request.setAttribute("showNegativeQuestions", showNegativeQuestions);
        request.setAttribute("sort", sort);
        request.setAttribute("questions", results.getQuestions());
        Integer questionSize = results.getResultSize();
        paginationCalculation(request, questionSize);
        String paginationUrl = request.getContextPath() + "/i/" + instanceName + "/search?q=" + q;
        if (showNegativeQuestions) {
            paginationUrl += "&showNegativeQuestions=true";
        }
        if (sort != 0) {
            paginationUrl += "&sort=" + sort;
        }
        paginationUrl += "&";
        request.setAttribute("paginationUrl", paginationUrl);

        return "search";
    }

    @RequestMapping(value = "/{instancename}/about", method = RequestMethod.GET)
    public String about(@PathVariable("instancename") String instanceName, HttpServletRequest request) {
        request.setAttribute("instanceName", instanceName);
        Instance instance = instanceService.getInstanceByName(instanceName);
        request.setAttribute("instance", instance);

        return "about";
    }

    private int genericDisplayInstance(String instanceName, HttpServletRequest request) {
        Instance instance = putInstanceInRequest(instanceName, request);
        putSearchQueryInRequest(request);

        //Question index management
        String strQuestionIndex = request.getParameter("questionIndex");
        int questionIndex = 0;
        if (strQuestionIndex != null) {
            try {
                int test = Integer.valueOf(strQuestionIndex);
                if (test > 0) {
                    questionIndex = test;
                }
            } catch (NumberFormatException nfe) {
                questionIndex = 0;
            }
            if (questionIndex >= 30) {
                questionIndex = 30;
            }
        }
        request.setAttribute("questionIndex", questionIndex);

        //User specific data, favorite and ignored tags
        User user = userService.getCurrentUser();
        if (user != null) {
            request.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
            request.setAttribute("user", user);
            Collection<Tag> favTags = userTagService.getFavoriteTags(user, instance.getId());
            String[] favTagsArray = new String[favTags.size()];
            int i = 0;
            for (Tag tag : favTags) {
                favTagsArray[i++] = tag.getText();
            }
            request.setAttribute("favoriteTags", favTagsArray);

            Collection<Tag> ignTags = userTagService.getIgnoredTags(user, instance.getId());
            String[] ignTagsArray = new String[ignTags.size()];
            i = 0;
            for (Tag tag : ignTags) {
                ignTagsArray[i++] = tag.getText();
            }
            request.setAttribute("ignoredTags", ignTagsArray);
        }
        return instance.getId();
    }
}
