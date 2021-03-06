<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - Nouveau compte cr&eacute;&eacute;</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>F&eacute;licitations! Votre compte Responses a &eacute;t&eacute; correctement cr&eacute;&eacute;.
        </h1>
        <br/><br/><br/>
        <p>
            <a href="${siteUrl}">Revenez &agrave; la page d'accueil</a> pour vous authentifier et poursuivre votre
            navigation.
        </p>
       <br/><br/><br/><br/><br/>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>

</div>
</body>
</html>