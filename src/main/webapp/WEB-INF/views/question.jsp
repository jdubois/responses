<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - "${question.title}"</title>
    <jsp:include page="../fragments/html_head.jsp"/>
    <c:if test="${instance.type eq 1}">
      <meta name="keywords" content="<c:forEach var="tag" items="${question.tags}">${tag.text}, </c:forEach>"/>
      <meta name="description" content="${question.title}"/>
    </c:if>
    <link href="${staticContent}/syntaxhighlighter_2.1.364/styles/shAll.css" rel="stylesheet" type="text/css" />
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../fragments/header.jsp"/>
    <div id="content">
        <div id="question-question">
            <div id="question-title"><h1>${question.title}</h1></div>
            <div id="question-text" class="display-html">
                ${question.text}
            </div>
            <c:if test="${instance.type eq 1}">
                <div class="addthis_toolbox addthis_default_style">
                    <span class="addthis_separator">Partager: </span><a class="addthis_button_twitter"></a><a class="addthis_button_facebook"></a><a class="addthis_button_email"></a><a class="addthis_button_favorites"></a><span class="addthis_separator">|</span>
                    <a href="http://www.addthis.com/bookmark.php?v=250&amp;username=responses" class="addthis_button_expanded">Plus</a>
                </div>
                <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js#username=com.github.jdubois.responses"></script>
            </c:if>
            <div>
                <div id="question-comments-img" <c:if test="${fn:length(question.questionComments) == 0}">style="display: none;"</c:if>>
                        <img src="${staticContent}/style/comment_question.png" width="695" height="36" alt="comment"/>
                </div>
                <div id="question-comments-container">
                    <c:forEach var="comment" items="${question.questionComments}" varStatus="status" begin="0" end="4"
                               step="1">
                        <div class="question-comments">
                                <a href="#c_${comment.id}">${status.index + 1}</a>
                                ${comment.value} - <a href="${baseUrl}/profile/${comment.user.id}/${comment.user.profileUrl}">${comment.user.firstName} ${comment.user.lastName}</a>
                        </div>
                    </c:forEach>
                    <c:if test="${fn:length(question.questionComments) > 5}">
                        <div id="question-comments-show" class="question-comments link" onclick="show_comments_question('${question.id}')">${fn:length(question.questionComments) - 5} commentaire(s) cach&eacute;(s)</div>
                    </c:if>
                </div>
                <div class="qa-actions">
                    <sec:authorize ifAnyGranted="ROLE_USER">
                        <img src="${staticContent}/style/page_white_edit.png" class="icon"
                             alt=""/>&nbsp;<a href="${baseUrl}/answer/new?questionId=${question.id}"><b>Nouvelle r&eacute;ponse</b></a>&nbsp;|
                        <c:if test="${isAuthor eq true}">
                            <img src="${staticContent}/style/page_white_wrench.png" class="icon"
                                 alt=""/>&nbsp;<a href="${baseUrl}/question/edit?questionId=${question.id}">Editer la question</a>&nbsp;|
                        </c:if>
                        <img src="${staticContent}/style/comment.png" class="icon" alt=""/>&nbsp;<span id="qa-actions-link--1" class="qa-actions-link" onclick="comment_select('-1')">Commenter</span>&nbsp;|
                    </sec:authorize>
                    <c:if test="${instance.type eq 1}">
                        <img src="${staticContent}/style/flag_blue.png" class="icon"
                                 alt=""/>&nbsp;<span class="qa-actions-link" onclick="abuseQuestion('${question.id}')">Signaler un abus</span>
                    </c:if>
                    <c:if test="${instance.type eq 0}">
                        <img src="${staticContent}/style/bug.png" class="icon" alt=""/>&nbsp;<span class="qa-actions-link" id="supportMessage" onclick="showSupport(${question.id})">
                            <c:if test="${wfState eq 0 or wfState eq 3}">
                                Support
                            </c:if>
                            <c:if test="${wfState eq 1}">
                                <b>Support (demande en attente de validation)</b>
                            </c:if>
                            <c:if test="${wfState eq 2}">
                                <b>Support (demande en attente d'assignation)</b>
                            </c:if>
                            <c:if test="${wfState eq 4}">
                                <b>Support (demande en attente de traitement)</b>
                            </c:if>
                            <c:if test="${wfState eq 5}">
                                <b>Support (demande r&eacute;solue)</b>
                            </c:if>
                            <c:if test="${wfState eq 6}">
                                <b>Support (solution refus&eacute;e)</b>
                            </c:if>
                            <c:if test="${wfState eq 7}">
                                <b>Support (solution valid&eacute;e)</b>
                            </c:if>
                        </span>
                    </c:if>
                    <br/>

                    <div id="comment_-1" class="qa-actions-comment">
                        <textarea id="comment_value_-1" rows="3" cols="80"></textarea>
                        <br/>
                        <span class="qa-actions-link" onclick="send_comment_question('${question.id}')">
                         Envoyer le commentaire <br/>
                        </span>
                    </div>
                </div>
            </div>
        </div>
        <div id="support-wrapper"></div>
        <h2>${question.answersSize} r&eacute;ponse<c:if test="${question.answersSize > 1}">s</c:if></h2>
        <div id="answers">
            <c:forEach var="answer" items="${answers}">
                <div id="a_${answer.id}" class="question-answer"><a name="${answer.id}"></a>
                    <table>
                        <tr>
                            <td>
                                <div class="question-answer-info">
                                    <div class="vote question-answer-vote">
                                        <table>
                                            <tr>
                                                <td class="vote_title">Vote</td>
                                            </tr>
                                            <sec:authorize ifNotGranted="ROLE_USER">
                                                <tr>
                                                    <td id='vote_u_a_${answer.id}' class='vote_u_u'></td>
                                                </tr>
                                                <tr>
                                                    <td id='vote_t_a_${answer.id}'
                                                        class='vote_t'>${answer.votesSize}</td>
                                                </tr>
                                                <tr>
                                                    <td id='vote_d_a_${answer.id}' class='vote_d_u'></td>
                                                </tr>
                                            </sec:authorize>
                                            <sec:authorize ifAnyGranted="ROLE_USER">
                                                <c:choose>
                                                    <c:when test="${user eq answer.user}">
                                                        <tr>
                                                            <td id='vote_u_a_${answer.id}' class='vote_u_u'></td>
                                                        </tr>
                                                        <tr>
                                                            <td id='vote_t_a_${answer.id}'
                                                                class='vote_t'>${answer.votesSize}</td>
                                                        </tr>
                                                        <tr>
                                                            <td id='vote_d_a_${answer.id}' class='vote_d_u'></td>
                                                        </tr>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <tr>
                                                            <c:choose>
                                                                <c:when test="${answer.currentUserVote == 1}">
                                                                    <td id='vote_u_a_${answer.id}' class='vote_u_h'
                                                                        onclick='voteForAnswer(${answer.id}, 0)'></td>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <td id='vote_u_a_${answer.id}' class='vote_u'
                                                                        onmouseover="vote_u_h('a_${answer.id}')"
                                                                        onmouseout="vote_u('a_${answer.id}')"
                                                                        onclick='voteForAnswer(${answer.id}, 1)'></td>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </tr>
                                                        <tr>
                                                            <c:choose>
                                                                <c:when test="${answer.currentUserVote != 0}">
                                                                    <td id='vote_t_a_${answer.id}'
                                                                        class='vote_t_h'>${answer.votesSize}</td>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <td id='vote_t_a_${answer.id}'
                                                                        class='vote_t'>${answer.votesSize}</td>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </tr>
                                                        <tr>
                                                            <c:choose>
                                                                <c:when test="${answer.currentUserVote == -1}">
                                                                    <td id='vote_d_a_${answer.id}' class='vote_d_h'
                                                                        onclick='voteForAnswer(${answer.id} , 0)'></td>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <td id='vote_d_a_${answer.id}' class='vote_d'
                                                                        onmouseover="vote_d_h('a_${answer.id}')"
                                                                        onmouseout="vote_d('a_${answer.id}')"
                                                                        onclick='voteForAnswer(${answer.id} , -1)'></td>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </tr>
                                                    </c:otherwise>
                                                </c:choose>
                                            </sec:authorize>
                                        </table>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <div>
                                    <img src="http://www.gravatar.com/avatar/${answer.user.smallGravatarUrl}.jpg" height="32" width="32" alt="gravatar"/>
                                    <span class="answer-title">Par <a
                                            href="${baseUrl}/profile/${answer.user.id}/${answer.user.profileUrl}">${answer.user.firstName} ${answer.user.lastName}</a> il y a ${answer.period}.</span>
                                    <c:if test="${question.bestAnswerId eq answer.id}"><span class="display-html-best-answer-title"><br/><img src="${staticContent}/style/tick.png" class="icon" alt=""/>&nbsp;Meilleure r&eacute;ponse</span></c:if>
                                 </div>
                                <div id="text_${answer.id}"
                                     class="display-html question-answer-text<c:if test="${question.bestAnswerId eq answer.id}"> display-html-best-answer</c:if>">
                                        ${answer.text}
                                </div>
                                <div id="answer-comments-img_${answer.id}" class="answer-comments-img"
                                     <c:if test="${fn:length(answer.answerComments) == 0}">style="display: none;"</c:if>>
                                    <img src="${staticContent}/style/comment_answer.png" width="550" height="36"
                                         alt="comment"/>
                                </div>
                                <div id="answer-comments-container_${answer.id}">
                                    <c:forEach var="comment" items="${answer.answerComments}" varStatus="status"
                                               begin="0" end="4"
                                               step="1">
                                        <div class="answer-comments">
                                            <a href="#a_${comment.id}">${status.index + 1}</a>
                                                ${comment.value} - <a
                                                href="${baseUrl}/profile/${comment.user.id}/${comment.user.profileUrl}">${comment.user.firstName} ${comment.user.lastName}</a>
                                        </div>
                                    </c:forEach>
                                    <c:if test="${fn:length(answer.answerComments) > 5}">
                                        <div id="answer-comments-show" class="answer-comments link"
                                             onclick="show_comments_answer('${answer.id}')">${fn:length(answer.answerComments) - 5}
                                            commentaire(s) cach&eacute;(s)
                                        </div>
                                    </c:if>
                                </div>

                                <div class="qa-actions">
                                    <img src="${staticContent}/style/link.png" class="icon"
                                        alt=""/>&nbsp;<span class="qa-actions-link"><a href="#${answer.id}">Lien permanent</a></span>
                                    <sec:authorize ifAnyGranted="ROLE_USER">
                                        <c:if test="${isAuthor eq true}">
                                            &nbsp;|&nbsp;<img src="${staticContent}/style/tick.png" class="icon" alt=""/>&nbsp;
                                            <span class="qa-actions-link" onclick="best_answer('${answer.id}')">S&eacute;lectionner comme meilleure r&eacute;ponse</span>
                                        </c:if>
                                        <c:if test="${user eq answer.user}">
                                            &nbsp;|&nbsp;<img src="${staticContent}/style/page_white_wrench.png" class="icon" alt=""/>&nbsp;
                                             <a href="${baseUrl}/answer/edit?answerId=${answer.id}">Editer la r&eacute;ponse</a>
                                        </c:if>
                                        &nbsp;|&nbsp;<img src="${staticContent}/style/comment.png" class="icon" alt=""/>&nbsp;<span id="answer-actions-link-${answer.id}" class="qa-actions-link" onclick="comment_select('${answer.id}')">Commenter</span>
                                    </sec:authorize>
                                    <c:if test="${instance.type eq 1}">
                                        &nbsp;|&nbsp;<img src="${staticContent}/style/flag_blue.png" class="icon"
                                            alt=""/>&nbsp;<span class="qa-actions-link" onclick="abuseAnswer('${answer.id}')">Signaler un abus</span>
                                    </c:if>
                                    <div id="comment_${answer.id}" class="qa-actions-comment">
                                            <textarea id="comment_value_${answer.id}" rows="3" cols="80"></textarea>
                                                <span class="qa-actions-link"
                                                      onclick="send_comment_answer('${answer.id}')">
                                                    <br/>Envoyer le commentaire
                                                </span>
                                    </div>
                                </div>

                            </td>
                        </tr>
                    </table>
                </div>
            </c:forEach>
            <sec:authorize ifAnyGranted="ROLE_USER">
                <div style="padding-left: 260px; padding-top: 50px; font-size: larger;"><a href="${baseUrl}/answer/new?questionId=${question.id}">Participez! Donnez votre r&eacute;ponse &agrave; cette question</a></div>
            </sec:authorize>
            <div class="questions-nav">
                <c:if test="${pagesNumber >= 1}">
                    <span>Pages : </span>
                    <c:choose>
                        <c:when test="${(answerIndex == 0)}">
                            <span class="pagination_select"><a href="${paginationUrl}answerIndex=0">1</a></span>
                        </c:when>
                        <c:otherwise>
                            <span class="pagination"><a href="${paginationUrl}answerIndex=0">1</a></span>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${(answerIndex > 4)}">
                        <span>...</span>
                    </c:if>
                    <c:forEach begin="1" end="${pagesNumber}" var="status">
                        <c:if test="${(answerIndex >= (status - 3)) && (answerIndex <= (status + 3) && (answerIndex != status))}">
                            <span class="pagination"><a
                                    href="${paginationUrl}answerIndex=${status}">${status + 1}</a></span>
                        </c:if>
                        <c:if test="${(answerIndex == status)}">
                            <span class="pagination_select"><a
                                    href="${paginationUrl}answerIndex=${status}">${status + 1}</a></span>
                        </c:if>
                    </c:forEach>
                    <c:if test="${(answerIndex <= pagesNumber - 4)}">
                        <span>...</span>
                    </c:if>
                </c:if>
            </div>
            <br/>
        </div>
        <div id="question-info">
            <div class="box_header"><span></span></div>
            <div class="box_body">
                <p class="box_title">${question.answersSize} r&eacute;ponse<c:if test="${question.answersSize > 1}">s</c:if></p>
                <img src="${staticContent}/style/page_white_edit.png" class="icon" alt=""/>&nbsp;<a href="${baseUrl}/answer/new?questionId=${question.id}"><b>Nouvelle r&eacute;ponse</b></a><br/><br/>
                <img src="${staticContent}/style/page_refresh.png" class="icon" alt="refresh"/>&nbsp;<a href="${baseUrl}/q/${question.id}/${question.titleAsUrl}">Rafra&icirc;chir</a><br/>
                <img src="${staticContent}/style/link.png" class="icon" alt="permlink"/>&nbsp;<a href="${baseUrl}/q/${question.id}/${question.titleAsUrl}">Lien permanent</a><br/>
                <img src="${staticContent}/style/magnifier.png" class="icon" alt="related"/>&nbsp;<a href="${baseUrl}/tagged/<c:forEach var="tag" items="${question.tags}" varStatus="status">${tag.text}<c:if test="${not status.last}">+</c:if></c:forEach>">Questions proches</a>
                <sec:authorize ifAnyGranted="ROLE_USER">
                     <div id="alert_u" style="display: none">
                       <div onclick="unwatch_question('${question.id}')"><img src="${staticContent}/style/email_open.png" class="icon" alt="email"/>&nbsp;<span class="alert">Alerte e-mail: <b>oui</b></span></div>
                     </div>
                    <div id="alert" style="display: none">
                        <div onclick="watch_question('${question.id}')"><img src="${staticContent}/style/email.png" class="icon" alt="email"/>&nbsp;<span class="alert">Alerte e-mail: <b>non</b></span></div>
                     </div>
                </sec:authorize>
                <br/><br/>
                <p class="box_title">Etiquettes</p>
                <c:if test="${isAuthor ne true}">
                    <c:forEach var="tag" items="${question.tags}">
                      <a href="${baseUrl}/tagged/${tag.text}"><span
                            class="questionTag">${tag.text}</span></a> <b>(${tag.size})</b><br/>
                    </c:forEach>
                </c:if>
                <c:if test="${isAuthor eq true}">
                    <c:forEach var="tag" items="${question.tags}">
                        <a href="${baseUrl}/tagged/${tag.text}"><span class="tag">${tag.text}</span></a>
                                <b>(${tag.size})</b>
                                <span class='tag_delete' onmouseover="$(this).addClass('tag_delete_h')"
                                      onmouseout="$(this).removeClass('tag_delete_h')"
                                      onclick="removeQuestionTag('${question.id}','${tag.text}')">x</span><br/>
                    </c:forEach>
                    <br/>
                    <c:if test="${fn:length(question.tags) lt 5}">
                        <input id="add_question_tag" name="add_question_tag" maxlength="20" class="tag_auto_complete"/><img src="${staticContent}/style/add.png" class="tag_add_img"
                             onclick="addQuestionTag('${question.id}')" alt="add"/>
                        <br/>
                    </c:if>
                </c:if>
                <br/>
                <p class="box_title">Informations</p>
                Question pos&eacute;e par<br/>
                <img src="http://www.gravatar.com/avatar/${question.user.gravatarUrl}.jpg" height="80" width="80" style="left: 25px" alt="gravatar"/><br/>
                <div style="text-align: center;"><a href="${baseUrl}/profile/${question.user.id}/${question.user.profileUrl}"><b>${question.user.firstName} ${question.user.lastName}</b></a></div><br/>
                Cr&eacute;&eacute;e le <br/><b><fmt:formatDate pattern="dd/MM/yyyy '&agrave;' HH:mm" value="${question.creationDate}"/></b>.<br/><br/>
                Mise &agrave; jour il y a <br/><b>${question.period}</b>.<br/><br/>
                Vue <br/><b>${question.views} fois</b>.<br/><br/>
                <p><b>Vote</b></p>
                <div id="question-info-vote">
                    <div class="vote"><table><tr><td class="vote_title">Vote</td></tr>
                        <sec:authorize ifNotGranted="ROLE_USER">
                            <tr><td id='vote_u_${question.id}' class='vote_u_u'></td></tr>
                            <tr><td id='vote_t_${question.id}' class='vote_t'>${question.votesSize}</td></tr>
                            <tr><td id='vote_d_${question.id}' class='vote_d_u'></td></tr>
                        </sec:authorize>
                        <sec:authorize ifAnyGranted="ROLE_USER">
                            <c:choose>
                                <c:when test="${user eq question.user}">
                                    <tr><td id='vote_u_${question.id}' class='vote_u_u'></td></tr>
                                    <tr><td id='vote_t_${question.id}' class='vote_t'>${question.votesSize}</td></tr>
                                    <tr><td id='vote_d_${question.id}' class='vote_d_u'></td></tr>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <c:choose>
                                             <c:when test="${question.currentUserVote == 1}">
                                                  <td id='vote_u_${question.id}' class='vote_u_h' onclick='voteForQuestion(${question.id}, 0)'></td>
                                             </c:when>
                                             <c:otherwise>
                                                <td id='vote_u_${question.id}' class='vote_u' onmouseover='vote_u_h(${question.id})'
                                                    onmouseout='vote_u(${question.id})' onclick='voteForQuestion(${question.id}, 1)'></td>
                                             </c:otherwise>
                                        </c:choose>
                                    </tr><tr>
                                        <c:choose>
                                            <c:when test="${question.currentUserVote != 0}">
                                                <td id='vote_t_${question.id}' class='vote_t_h'>${question.votesSize}</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td id='vote_t_${question.id}' class='vote_t'>${question.votesSize}</td>
                                            </c:otherwise>
                                        </c:choose>
                                    </tr><tr>
                                        <c:choose>
                                            <c:when test="${question.currentUserVote == -1}">
                                                <td id='vote_d_${question.id}' class='vote_d_h' onclick='voteForQuestion(${question.id} , 0)'></td
                                            </c:when>
                                            <c:otherwise>
                                                <td id='vote_d_${question.id}' class='vote_d' onmouseover='vote_d_h(${question.id})'
                                                    onmouseout='vote_d(${question.id})' onclick='voteForQuestion(${question.id} , -1)'></td>
                                            </c:otherwise>
                                        </c:choose>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </sec:authorize>
                    </table></div>
                </div>
                <br/>
                <sec:authorize ifNotGranted="ROLE_USER">
                    <p>Si vous &eacute;tiez <a href="javascript:authentication();">authentifi&eacute;</a>, vous pourriez poser des questions, y r&eacute;pondre, les commenter, s&eacute;lectionner
                        des &eacute;tiquettes favorites ou ignor&eacute;es...</p>
                </sec:authorize>
            </div>
            <div class="box_footer"><span></span></div>
        </div>
    </div>
    <jsp:include page="../fragments/footer.jsp"/>
    <script type="text/javascript" src="${staticContent}/syntaxhighlighter_2.1.364/scripts/shBrushAll.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            <c:if test="${message != null}">
                showMessage("${message}");
            </c:if>
            <c:if test="${error != null}">
                showError("${error}");
            </c:if>
            <c:choose>
                <c:when test="${watched eq true}">
                     $("#alert_u").show();
                </c:when>
                <c:otherwise>
                    $("#alert").show();
                </c:otherwise>
            </c:choose>
            <c:if test="${isAuthor eq true}">
                $("#add_question_tag").autocomplete(baseUrl + "/tag/search", {
                    width: 130,
                    cacheLength: 50
                });
            </c:if>
            setTimeout("countQuestionView('${question.id}')", 1000);
            SyntaxHighlighter.all();
            tagToolTips();
            voteAnswerToolTips();
            <c:if test="${welcome eq null}">
                <sec:authorize ifNotGranted="ROLE_USER">
                    showWelcome();
                    <c:set var="welcome" scope="session" value="true"/>
                </sec:authorize>
            </c:if>
        });
    </script>
    </div>
</body>
</html>