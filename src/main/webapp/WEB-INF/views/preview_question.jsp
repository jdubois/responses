<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - Pr&eacute;visualisation d'une question</title>
    <jsp:include page="../fragments/html_head.jsp"/>
    <link href="${staticContent}/syntaxhighlighter_2.1.364/styles/shAll.css" rel="stylesheet" type="text/css"/>
</head>
<body id="main">
<h1>Pr&eacute;visualisation d'une question</h1>
<div id="question-question">
    <div id="question-title"><h2>${title}</h2></div>
    <div id="question-text" class="display-html">
        ${text}
    </div>
</div>
<jsp:include page="../fragments/footer.jsp"/>
<br/>
<div style="padding-left: 350px">
    <a class="button" href="javascript:window.close();" onclick="this.blur();"><span>Fermer la fen&ecirc;tre</span></a>
</div>
<script type="text/javascript" src="${staticContent}/syntaxhighlighter_2.1.364/scripts/shBrushAll.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
        SyntaxHighlighter.all();
    });
</script>
</body>
</html>