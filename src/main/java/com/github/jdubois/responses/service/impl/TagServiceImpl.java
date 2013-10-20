package com.github.jdubois.responses.service.impl;

import com.github.jdubois.responses.model.Instance;
import com.github.jdubois.responses.model.Tag;
import com.github.jdubois.responses.model.User;
import com.github.jdubois.responses.service.InstanceService;
import com.github.jdubois.responses.service.TagService;
import com.github.jdubois.responses.service.UserService;
import com.github.jdubois.responses.service.dto.TagSummaryInformation;
import com.github.jdubois.responses.service.exception.InstanceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Julien Dubois
 */
@Repository
@SuppressWarnings("unchecked")
public class TagServiceImpl implements TagService, ApplicationListener {

    private final Log log = LogFactory.getLog(TagServiceImpl.class);

    //Cache TagSummaryInformation by (instanceId (key, TagSummaryInformation))
    //where key is the concatenation of the tags text : tag1+tag2+tag3+tag4+tag5+
    private Map<Integer, Map<String, TagSummaryInformation>> tagSummaryInfoCache;

    public static final String SQL_TAG_SUMMARY_1 = "select tag.text as text, " +
            " s.size as size " +
            "from (" +
            " (SELECT t1.tag1 as tag ," +
            "   t1.size as size " +
            " FROM TagSummary t1 " +
            "  where t1.tag2= ? " +
            "  and t1.tag3  ='0' " +
            "  order by t1.size desc limit 10 " +
            " ) " +
            "union " +
            " (SELECT t2.tag2 as tag, " +
            "   t2.size as size " +
            " FROM TagSummary t2 " +
            "  where t2.tag1= ? " +
            "  and t2.tag3  ='0' " +
            "  order by t2.size desc limit 10 " +
            " )) as s " +
            " inner join Tag tag on s.tag=tag.id " +
            "order by s.size desc limit 10";

    public static final String SQL_TAG_SUMMARY_2 = "select tag.text as text, " +
            " s.size as size " +
            "from (" +
            " (SELECT t1.tag1 as tag ," +
            "   t1.size as size " +
            " FROM TagSummary t1 " +
            "  where t1.tag2= ? " +
            "  and t1.tag3  =? " +
            "  and t1.tag4  ='0' " +
            "  order by t1.size desc limit 10 " +
            " ) " +
            "union " +
            " (SELECT t2.tag2 as tag, " +
            "   t2.size as size " +
            " FROM TagSummary t2 " +
            "  where t2.tag1= ? " +
            "  and t2.tag3  =? " +
            "  and t2.tag4  ='0' " +
            "  order by t2.size desc limit 10 " +
            " ) " +
            "union " +
            " (SELECT t3.tag3 as tag, " +
            "   t3.size as size " +
            " FROM TagSummary t3 " +
            "  where t3.tag1= ? " +
            "  and t3.tag2  =? " +
            "  and t3.tag4  ='0' " +
            "  order by t3.size desc limit 10 " +
            " )) as s " +
            " left join Tag tag on s.tag=tag.id " +
            "order by s.size desc, tag.text asc limit 11";

    public static final String SQL_TAG_SUMMARY_3 = "select tag.text as text, " +
            " s.size as size " +
            "from (" +
            " (SELECT t1.tag1 as tag ," +
            "   t1.size as size " +
            " FROM TagSummary t1 " +
            "  where t1.tag2= ? " +
            "  and t1.tag3  =? " +
            "  and t1.tag4  =? " +
            "  and t1.tag5  ='0' " +
            "  order by t1.size desc limit 10 " +
            " ) " +
            "union " +
            " (SELECT t2.tag2 as tag ," +
            "   t2.size as size " +
            " FROM TagSummary t2 " +
            "  where t2.tag1= ? " +
            "  and t2.tag3  =? " +
            "  and t2.tag4  =? " +
            "  and t2.tag5  ='0' " +
            "  order by t2.size desc limit 10 " +
            " ) " +
            "union " +
            " (SELECT t3.tag3 as tag ," +
            "   t3.size as size " +
            " FROM TagSummary t3 " +
            "  where t3.tag1= ? " +
            "  and t3.tag2  =? " +
            "  and t3.tag4  =? " +
            "  and t3.tag5  ='0' " +
            "  order by t3.size desc limit 10 " +
            " ) " +
            "union " +
            " (SELECT t4.tag4 as tag ," +
            "   t4.size as size " +
            " FROM TagSummary t4 " +
            "  where t4.tag1= ? " +
            "  and t4.tag2  =? " +
            "  and t4.tag3  =? " +
            "  and t4.tag5  ='0' " +
            "  order by t4.size desc limit 10 " +
            " )) as s " +
            " left join Tag tag on s.tag=tag.id " +
            "order by s.size desc, tag.text asc limit 11";

