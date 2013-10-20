<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Question inexistante!</h1>
        <p style="text-align: center;">
            La question que vous avez demand&eacute;e n'existe pas.
            <br/>
            <br/>
            [ <a href="${context}/">Retour &agrave; la page principale de Responses</a> ]
        </p>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
</div>
</body>
</html>