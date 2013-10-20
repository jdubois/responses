package com.github.jdubois.responses.web.instance;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Tag;
import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.service.SearchEngineService;
import com.github.jdubois.responses.service.TagService;
import com.github.jdubois.responses.service.UserTagService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author Julien Dubois
 */
@Controller
public class TagController extends BaseController {

    private final Log log = LogFactory.getLog(TagController.class);

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private UserTagService userTagService;

    @Autowired
    private TagService tagService;

    @Autowired
    private SearchEngineService searchEngineService;

    @RequestMapping(value = "/{instancename}/tag/search", method = RequestMethod.GET)
    @ResponseBody
    public String searchTag(@PathVariable("instancename") String instanceName, HttpServletRequest request) {
        int instanceId = instanceService.findInstanceIdByName(instanceName);
        String query = request.getParameter("q");
        List<Object[]> tags = this.searchEngineService.searchTags(instanceId, query);
        StringBuffer results = new StringBuffer();
        for (Object[] tag : tags) {
            String result = (String) tag[0];
            results.append(result);
            results.append("\n");
        }
        //TODO mettre la taille des tags
        return results.toString();
    }

    @RequestMapping(value = "/{instancename}/tag/create/favorite/{tagname}", method = RequestMethod.GET)
    @ResponseBody
    public String addFavoriteTag(@PathVariable("instancename") String instanceName, @PathVariable("tagname") String tagName) {
        try {
            int instanceId = instanceService.findInstanceIdByName(instanceName);
            Set<Tag> tags = this.userTagService.addFavoriteTag(instanceId, tagName);
            return extractTagsList(tags);
        } catch (Exception e) {
            log.error("Error while adding a favorite tag : " + e.getMessage());
            return "";
        }
    }

    @RequestMapping(value = "/{instancename}/tag/delete/favorite/{tagname}", method = RequestMethod.GET)
    @ResponseBody
    public String removeFavoriteTag(@PathVariable("instancename") String instanceName, @PathVariable("tagname") String tagName) {
        try {
            int instanceId = instanceService.findInstanceIdByName(instanceName);
            Set<Tag> tags = this.userTagService.removeFavoriteTag(instanceId, tagName);
            return extractTagsList(tags);
        } catch (Exception e) {
            log.error("Error while removing a favorite tag : " + e.getMessage());
            return "";
        }
    }

    @RequestMapping(value = "/{instancename}/tag/create/ignored/{tagname}", method = RequestMethod.GET)
    @ResponseBody
    public String addIgnoredTag(@PathVariable("instancename") String instanceName, @PathVariable("tagname") String tagName) {
        try {
            int instanceId = instanceService.findInstanceIdByName(instanceName);
            Set<Tag> tags = this.userTagService.addIgnoredTag(instanceId, tagName);
            return extractTagsList(tags);
        } catch (Exception e) {
            log.error("Error while adding an ignored tag : " + e.getMessage());
            return "";
        }
    }

    @RequestMapping(value = "/{instancename}/tag/delete/ignored/{tagname}", method = RequestMethod.GET)
    @ResponseBody
    public String removeIgnoredTag(@PathVariable("instancename") String instanceName, @PathVariable("tagname") String tagName) {
        try {
            int instanceId = instanceService.findInstanceIdByName(instanceName);
            Set<Tag> tags = this.userTagService.removeIgnoredTag(instanceId, tagName);
            return extractTagsList(tags);
        } catch (Exception e) {
            log.error("Error while removing an ignored tag : " + e.getMessage());
            return "";
        }
    }

    @RequestMapping(value = "/{instancename}/tag/cloud", method = RequestMethod.GET)
    public String showTagCloud(@PathVariable("instancename") String instanceName, HttpServletRequest request) {
        Instance instance = this.putInstanceInRequest(instanceName, request);
        List<Tag> tags = tagService.getPopularTags(instance.getId(), 100);
        request.setAttribute("tags", tags);
        return "tag_cloud";
    }

    /**
     * Transforms a set of Tags into a string that can be used in JavaScript.
     */
    private String extractTagsList(Set<Tag> tags) {
        StringBuffer results = new StringBuffer();
        for (Tag tag : tags) {
            results.append(tag.getText()).append("+");
        }
        if (results.length() > 0) {
            return results.substring(0, results.length() - 1);
        } else {
            return "";
        }
    }
}