    public static final String SQL_TAG_SUMMARY_4 = "select tag.text as text, " +
            " s.size as size " +
            "from (" +
            " (SELECT t1.tag1 as tag ," +
            "   t1.size as size " +
            " FROM TagSummary t1 " +
            "  where t1.tag2= ? " +
            "  and t1.tag3  =? " +
            "  and t1.tag4  =? " +
            "  and t1.tag5  = ? " +
            "  order by t1.size desc limit 10 " +
            " ) " +
            "union " +
            " (SELECT t2.tag2 as tag ," +
            "   t2.size as size " +
            " FROM TagSummary t2 " +
            "  where t2.tag1= ? " +
            "  and t2.tag3  =? " +
            "  and t2.tag4  =? " +
            "  and t2.tag5  = ? " +
            "  order by t2.size desc limit 10 " +
            " ) " +
            "union " +
            " (SELECT t3.tag3 as tag ," +
            "   t3.size as size " +
            " FROM TagSummary t3 " +
            "  where t3.tag1= ? " +
            "  and t3.tag2  =? " +
            "  and t3.tag4  =? " +
            "  and t3.tag5  =? " +
            "  order by t3.size desc limit 10 " +
            " ) " +
            "union " +
            " (SELECT t4.tag4 as tag ," +
            "   t4.size as size " +
            " FROM TagSummary t4 " +
            "  where t4.tag1= ? " +
            "  and t4.tag2  =? " +
            "  and t4.tag3  =? " +
            "  and t4.tag5  =? " +
            "  order by t4.size desc limit 10 " +
            " ) " +
            "union " +
            " (SELECT t5.tag5 as tag ," +
            "   t5.size as size " +
            " FROM TagSummary t5 " +
            "  where t5.tag1= ? " +
            "  and t5.tag2  =? " +
            "  and t5.tag3  =? " +
            "  and t5.tag4  =? " +
            "  order by t5.size desc limit 10 " +
            " )) as s " +
            " left join Tag tag on s.tag=tag.id " +
            "order by s.size desc, tag.text asc limit 11";

    public static final String SQL_TAG_SUMMARY_5 = "SELECT t.size as size " +
            "FROM TagSummary t " +
            "  where t.tag1= ? " +
            "  and t.tag2  =? " +
            "  and t.tag3  =? " +
            "  and t.tag4  =? " +
            "  and t.tag5  =? ";

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserService userService;

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //The tag index : instance_id/name/key
    private Map<Integer, Map<String, Integer>> tagsIndex;

    //Tag name validation
    Pattern tagValidator = Pattern.compile("[a-z0-9-]{1,20}");

    private List<Tag> emptyList = new ArrayList<Tag>();

    @PostConstruct
    private void init() {
        tagSummaryInfoCache = new ConcurrentHashMap<Integer, Map<String, TagSummaryInformation>>();
    }

    @Transactional
    @Secured("ROLE_USER")
    public Tag addTag(int instanceId, String text) {

        //Validation, returns null if the text is not valid
        Matcher matcher = tagValidator.matcher(text);
        if (!matcher.matches()) {
            log.info("The tag \"" + text + "\" is not valid.");
            return null;
        }

        Instance instance = instanceService.getInstance(instanceId);
        if (!isTagAlreadyCreated(instanceId, text)) {
            Tag tag = new Tag();
            tag.setText(text);
            tag.setSize(0);
            tag.setInstance(instance);
            em.persist(tag);
            Map<String, Integer> instanceMap = getTagsIndex(instance.getId());
            instanceMap.put(text, tag.getId());
            if (log.isInfoEnabled()) {
                User user = userService.getCurrentUser();
                log.info("Tag \"" + text + "\" in instance \"" + instance.getId() + "\" created by user id #" + user.getId());
            }
            return tag;
        } else {
            return this.getTagFromText(instance, text);
        }
    }

