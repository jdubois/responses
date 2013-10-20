<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Conditions g&eacute;n&eacute;rales d'utilisation</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <p style="text-align: center;font-style:italic;">Les conditions g&eacute;n&eacute;rales d'utilisation suivantes ont &eacute;t&eacute; valid&eacute;es par tous
        les utilisateurs enregistr&eacute;s sur le site Responses.</p>

        <jsp:include page="../authentication/registration_rules.jsp"/>

    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
</div>
</body>
</html>