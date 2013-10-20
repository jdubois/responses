<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
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
        <h1>Une erreur de s&eacute;curit&eacute; s'est produite</h1>
        <p>
            Vous n'avez pas les droit n&eacute;cessaires pour voir l'instance demand&eacute;e. Cette erreur
            de s&eacute;curit&eacute; a &eacute;t&eacute; enregistr&eacute;e et a lev&eacute; une alerte.
        </p>
        <p>
            <div style="text-align: center;">
                [ <a href="${context}/">Retour &agrave; la page principale de Responses</a> ]
            </div>
        </p>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
    <script type="text/javascript">
        $(document).ready(function() {
            window.location.href = "${context}";
        });
    </script>
</div>
</body>
</html>