    @Transactional(readOnly = true)
    public Tag getTagFromText(int instanceId, String text) {
        Instance instance = instanceService.getInstance(instanceId);
        return this.getTagFromText(instance, text);
    }

    private Tag getTagFromText(Instance instance, String text) {
        Map<String, Integer> instanceMap = getTagsIndex(instance.getId());
        Integer id = instanceMap.get(text);
        if (id == null) {
            return null;
        } else {
            return em.find(Tag.class, id);
        }
    }

    public int getTagIdFromText(int instanceId, String text) {
        Map<String, Integer> instanceMap = getTagsIndex(instanceId);
        Integer id = instanceMap.get(text);
        if (id == null) {
            return 0;
        } else {
            return id;
        }
    }

    @Transactional(readOnly = true)
    public List<Tag> getPopularTags(int instanceId, int maxResults) {
        javax.persistence.Query query = em.createNamedQuery("Tag.getPopularTags");
        query.setParameter("instanceId", instanceId);
        query.setMaxResults(maxResults);
        return (List<Tag>) query.getResultList();
    }

    @Transactional(readOnly = true)
    public TagSummaryInformation getTagSummaryFor(int instanceId) {
        String[] tagsArray = {};
        TagSummaryInformation info = getCacheTagSummaryInfo(instanceId, tagsArray);
        if (info == null) {
            info = new TagSummaryInformation();
            List<Tag> relatedTags = getPopularTags(instanceId, 10);
            info.setRelatedTags(relatedTags);
            putCacheTagSummaryInfo(instanceId, tagsArray, info);
        }
        return info;
    }

    @Transactional(readOnly = true)
    public TagSummaryInformation getTagSummaryFor(int instanceId, String tag1) {
        String[] tagsArray = {tag1};
        TagSummaryInformation info = getCacheTagSummaryInfo(instanceId, tagsArray);
        if (info == null) {
            info = new TagSummaryInformation();
            Tag _tag1 = this.getTagFromText(instanceId, tag1);
            if (_tag1 == null) {
                info.setSize(0);
            } else {
                info.setSize(_tag1.getSize());
                if (log.isDebugEnabled()) {
                    log.debug("jdbcTemplace : SQL_TAG_SUMMARY_1");
                }

                List<Tag> relatedTags = jdbcTemplate.query(
                        SQL_TAG_SUMMARY_1, new Object[]{_tag1.getId(), _tag1.getId()},
                        TagRowMapper);

                info.setRelatedTags(relatedTags);
            }
            putCacheTagSummaryInfo(instanceId, tagsArray, info);
        }
        return info;
    }

