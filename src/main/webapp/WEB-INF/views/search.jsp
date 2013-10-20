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
    <title>Responses - Recherche "${searchQuery}"</title>
    <jsp:include page="../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../fragments/header.jsp"/>
    <div id="content">
        <h1 style="width: 750px">R&eacute;sultats de la recherche "${searchQuery}"</h1>
        <div>
            <p style="margin-left: 60px">
            <br/>
            Tri des questions par :
            <select id="search_sort_by" onchange="searchQuestions()">
                <option value="0" <c:if test="${sort eq 0}">selected="selected"</c:if>>Pertinence</option>
                <option value="1" <c:if test="${sort eq 1}">selected="selected"</c:if>>Vote</option>
                <option value="2" <c:if test="${sort eq 2}">selected="selected"</c:if>>Date de cr&eacute;ation</option>
                <option value="3" <c:if test="${sort eq 3}">selected="selected"</c:if>>Date de mise à jour</option>
            </select> 
            &nbsp;&nbsp;&nbsp;&nbsp;
            Inclure les questions avec un vote inférieur à 0 :
                <input type="checkbox" id="search_showNegativeQuestions" value="" onchange="searchQuestions()"
                        <c:if test="${showNegativeQuestions}">checked="checked"</c:if>/>
            </p>
        </div>
        <div id="show_questions">
            <jsp:include page="questions.jsp"/>
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
                <img src="${staticContent}/style/link.png" height="16" width="16" style="top: 4px" alt="permlink"/>&nbsp;<a href="${baseUrl}/search?q=${searchQuery}">Lien permanent</a><br/>
                <br/>
                <jsp:include page="../fragments/tag.jsp"/>
            </div>
            <div class="box_footer"><span></span></div>
        </div>
    </div>
    <jsp:include page="../fragments/footer.jsp"/>
</div>
</body>
</html>