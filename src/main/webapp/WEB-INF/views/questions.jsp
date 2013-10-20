<%@ page import="com.github.jdubois.responses.service.impl.ConfigurationServiceImpl" %>
<%@ page import="com.github.jdubois.responses.model.Instance" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%
    String staticContent = ConfigurationServiceImpl.staticContent;
    request.setAttribute("staticContent", staticContent);
    Instance instance = (Instance) request.getAttribute("instance");
    if (instance != null) {
        request.setAttribute("baseUrl", request.getContextPath() + "/i/" + instance.getName());
    }
%>
<div id="questions">
    <c:forEach var="question" items="${questions}">
        <div id="q_${question.id}" class="question">
            <c:set var="questionUrl" scope="page" value="${baseUrl}/q/${question.id}/${question.titleAsUrl}"/>
            <table><tr><td><div class="vote"><table><tr><td class="vote_title">Vote</td></tr>
            <sec:authorize ifNotGranted="ROLE_USER">
                        <tr><td class='vote_u_u'></td></tr>
                        <tr><td class='vote_t'>${question.votesSize}</td></tr>
                        <tr><td class='vote_d_u'></td></tr>
                </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_USER">
                    <c:choose>
                        <c:when test="${user eq question.user}">
                            <tr><td id='vote_u_${question.id}' class='vote_u_u'></td></tr>
                            <tr><td id='vote_t_${question.id}' class='vote_t'>${question.votesSize}</td></tr>
                            <tr><td id='vote_d_${question.id}' class='vote_d_u'></td></tr>
                        </c:when>
                        <c:otherwise><tr>
                            <c:choose>
                                <c:when test="${question.currentUserVote == 1}">
                                    <td id='vote_u_${question.id}' class='vote_u_h' onclick='voteForQuestion(${question.id}, 0)'></td>
                                </c:when>
                                <c:otherwise>
                                    <td id='vote_u_${question.id}' class='vote_u' onmouseover='vote_u_h(${question.id})' onmouseout='vote_u(${question.id})' onclick='voteForQuestion(${question.id}, 1)'></td>
                                </c:otherwise>
                            </c:choose></tr><tr><c:choose>
                                <c:when test="${question.currentUserVote != 0}">
                                    <td id='vote_t_${question.id}' class='vote_t_h'>${question.votesSize}</td>
                                </c:when>
                                <c:otherwise>
                                    <td id='vote_t_${question.id}' class='vote_t'>${question.votesSize}</td>
                                </c:otherwise>
                            </c:choose></tr><tr><c:choose>
                                <c:when test="${question.currentUserVote == -1}">
                                    <td id='vote_d_${question.id}' class='vote_d_h' onclick='voteForQuestion(${question.id} , 0)'></td>
                                </c:when>
                                <c:otherwise>
                                    <td id='vote_d_${question.id}' class='vote_d' onmouseover='vote_d_h(${question.id})' onmouseout='vote_d(${question.id})' onclick='voteForQuestion(${question.id} , -1)'></td>
                                </c:otherwise>
                            </c:choose></tr>
                        </c:otherwise>
                    </c:choose>
                </sec:authorize>
            </table></div></td><td>
            <div class="questionTitle"> <a href="${questionUrl}" style="color: #bc0400;">${question.title}</a></div>
            <div class="questionInfo">
                <c:forEach var="tag" items="${question.tags}"> <a href="${baseUrl}/tagged/${tag.text}"><span class="questionTag">${tag.text}</span></a> </c:forEach><br/>
                R&eacute;ponses: <span class="questionInfoNb">${question.answersSize}</span> | Affichages: <span class="questionInfoNb">${question.views}</span> |
                <a href="${baseUrl}/profile/${question.user.id}/${question.user.profileUrl}"><b>${question.user.firstName} ${question.user.lastName}</b></a> | Mise &agrave; jour il y a ${question.period}.
            </div></td></tr></table>
        </div>
    </c:forEach>
    <div class="questions-nav">
        <c:if test="${pagesNumber >= 1}">
            <span>Pages : </span>
            <c:choose>
                <c:when test="${(questionIndex == 0)}">
                    <span class="pagination_select"><a href="${paginationUrl}questionIndex=0">1</a></span>
                </c:when>
                <c:otherwise>
                    <span class="pagination"><a href="${paginationUrl}questionIndex=0">1</a></span>
                </c:otherwise>
            </c:choose>
            <c:if test="${(questionIndex > 4)}">
                <span>...</span>
            </c:if>
            <c:forEach begin="1" end="${pagesNumber}" var="status">
                <c:if test="${(questionIndex >= (status - 3)) && (questionIndex <= (status + 3) && (questionIndex != status))}">
                    <span class="pagination"><a href="${paginationUrl}questionIndex=${status}">${status + 1}</a></span>
                </c:if>
                <c:if test="${(questionIndex == status)}">
                    <span class="pagination_select"><a href="${paginationUrl}questionIndex=${status}">${status + 1}</a></span>
                </c:if>
            </c:forEach>
            <c:if test="${(questionIndex <= pagesNumber - 4)}">
                <span>...</span>
            </c:if>
        </c:if>
    </div>
</div>