    @Transactional(readOnly = true)
    public TagSummaryInformation getTagSummaryFor(int instanceId, String tag1, String tag2) {
        String[] tagsArray = {tag1, tag2};
        TagSummaryInformation info = getCacheTagSummaryInfo(instanceId, tagsArray);
        if (info == null) {
            info = new TagSummaryInformation();
            Tag _tag1 = this.getTagFromText(instanceId, tag1);
            Tag _tag2 = this.getTagFromText(instanceId, tag2);

            if (_tag1 == null || _tag2 == null) {
                info.setSize(0);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("jdbcTemplace : SQL_TAG_SUMMARY_2");
                }

                List<Tag> relatedTags = jdbcTemplate.query(
                        SQL_TAG_SUMMARY_2, new Object[]{_tag1.getId(), _tag2.getId(),
                        _tag1.getId(), _tag2.getId(),
                        _tag1.getId(), _tag2.getId()},
                        TagRowMapper);

                if (relatedTags.size() > 0) {
                    Tag summary = relatedTags.remove(0);
                    info.setSize(summary.getSize());
                } else {
                    info.setSize(0);
                }
                info.setRelatedTags(relatedTags);
            }
            putCacheTagSummaryInfo(instanceId, tagsArray, info);
        }
        return info;
    }

    @Transactional(readOnly = true)
    public TagSummaryInformation getTagSummaryFor(int instanceId, String tag1, String tag2, String tag3) {
        String[] tagsArray = {tag1, tag2, tag3};
        TagSummaryInformation info = getCacheTagSummaryInfo(instanceId, tagsArray);
        if (info == null) {
            info = new TagSummaryInformation();
            Tag _tag1 = this.getTagFromText(instanceId, tag1);
            Tag _tag2 = this.getTagFromText(instanceId, tag2);
            Tag _tag3 = this.getTagFromText(instanceId, tag3);

            if (_tag1 == null || _tag2 == null || _tag3 == null) {
                info.setSize(0);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("jdbcTemplace : SQL_TAG_SUMMARY_3");
                }

                List<Tag> relatedTags = jdbcTemplate.query(
                        SQL_TAG_SUMMARY_3, new Object[]{_tag1.getId(), _tag2.getId(), _tag3.getId(),
                        _tag1.getId(), _tag2.getId(), _tag3.getId(),
                        _tag1.getId(), _tag2.getId(), _tag3.getId(),
                        _tag1.getId(), _tag2.getId(), _tag3.getId()},
                        TagRowMapper);

                if (relatedTags.size() > 0) {
                    Tag summary = relatedTags.remove(0);
                    info.setSize(summary.getSize());
                } else {
                    info.setSize(0);
                }
                info.setRelatedTags(relatedTags);
            }
            putCacheTagSummaryInfo(instanceId, tagsArray, info);
        }
        return info;
    }

    @Transactional(readOnly = true)
    public TagSummaryInformation getTagSummaryFor(int instanceId, String tag1, String tag2, String tag3, String tag4) {
        String[] tagsArray = {tag1, tag2, tag3, tag4};
        TagSummaryInformation info = getCacheTagSummaryInfo(instanceId, tagsArray);
        if (info == null) {
            info = new TagSummaryInformation();
            Tag _tag1 = this.getTagFromText(instanceId, tag1);
            Tag _tag2 = this.getTagFromText(instanceId, tag2);
            Tag _tag3 = this.getTagFromText(instanceId, tag3);
            Tag _tag4 = this.getTagFromText(instanceId, tag4);

            if (_tag1 == null || _tag2 == null || _tag3 == null || _tag4 == null) {
                info.setSize(0);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("jdbcTemplace : SQL_TAG_SUMMARY_4");
                }
                List<Tag> relatedTags = jdbcTemplate.query(
                        SQL_TAG_SUMMARY_4, new Object[]{_tag1.getId(), _tag2.getId(), _tag3.getId(), _tag4.getId(),
                        _tag1.getId(), _tag2.getId(), _tag3.getId(), _tag4.getId(),
                        _tag1.getId(), _tag2.getId(), _tag3.getId(), _tag4.getId(),
                        _tag1.getId(), _tag2.getId(), _tag3.getId(), _tag4.getId(),
                        _tag1.getId(), _tag2.getId(), _tag3.getId(), _tag4.getId()},
                        TagRowMapper);

                if (relatedTags.size() > 0) {
                    Tag summary = relatedTags.remove(0);
                    info.setSize(summary.getSize());
                } else {
                    info.setSize(0);
                }
                info.setRelatedTags(relatedTags);
            }
            putCacheTagSummaryInfo(instanceId, tagsArray, info);
        }
        return info;
    }

    @Transactional(readOnly = true)
    public TagSummaryInformation getTagSummaryFor(int instanceId, String tag1, String tag2, String tag3, String tag4, String tag5) {
        String[] tagsArray = {tag1, tag2, tag3, tag4, tag5};
        TagSummaryInformation info = getCacheTagSummaryInfo(instanceId, tagsArray);
        if (info == null) {
            info = new TagSummaryInformation();
            Tag _tag1 = this.getTagFromText(instanceId, tag1);
            Tag _tag2 = this.getTagFromText(instanceId, tag2);
            Tag _tag3 = this.getTagFromText(instanceId, tag3);
            Tag _tag4 = this.getTagFromText(instanceId, tag4);
            Tag _tag5 = this.getTagFromText(instanceId, tag5);
            if (_tag1 == null || _tag2 == null || _tag3 == null || _tag4 == null || _tag5 == null) {
                info.setSize(0);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("jdbcTemplace : SQL_TAG_SUMMARY_5");
                }
                int size = jdbcTemplate.queryForObject(
                        SQL_TAG_SUMMARY_5, new Object[]{_tag1.getId(), _tag2.getId(), _tag3.getId(), _tag4.getId(), _tag5.getId()},
                        Integer.class);

                info.setSize(size);
                info.setRelatedTags(emptyList);
            }
            putCacheTagSummaryInfo(instanceId, tagsArray, info);
        }
        return info;
    }

    private boolean isTagAlreadyCreated(int instanceId, String text) {
        Map<String, Integer> instanceMap = getTagsIndex(instanceId);
        if (instanceMap == null) {
            if (log.isDebugEnabled())
                log.debug("The instance id=" + instanceId + " is not in the cache!");

            return false;
        }
        return instanceMap.containsKey(text);
    }

    private Map<String, Integer> getTagsIndex(int instanceId) {
        Map<String, Integer> instanceMap = tagsIndex.get(instanceId);
        if (instanceMap == null) {
            if (log.isDebugEnabled()) {
                log.debug("The instance id=" + instanceId + " is not in the cache!");
            }

            Instance instance = em.find(Instance.class, instanceId);
            if (instance == null) {
                throw new InstanceException("No instance with id " + instanceId);
            } else {
                //put the instance in the cache
                instanceMap = new ConcurrentHashMap<String, Integer>(1000);
                tagsIndex.put(instance.getId(), instanceMap);
                return instanceMap;
            }
        }
        return instanceMap;
    }

    @Transactional(readOnly = true)
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent && tagsIndex == null) {
            refreshTagsCache();
        }
    }

    private void refreshTagsCache() {
        int tagsSize = 0;
        tagsIndex = new ConcurrentHashMap<Integer, Map<String, Integer>>(100);
        List<Instance> instances = em.createQuery("select i from Instance i").getResultList();
        for (Instance instance : instances) {
            tagsIndex.put(instance.getId(), new ConcurrentHashMap<String, Integer>(1000));
        }
        List<Tag> tags = em.createQuery("select t from Tag t").getResultList();
        for (Tag tag : tags) {
            Map<String, Integer> instanceMap = tagsIndex.get(tag.getInstance().getId());
            instanceMap.put(tag.getText(), tag.getId());
            if (log.isInfoEnabled()) {
                tagsSize++;
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Loaded " + tagsSize + " tags in the tags cache.");
        }
    }

    private static ParameterizedRowMapper<Tag> TagRowMapper = new ParameterizedRowMapper<Tag>() {
        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
            Tag tag = new Tag();
            tag.setText(rs.getString("text"));
            tag.setSize(rs.getInt("size"));
            return tag;
        }
    };

    private void putCacheTagSummaryInfo(int instanceId, String[] tagsArray, TagSummaryInformation info) {
        Map<String, TagSummaryInformation> instanceCache = tagSummaryInfoCache.get(instanceId);
        if (instanceCache == null) {
            instanceCache = new ConcurrentHashMap<String, TagSummaryInformation>();
            tagSummaryInfoCache.put(instanceId, instanceCache);
        }
        String key = keyForCacheTagSummaryInfo(tagsArray);
        instanceCache.put(key, info);
        if (log.isDebugEnabled()) {
            log.debug("PUT cacheTagSummaryInfo : instance=" + instanceId + "|key=" + key);
        }
    }

    private TagSummaryInformation getCacheTagSummaryInfo(int instanceId, String[] tagsArray) {
        Map<String, TagSummaryInformation> instanceCache = tagSummaryInfoCache.get(instanceId);
        if (instanceCache == null) {
            return null;
        }
        String key = keyForCacheTagSummaryInfo(tagsArray);
        if (log.isDebugEnabled()) {
            log.debug("GET cacheTagSummaryInfo : instance=" + instanceId + "|key=" + key);
        }
        return instanceCache.get(key);
    }

    public void cleanCacheTagSummaryInfo(int instanceId, Set<Tag> tags) {
        String tagsArray[] = new String[tags.size()];
        int index = 0;
        for (Tag tag : tags) {
            tagsArray[index++] = tag.getText();
        }
        cleanCacheTagSummaryInfo(instanceId, tagsArray);
    }

    public void cleanCacheTagSummaryInfo(int instanceId, String[] tagsArray) {
        Map<String, TagSummaryInformation> instanceCache = tagSummaryInfoCache.get(instanceId);
        if (instanceCache != null) {
            internalCleanCacheTagSummaryInfo(instanceCache);
            if (tagsArray.length == 1) {
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0]);
            } else if (tagsArray.length == 2) {
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1]);

                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1]);
            } else if (tagsArray.length == 3) {
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[2]);

                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[2]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[2]);

                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[2]);
            } else if (tagsArray.length == 4) {
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[2]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[3]);

                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[2]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1], tagsArray[2]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[2], tagsArray[3]);

                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[2]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[2], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1], tagsArray[2], tagsArray[3]);

                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[2], tagsArray[3]);
            } else if (tagsArray.length == 5) {
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[2]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[4]);

                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[2]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1], tagsArray[2]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[2], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[2], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[3], tagsArray[4]);

                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[2]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[2], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[2], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[3], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1], tagsArray[2], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1], tagsArray[2], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1], tagsArray[3], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[2], tagsArray[3], tagsArray[4]);

                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[2], tagsArray[3]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[2], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[3], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[2], tagsArray[3], tagsArray[4]);
                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[1], tagsArray[2], tagsArray[3], tagsArray[4]);

                internalCleanCacheTagSummaryInfo(instanceCache, tagsArray[0], tagsArray[1], tagsArray[2], tagsArray[3], tagsArray[4]);
            }

        }
    }

    private void internalCleanCacheTagSummaryInfo(Map<String, TagSummaryInformation> instanceCache) {
        String[] tmpArray = {};
        internalCleanCacheTagSummaryInfo(instanceCache, tmpArray);
    }

    private void internalCleanCacheTagSummaryInfo(Map<String, TagSummaryInformation> instanceCache, String tag1) {
        String[] tmpArray = {tag1};
        internalCleanCacheTagSummaryInfo(instanceCache, tmpArray);
    }

    private void internalCleanCacheTagSummaryInfo(Map<String, TagSummaryInformation> instanceCache, String tag1, String tag2) {
        String[] tmpArray = {tag1, tag2};
        internalCleanCacheTagSummaryInfo(instanceCache, tmpArray);
    }

    private void internalCleanCacheTagSummaryInfo(Map<String, TagSummaryInformation> instanceCache, String tag1, String tag2, String tag3) {
        String[] tmpArray = {tag1, tag2, tag3};
        internalCleanCacheTagSummaryInfo(instanceCache, tmpArray);
    }

    private void internalCleanCacheTagSummaryInfo(Map<String, TagSummaryInformation> instanceCache, String tag1, String tag2, String tag3, String tag4) {
        String[] tmpArray = {tag1, tag2, tag3, tag4};
        internalCleanCacheTagSummaryInfo(instanceCache, tmpArray);
    }

    private void internalCleanCacheTagSummaryInfo(Map<String, TagSummaryInformation> instanceCache, String tag1, String tag2, String tag3, String tag4, String tag5) {
        String[] tmpArray = {tag1, tag2, tag3, tag4, tag5};
        internalCleanCacheTagSummaryInfo(instanceCache, tmpArray);
    }

    private void internalCleanCacheTagSummaryInfo(Map<String, TagSummaryInformation> instanceCache, String[] tagsArray) {
        String key = keyForCacheTagSummaryInfo(tagsArray);
        instanceCache.remove(key);
        if (log.isDebugEnabled()) {
            log.debug("CLEAN cacheTagSummaryInfo : key=" + key);
        }
    }

    private String keyForCacheTagSummaryInfo(String[] tagsArray) {
        StringBuffer buffer = new StringBuffer("");
        for (String tag : tagsArray) {
            buffer.append(tag);
            buffer.append("+");
        }
        return buffer.toString();
    }
}
