<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - Profil de ${userProfile.firstName} ${userProfile.lastName}</title>
    <jsp:include page="../fragments/html_head.jsp"/>
    <c:if test="${instance.type eq 1}">
      <meta name="keywords" content="${userProfile.firstName} ${userProfile.lastName}">
      <meta name="description" content="Profil de ${userProfile.firstName} ${userProfile.lastName}">
    </c:if>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../fragments/header.jsp"/>
    <div id="content">
        <h1>Profil de ${userProfile.firstName} ${userProfile.lastName}</h1>
        <img src="http://www.gravatar.com/avatar/${userProfile.gravatarUrl}.jpg" id="gravatar" alt="gravatar" height="80" width="80" style="padding-left: 420px"/>
        <p style="width:650px;margin: auto">
            <b>Utilisateur depuis le <fmt:formatDate pattern="dd/MM/yyyy" value="${userProfile.creationDate}" /></b><br/>
            Dernier acc&egrave;s le <fmt:formatDate pattern="dd/MM/yyyy" value="${userProfile.lastAccessDate}" />
        </p>
        <table style="width:650px;margin: auto; padding-top: 30px;">
            <tr>
                <td><img src="${staticContent}/style/world.png" class="icon" alt=""/>&nbsp;Site Internet: </td><td><a href="${userProfile.website}" rel="nofollow">${userProfile.website}</a></td>
                <td><img src="${staticContent}/style/feed.png" class="icon" alt=""/>&nbsp;Blog: </td><td><a href="${userProfile.blog}" rel="nofollow">${userProfile.blog}</a></td>
            </tr>
            <tr>
                <td><img src="${staticContent}/style/twitter.png" class="icon" alt=""/>&nbsp;Twitter: </td><td><a href="http://twitter.com/${userProfile.twitter}" rel="nofollow">${userProfile.twitter}</a></td>
                <td><img src="${staticContent}/style/linkedin.png" class="icon" alt=""/>&nbsp;Profil professionnel: </td><td><a href="${userProfile.linkedIn}" rel="nofollow">${userProfile.linkedIn}</a></td>
            </tr>
        </table>
        <h2>Expertise</h2>
        <p style="width:650px;margin: auto">
        L'expertise est gagn&eacute;e lorsqu'une r&eacute;ponse est jug&eacute;e pertinente. Par exemple, un vote
        positif sur une r&eacute;ponse fait gagner 1 point d'expérience sur chacune des &eacute;tiquettes
        associ&eacute;es &agrave; la question. De m&ecirc;me, une r&eacute;ponse s&eacute;lectionn&eacute;e comme
        &eacute;tant la meilleure fait gagner 5 points.<br/>
        Inversement, des votres n&eacute;gatifs font perdre des points.
            <br/><br/><br/>
        </p>
        <table style="width:200px;margin: auto">
            <tr>
                <th>Etiquette</th>
                <th>Points</th>
            </tr>
            <c:forEach var="item" items="${expertizeList}">
                <tr>
                    <td><a href="${baseUrl}/tagged/${item.name}"><span
                            class=questionTag>${item.name}</span></a></td>
                    <td style="text-align: center;">${item.value}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
    <jsp:include page="../fragments/footer.jsp"/>
</div>
</body>
</html>
