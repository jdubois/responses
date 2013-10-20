package com.github.jdubois.responses.model;

import com.github.jdubois.responses.lucene.InstanceFilter;
import com.github.jdubois.responses.service.util.SeoUtil;
import org.apache.solr.analysis.ASCIIFoldingFilterFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Julien Dubois
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
        @NamedQuery(name = "Question.getAllQuestionsForInstance",
                query = "select q from Question q " +
                        "where q.instance.id = :instanceId"),
        @NamedQuery(name = "Question.getLastQuestionDateForUser",
                query = "select max(q.creationDate) from Question q " +
                        "where q.user.id = :userId"),
        @NamedQuery(name = "Question.getLatestQuestions",
                query = "select q from Question q " +
                        "where q.instance.id = :instanceId " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor1Tag",
                query = "select q from Question q " +
                        "join q.tags t " +
                        "where q.instance.id = :instanceId " +
                        "and t.id = :tagId1 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor2Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor3Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor4Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor5Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "join q.tags t5 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "and t5.id = :tagId5 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getLatestQuestionsUnanswered",
                query = "select q from Question q " +
                        "where q.instance.id = :instanceId " +
                        "and q.answersSize = 0 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor1TagUnanswered",
                query = "select q from Question q " +
                        "join q.tags t " +
                        "where q.instance.id = :instanceId " +
                        "and q.answersSize = 0 " +
                        "and t.id = :tagId1 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor2TagUnanswered",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "where q.instance.id = :instanceId " +
                        "and q.answersSize = 0 " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor3TagUnanswered",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "where q.instance.id = :instanceId " +
                        "and q.answersSize = 0 " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor4TagUnanswered",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "where q.instance.id = :instanceId " +
                        "and q.answersSize = 0 " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor5TagUnanswered",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "join q.tags t5 " +
                        "where q.instance.id = :instanceId " +
                        "and q.answersSize = 0 " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "and t5.id = :tagId5 " +
                        "order by q.updateDate desc"),
        @NamedQuery(name = "Question.getLatestQuestionsCreated",
                query = "select q from Question q " +
                        "where q.instance.id = :instanceId " +
                        "order by q.creationDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor1TagCreated",
                query = "select q from Question q " +
                        "join q.tags t " +
                        "where q.instance.id = :instanceId " +
                        "and t.id = :tagId1 " +
                        "order by q.creationDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor2TagCreated",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "order by q.creationDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor3TagCreated",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "order by q.creationDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor4TagCreated",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "order by q.creationDate desc"),
        @NamedQuery(name = "Question.getQuestionsFor5TagCreated",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "join q.tags t5 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "and t5.id = :tagId5 " +
                        "order by q.creationDate desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopular",
                query = "select q from Question q " +
                        "where q.instance.id = :instanceId " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularForDate",
                query = "select q from Question q " +
                        "where q.instance.id = :instanceId " +
                        "and q.creationDate > :creationDate " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsViews",
                query = "select q from Question q " +
                        "where q.instance.id = :instanceId " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsForDate",
                query = "select q from Question q " +
                        "where q.instance.id = :instanceId " +
                        "and q.creationDate > :creationDate " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularFor1Tag",
                query = "select q from Question q " +
                        "join q.tags t " +
                        "where q.instance.id = :instanceId " +
                        "and t.id = :tagId1 " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularFor1TagForDate",
                query = "select q from Question q " +
                        "join q.tags t " +
                        "where q.instance.id = :instanceId " +
                        "and t.id = :tagId1 " +
                        "and q.creationDate > :creationDate " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsFor1Tag",
                query = "select q from Question q " +
                        "join q.tags t " +
                        "where q.instance.id = :instanceId " +
                        "and t.id = :tagId1 " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsFor1TagForDate",
                query = "select q from Question q " +
                        "join q.tags t " +
                        "where q.instance.id = :instanceId " +
                        "and t.id = :tagId1 " +
                        "and q.creationDate > :creationDate " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularFor2Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularFor2TagForDate",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and q.creationDate > :creationDate " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsFor2Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsFor2TagForDate",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and q.creationDate > :creationDate " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularFor3Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularFor3TagForDate",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and q.creationDate > :creationDate " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsFor3Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsFor3TagForDate",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and q.creationDate > :creationDate " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularFor4Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularFor4TagForDate",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "and q.creationDate > :creationDate " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsFor4Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsFor4TagForDate",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "and q.creationDate > :creationDate " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularFor5Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "join q.tags t5 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "and t5.id = :tagId5 " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsPopularFor5TagForDate",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "join q.tags t5 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "and t5.id = :tagId5 " +
                        "and q.creationDate > :creationDate " +
                        "order by q.votesSize desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsFor5Tag",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "join q.tags t5 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "and t5.id = :tagId5 " +
                        "order by q.views desc"),
        @NamedQuery(name = "Question.getTopQuestionsViewsFor5TagForDate",
                query = "select q from Question q " +
                        "join q.tags t1 " +
                        "join q.tags t2 " +
                        "join q.tags t3 " +
                        "join q.tags t4 " +
                        "join q.tags t5 " +
                        "where q.instance.id = :instanceId " +
                        "and t1.id = :tagId1 " +
                        "and t2.id = :tagId2 " +
                        "and t3.id = :tagId3 " +
                        "and t4.id = :tagId4 " +
                        "and t5.id = :tagId5 " +
                        "and q.creationDate > :creationDate " +
                        "order by q.views desc")})
