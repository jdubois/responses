<%
   response.setHeader( "Pragma", "no-cache" );
   response.setHeader( "Cache-Control", "no-store" );
   response.setDateHeader( "Expires", -1 );
%>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - "${instance.longName}"</title>
    <jsp:include page="../fragments/html_head.jsp"/>
    <c:if test="${instance.type eq 1}">
        <link rel="alternate" title="RSS feed" href="${rssUrl}" TYPE="application/rss+xml">
    </c:if>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../fragments/header.jsp"/>
    <div id="content">
        <div id="tabs">
            <ul>
                <li class="tabs-title"><a href="#updated_questions"><span>Questions</span></a></li>
                <c:if test="${selectedTagsList ne null}">
                    <li class="tabs-title"><a href="${baseUrl}/new-questions/${selectedTagsList}"><span>Nouveaut&eacute;s</span></a></li>
                    <li class="tabs-title"><a href="${baseUrl}/top-questions/${selectedTagsList}"><span>Top</span></a></li>
                    <li class="tabs-title"><a href="${baseUrl}/unanswered/${selectedTagsList}"><span>Sans r&eacute;ponse</span></a></li>
                </c:if>
                <c:if test="${selectedTagsList eq null}">
                    <li class="tabs-title"><a href="${baseUrl}/new-questions"><span>Nouveaut&eacute;s</span></a></li>
                    <li class="tabs-title"><a href="${baseUrl}/top-questions"><span>Top</span></a></li>
                    <li class="tabs-title"><a href="${baseUrl}/unanswered"><span>Sans r&eacute;ponse</span></a></li>
                </c:if>
                <li class="tabs-title"><a href="${baseUrl}/tag/cloud"><span>Etiquettes</span></a></li>
            </ul>
            <div id="updated_questions">
                <jsp:include page="questions.jsp"/>
            </div>
            <div id="search_questions">
            </div>
            <div id="faq_questions">
            </div>
        </div>
        <div id="instance-info">
            <div class="box_header"><span></span></div>
            <div class="box_body">
                <p class="box_title">
                    <c:choose>
                        <c:when test="${questionSize <= 1}"><b>${questionSize}</b> question</c:when>
                        <c:otherwise><b>${questionSize}</b> questions</c:otherwise>
                    </c:choose>
                </p>
                <c:if test="${selectedTagsList ne null}">
                    <sec:authorize ifAnyGranted="ROLE_USER">
                        <img src="${staticContent}/style/page_white_edit.png" class="icon" alt=""/>&nbsp;<a href="${baseUrl}/tagged/${selectedTagsList}/question/ask"><b>Nouvelle question</b></a><br/><br/>
                    </sec:authorize>
                    <img src="${staticContent}/style/page_refresh.png" class="icon" alt="refresh"/>&nbsp;<a href="${baseUrl}/tagged/${selectedTagsList}">Rafra&icirc;chir</a><br/>
                    <img src="${staticContent}/style/link.png"  class="icon" alt="permlink"/>&nbsp;<a href="${baseUrl}/tagged/${selectedTagsList}">Lien permanent</a><br/>
                </c:if>
                <c:if test="${selectedTagsList eq null}">
                    <sec:authorize ifAnyGranted="ROLE_USER">
                        <img src="${staticContent}/style/page_white_edit.png" class="icon" alt=""/>&nbsp;<a href="${baseUrl}/question/ask"><b>Nouvelle question</b></a><br/><br/>
                    </sec:authorize>
                    <img src="${staticContent}/style/page_refresh.png" class="icon" alt="refresh"/>&nbsp;<a href="${baseUrl}/">Rafra&icirc;chir</a><br/>
                    <img src="${staticContent}/style/link.png" class="icon" alt="permlink"/>&nbsp;<a href="${baseUrl}/">Lien permanent</a><br/>
                </c:if>
                <c:if test="${instance.type eq 1}">
                    <img src="${staticContent}/style/feed.png" class="icon" alt="rss"/>&nbsp;<a href="${rssUrl}">Flux RSS</a><br/>
                </c:if><br/>
                <jsp:include page="../fragments/tag.jsp"/>
            </div>
            <div class="box_footer"><span></span></div>
        </div>
    </div>
    <jsp:include page="../fragments/footer.jsp"/>
    <script type="text/javascript">
        $(document).ready(function() {
            tabs = $("#tabs").tabs({
                ajaxOptions: { cache: false },
                <sec:authorize ifNotGranted="ROLE_USER">
                    cache: true,
                </sec:authorize>
                spinner: 'Chargement...',
                fx: { opacity: 'toggle', duration: 200 }
            });
            $("#tabs").bind('tabsload', function(event, ui) {
                decorateQuestions();
                questionTagTooltips();
                voteQuestionToolTips();
            });
            <sec:authorize ifAnyGranted="ROLE_USER">
            $("#tabs").bind('tabsselect', function(event, ui) {
                $("#questions").remove();
            });
            if (selectedTagsList === "") {
                tabs.tabs('url', 0, baseUrl + "/ajax<c:if test="${questionIndex != null}">?questionIndex=${questionIndex}</c:if>");
            } else {
                tabs.tabs('url', 0, baseUrl + "/tagged/" + selectedTagsList+ "/ajax<c:if test="${questionIndex != null}">?questionIndex=${questionIndex}</c:if>");
            }
            </sec:authorize>
            <c:if test="${message != null}">
                showMessage("${message}");
            </c:if>
            <c:if test="${error != null}">
                showError("${error}");
            </c:if>
            questionTagTooltips();
            tagToolTips();
            voteQuestionToolTips();
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
