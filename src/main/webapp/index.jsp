<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="com.github.jdubois.responses.model.User" %>
<%@ page import="com.github.jdubois.responses.service.UserService" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.github.jdubois.responses.service.InstanceService" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    String targetUrl = request.getParameter("targetUrl");
    if (targetUrl != null) {
        session.setAttribute("targetUrl", URLEncoder.encode(targetUrl, "UTF-8"));
    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses, site communautaire d'entraide et de partage de la connaissance technique</title>
    <meta name="keywords" content="com.github.jdubois.responses, com.github.jdubois.responses.net, questions, réponses, communauté, partage, Java, questions techniques"/>
    <meta name="description" content="Responses est un site communautaire d'entraide et de partage de la connaissance technique."/>
    <jsp:include page="WEB-INF/fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div>
<div id="container">
    <jsp:include page="WEB-INF/fragments/header.jsp"/>
    <a class="prevPage browse left"></a>

    <a class="nextPage browse right"></a>
    <div id="content">
        <h1><a href="${context}/i/developpement/" style="color: #bc0400;">Acc&eacute;dez &agrave; notre site d&eacute;di&eacute; au d&eacute;veloppement informatique</a></h1>
        <table>
            <tr>
                <td style="width: 550px; vertical-align: top;">
                    <h2>Authentification</h2>
                    <sec:authorize ifNotGranted="ROLE_USER">
                        <div class="border">
                            <jsp:include page="WEB-INF/views/authentication/authentication.jsp"/>
                        </div>
                        <br/><br/>
                    </sec:authorize>
                    <sec:authorize ifAnyGranted="ROLE_USER">
                        <p>
                            Vous &ecirc;tes authentifi&eacute; avec l'e-mail
                            <b><sec:authentication property="principal.username"/></b>
                            (<a href="${context}/logout">D&eacute;connexion</a>).
                        </p>
                    </sec:authorize>
                </td>
                <td style="vertical-align: top; padding-left: 10px;">
                    <sec:authorize ifAnyGranted="ROLE_USER">
                        <%
                            ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
                            User user = applicationContext.getBean(UserService.class).getCurrentUser();
                            request.setAttribute("instances", user.getInstances());
                        %>
                        <c:if test="${fn:length(instances) gt 0}">
                            <h2>Instances priv&eacute;es</h2>
                            <ul>
                                <c:forEach var="instance" items="${instances}">
                                    <li><a href="${context}/i/${instance.name}/">${instance.longName}</a></li>
                                </c:forEach>
                            </ul>
                        </c:if>
                    </sec:authorize>
                    <sec:authorize ifAnyGranted="ROLE_MODERATOR,ROLE_SUPPORT">
                        <h2>Centre de support</h2>
                        <p>Vous &ecirc;tes authentifi&eacute; en tant que membre de l'&eacute;quipe de support.</p>
                        <ul>
                            <li><a href="${context}/support/">Centre de support</a></li>
                        </ul>
                    </sec:authorize>
                    <sec:authorize ifAnyGranted="ROLE_SU">
                        <h2>Super utilisateur</h2>
                        <p>Vous &ecirc;tes authentifi&eacute; en tant que <b>super utilisateur</b>!!</p>
                        <ul>
                            <li><a href="${context}/su/">Super User Dashboard</a></li>
                        </ul>
                        <h3>Liste des instances</h3>
                        <ul>
                            <%
                            ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
                            InstanceService instanceService = applicationContext.getBean(InstanceService.class);
                            request.setAttribute("allInstances", instanceService.getAllInstances());
                            %>
                            <c:forEach var="instance" items="${allInstances}">
                                <li><a href="${context}/i/${instance.name}/">${instance.longName}</a></li>
                            </c:forEach>
                        </ul>
                    </sec:authorize>
                    <h2>Responses</h2>
                    <table>
                        <tr>
                            <td><img src="${staticContent}/style/responses_logo_rounded.gif" width="82" height="95" alt="Logo"/></td>
                            <td>
                                <p>
                                    Responses est un logiciel d&eacute;velopp&eacute; par Julien Dubois
                                    <a href="http://www.julien-dubois.com">http://www.julien-dubois.com</a>.
                                </p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
    <jsp:include page="WEB-INF/fragments/footer.jsp"/>
    <script type="text/javascript" src="${staticContent}/jquery/tools/jquery.tools.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            <c:if test="${message != null}">
                showMessage("${message}");
            </c:if>
            <c:if test="${error != null}">
                showError("${error}");
            </c:if>
        });
    </script>
</div>
</body>
</html>
