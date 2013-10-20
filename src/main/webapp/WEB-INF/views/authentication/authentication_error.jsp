<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - Erreur d'authentification</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
    <sec:authorize ifAnyGranted="ROLE_USER">
        <c:if test="${sessionScope.authenticationReferer != null}">
            <script type="text/javascript">
                window.location.href = '${sessionScope.authenticationReferer}';
                <c:remove var="authenticationReferer" scope="session"/>
            </script>
        </c:if>
    </sec:authorize>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Erreur d'authentification</h1>
        <br/>

        <p style="padding-left: 330px;font: 18px arial, sans-serif;">
            Votre authentification a &eacute;chou&eacute;!

        <h2>Vous avez oubli&eacute; votre mot de passe?</h2>

        <p><a href="forgotten_password">cliquez ici</a> pour le r&eacute;cup&eacute;rer.</p>

        <h2>Vous voulez essayer &agrave; nouveau de vous authentifier?</h2>
        <sec:authorize ifNotGranted="ROLE_USER">
            <div class="border" style="width: 650px;margin: auto">
                <jsp:include page="../authentication/authentication.jsp"/>
            </div>
            <br/><br/>
        </sec:authorize>
        </p>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
</div>
</body>
</html>