@Indexed
@FullTextFilterDefs({
        @FullTextFilterDef(name = "instanceFilter", impl = InstanceFilter.class)
})
// TODO HTML is not analyzed anymore with the new LUCENE version !!!
@AnalyzerDef(name = "html-analyzer",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class),
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
                        @Parameter(name = "language", value = "French")
                })
        })
public class Question implements Serializable {

    private static final long serialVersionUID = -883713775234637130L;

    public static Map<Long, String> titleAsUrlCache = new ConcurrentHashMap<Long, String>(1000);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private long id;

    private long views;

    @Analyzer(definition = "string-analyzer")
    @Field(index = Index.YES, store = Store.NO)
    @Boost(2f)
    @Size(max = 140)
    @NotNull
    private String title;

    @Analyzer(definition = "html-analyzer")
    @Field(index = Index.YES, store = Store.NO)
    @Boost(1.5f)
    @Length(max = 10000)
    @NotNull
    private String text;

    @Field(index = Index.NO)
    @DateBridge(resolution = Resolution.SECOND)
    private Date creationDate;

    @Field(index = Index.NO)
    @DateBridge(resolution = Resolution.SECOND)
    private Date updateDate;

    @Field(index = Index.NO)
    private int votesSize;

    @Field(index = Index.NO)
    private int answersSize;

    private long bestAnswerId;

    @OneToMany(mappedBy = "question")
    @Sort(type = SortType.NATURAL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @BatchSize(size = 10)
    @IndexedEmbedded
    private Set<Answer> answers;

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
    @MapKey(name = "userId")
    @BatchSize(size = 40)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Map<Integer, QuestionVote> questionVotes;

    @ManyToMany(fetch = FetchType.EAGER)
    @Sort(type = SortType.NATURAL)
    @BatchSize(size = 40)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @IndexedEmbedded(depth = 1)
    private Set<Tag> tags;

    @OneToMany(mappedBy = "question")
    @Sort(type = SortType.NATURAL)
    @BatchSize(size = 20)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<QuestionComment> questionComments;

    @OneToMany(mappedBy = "question")
    @Sort(type = SortType.NATURAL)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Workflow> workflows;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Watch> watchs;

    @ManyToOne
    private User user;

    @ManyToOne
    @IndexedEmbedded
    private Instance instance;

    private int wfState;

    private int wfAssignedUser;

    private Date wfDate;

    @Transient
    private String period;

    @Transient
    private int currentUserVote;

    public String getTitleAsUrl() {
        String cache = titleAsUrlCache.get(id);
        if (cache != null) {
            return cache;
        } else {
            cache = SeoUtil.seoFromEscapedHtml(title);
            titleAsUrlCache.put(id, cache);
            return cache;
        }
    }

    public long getId() {
        return id;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public int getVotesSize() {
        return votesSize;
    }

    public void setVotesSize(int votesSize) {
        this.votesSize = votesSize;
    }

    public int getAnswersSize() {
        return answersSize;
    }

    public void setAnswersSize(int answersSize) {
        this.answersSize = answersSize;
    }

    public long getBestAnswerId() {
        return bestAnswerId;
    }

    public void setBestAnswerId(long bestAnswerId) {
        this.bestAnswerId = bestAnswerId;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public Map<Integer, QuestionVote> getQuestionVotes() {
        return questionVotes;
    }

    public void setQuestionVotes(Map<Integer, QuestionVote> questionVotes) {
        this.questionVotes = questionVotes;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<QuestionComment> getQuestionComments() {
        return questionComments;
    }

    public void setQuestionComments(Set<QuestionComment> questionComments) {
        this.questionComments = questionComments;
    }

    public Set<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(Set<Workflow> workflows) {
        this.workflows = workflows;
    }

    public Set<Watch> getWatchs() {
        return watchs;
    }

    public void setWatchs(Set<Watch> watchs) {
        this.watchs = watchs;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public int getWfState() {
        return wfState;
    }

    public void setWfState(int wfState) {
        this.wfState = wfState;
    }

    public Date getWfDate() {
        return wfDate;
    }

    public void setWfDate(Date wfDate) {
        this.wfDate = wfDate;
    }

    public int getWfAssignedUser() {
        return wfAssignedUser;
    }

    public void setWfAssignedUser(int wfAssignedUser) {
        this.wfAssignedUser = wfAssignedUser;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getCurrentUserVote() {
        return currentUserVote;
    }

    public void setCurrentUserVote(int currentUserVote) {
        this.currentUserVote = currentUserVote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;

        Question question = (Question) o;

        return id == question.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", views=" + views +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", votesSize=" + votesSize +
                ", answersSize=" + answersSize +
                ", user=" + user.getEmail() +
                ", period='" + period + '\'' +
                ", currentUserVote=" + currentUserVote +
                '}';
    }
}
