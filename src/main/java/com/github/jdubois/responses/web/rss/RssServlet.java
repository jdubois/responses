package com.github.jdubois.responses.web.rss;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Question;
import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.service.QuestionService;
import com.github.jdubois.responses.service.impl.ConfigurationServiceImpl;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Julien Dubois
 */
public class RssServlet extends HttpServlet {

    private final Log log = LogFactory.getLog(RssServlet.class);

    /**
     * Default feed type.
     */
    private static final String FEED_TYPE = "rss_2.0";

    /**
     * Default mime type.
     */
    private static final String MIME_TYPE = "application/xml; charset=UTF-8";

    private Pattern tagSplitter = Pattern.compile("\\+");

    private InstanceService instanceService;

    private QuestionService questionService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(config
                .getServletContext());

        instanceService = context.getBean(InstanceService.class);
        questionService = context.getBean(QuestionService.class);
    }

    @Override
    protected final void doGet(HttpServletRequest request,
                               HttpServletResponse response) throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        String instanceParam = request.getParameter("instanceId");
        int instanceId = Integer.parseInt(instanceParam);
        Instance instance = instanceService.getInstance(instanceId);
        if (instance.getType() != Instance.TYPE_PUBLIC) {
            String msg = "L'instance \"" + instance.getName() + "\" est protégée.";
            log.error("Security exception on RSS feed: " + msg);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, msg);
        } else {
            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType(FEED_TYPE);
            String tagList = request.getParameter("tagged");
            Collection<Question> questions;
            if (tagList != null) {
                feed.setTitle("Responses, dernières questions de l'instance \"" + instance.getLongName() + "\" étiquettées \"" + tagList + "\"");
                feed.setLink(ConfigurationServiceImpl.siteUrl + "/i/" + instance.getName() + "/tagged/" + tagList);
                String[] selTagsArray = tagSplitter.split(tagList);
                Arrays.sort(selTagsArray);
                questions = this.questionService.showQuestionsForTags(instance.getId(), false, false, selTagsArray, 0);
            } else {
                feed.setTitle("Responses, dernières questions de l'instance \"" + instance.getLongName() + "\"");
                feed.setLink(ConfigurationServiceImpl.siteUrl + "/i/" + instance.getName());
                questions = questionService.showLatestQuestions(instanceId, false, false, 0);
            }
            feed.setDescription("Ce flux RSS liste les 20 dernières questions posées.");

            List<SyndEntry> entries = new ArrayList<SyndEntry>();
            String url = ConfigurationServiceImpl.siteUrl + "/i/" + instance.getName() + "/q/";
            for (Question question : questions) {
                SyndEntry entry = new SyndEntryImpl();
                entry.setLink(url + question.getId() + "/" + question.getTitleAsUrl());
                SyndContent description = new SyndContentImpl();
                description.setType("text/html");
                entry.setTitle(question.getTitle());
                entry.setPublishedDate(question.getCreationDate());
                description.setValue(question.getText());
                entry.setDescription(description);
                entries.add(entry);
            }
            feed.setEntries(entries);

            response.setContentType(MIME_TYPE);
            SyndFeedOutput output = new SyndFeedOutput();
            try {
                output.output(feed, response.getWriter());
            } catch (FeedException fe) {
                String msg = "Le flux RSS n'a pas pu être généré.";
                log.error("Error while generating the RSS feed: "
                        + fe.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        msg);
            }
        }
    }
}
