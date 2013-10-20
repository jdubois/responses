<%@ page import="com.github.jdubois.responses.service.impl.ConfigurationServiceImpl" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    String siteUrl = ConfigurationServiceImpl.siteUrl;
    request.setAttribute("siteUrl", siteUrl);
%>
<div id="header">
    <ul id="menu">
        <c:if test="${instance eq null}">
        <li>
            <b><a href="${siteUrl}"><img src="${staticContent}/style/logo_sw.png" height="17" width="16" alt="Home"/> Accueil</a></b>
        </li>
        </c:if>
        <c:if test="${instance ne null}">
            <li>
                <b><a href="${siteUrl}"><img src="${staticContent}/style/logo_sw.png" height="17" width="16" alt="Home"/> Accueil</a> &gt;
                <a href="${baseUrl}/"> ${instance.longName}</a></b>
            </li>
            <li>
                <div>&nbsp;&nbsp;</div>
            </li>
            <sec:authorize ifAnyGranted="ROLE_USER">
                <li>
                    <ul class="sf-menu">
                        <li class="current">
                            <img src="${staticContent}/style/mouse.png" class="icon" alt=""/> Menu
                            <ul>
                                <sec:authorize ifAnyGranted="ROLE_ADMIN">
                                    <c:choose>
                                        <c:when test="${fn:contains(user.instances, instance)}">
                                            <li><a href="${baseUrl}/admin"><img src="${staticContent}/style/key.png" class="icon" alt=""/> Administration</a></li>
                                        </c:when>
                                        <c:otherwise>
                                            <sec:authorize ifAnyGranted="ROLE_SU">
                                                <li><a href="${baseUrl}/admin"><img src="${staticContent}/style/key.png" class="icon" alt=""/> Administration</a></li>
                                            </sec:authorize>
                                        </c:otherwise>
                                    </c:choose>
                                </sec:authorize>
                                <sec:authorize ifAnyGranted="ROLE_MODERATOR,ROLE_SUPPORT">
                                    <li><a href="${context}/support/"><img src="${staticContent}/style/bug.png" class="icon" alt=""/> Centre de support</a></li>
                                </sec:authorize>
                                <li><a href="${baseUrl}/profile/${user.id}/${user.profileUrl}"><img src="${staticContent}/style/user_suit.png" class="icon" alt=""/> Mon profil</a></li>
                                <li><a href="${baseUrl}/account"><img src="${staticContent}/style/user_edit.png" class="icon" alt=""/> Mon compte</a></li>
                                    <%--<li><a href="${baseUrl}/account/questions.page">Mes questions</a></li>--%>
                                    <%--<li><a href="${baseUrl}/account/favorites.page">Mes favoris</a></li>--%>
                                    <%--<li><a href="${baseUrl}/account/answers.page">Mes r&eacute;ponses</a></li>--%>
                                    <%--<li><a href="${baseUrl}/account/votes.page">Mes votes</a></li>--%>
                                <li><a href="${baseUrl}/account/watch"><img src="${staticContent}/style/email.png" class="icon" alt=""/> Mes alertes</a></li>
                                <li><a href="${context}/logout"><img src="${staticContent}/style/disconnect.png" class="icon" alt=""/> D&eacute;connexion</a></li>
                            </ul>
                        </li>
                    </ul>
                </li>
            </sec:authorize>
            <li>
                <div><span>&nbsp;&nbsp;</span></div>
            </li>
            <li id="menu_search">
                <div>
                    <img src="${staticContent}/style/magnifier.png" class="icon" alt="search"/>
                    <input type="text" id="menu_search_field" name="menu_search_field" size="25" maxlength="255"
                           value="${searchQuery}"/>
                </div>
            </li>

            <li>
                <div><span>&nbsp;&nbsp;</span></div>
            </li>
            <sec:authorize ifNotGranted="ROLE_USER">
                <li>
                    <a href="javascript:authentication();"><img src="${staticContent}/style/user_suit.png" class="icon" alt="User"/> Utilisateur non authentifi&eacute;
                    </a>
                </li>
            </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_USER">
                <li>
                   <img src="${staticContent}/style/user_suit.png" class="icon" alt="User"/> <a href="${baseUrl}/profile/${user.id}/${user.profileUrl}">${userName}</a>
                </li>
            </sec:authorize>
            <li>
                <div><span>&nbsp;&nbsp;</span></div>
            </li>
            <li><a href="${baseUrl}/help"><img src="${staticContent}/style/help.png" class="icon" alt=""/> Aide</a></li>
        </c:if>
    </ul>
    <div id="welcome" class="ui-widget" onclick="hideWelcome();">
        <div class="ui-state-highlight ui-corner-all" style="margin-top: 25px; padding:10px">
            <p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                <span id="welcome-text"></span></p>
        </div>
    </div>
    <div id="message" class="ui-widget" onclick="hideMessage();">
        <div class="ui-state-highlight ui-corner-all" style="margin-top: 25px; padding:10px">
            <p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                <span id="message-text"></span></p>
        </div>
    </div>
    <div id="error" class="ui-widget" onclick="hideError();">
        <div class="ui-state-error ui-corner-all" style="margin-top: 25px; padding: 10px">
            <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                <span id="error-text"></span></p>
        </div>
    </div>
</div>
