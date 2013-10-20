<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - R&eacute;pondre &agrave; une question.</title>
    <jsp:include page="../fragments/html_head.jsp"/>
    <link href="${staticContent}/syntaxhighlighter_2.1.364/styles/shAll.css" rel="stylesheet" type="text/css" />
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../fragments/header.jsp"/>
    <div id="content">
        <form id="answerQuestionForm" method="post">
            <div id="answer_question">
                <sec:authorize ifAnyGranted="ROLE_USER">
                    <div id="answerPanel">
                        <h1>${question.title}</h1>
                        <div style="padding-left: 80px">
                            <div id="question-text" class="display-html">
                                ${question.text}
                            </div>
                        </div>
                        <h2>Votre r&eacute;ponse</h2>
                        <div style="padding-left: 80px">
                            <input type="hidden" name="questionIdPost" value="${questionId}"/>
                            <textarea rows="15" cols="83" id="answerText" name="answerText" class="tinymce" style="width: 720px;height:300px">${answerText}</textarea>
                            <i>Astuce: </i> Utilisez l'ic&ocirc;ne <img src="${staticContent}/jquery/tinymce-3.3.2/plugins/codehighlighting/img/codehighlight.gif" height="16" width="16" alt="src"/> pour ins&eacute;rer du code (Java, etc.).<br/>
                            <br/>

                            <div style="margin-left: 200px">
                                <a class="button" href="javascript:previewAnswer();"
                                   onclick="this.blur();"><span>Pr&eacute;visualiser la r&eacute;ponse</span></a>
                                <a class="button" href="javascript:answerQuestion('${questionId}');"
                                   onclick="this.blur();"><span>R&eacute;pondre</span></a>
                                <a class="button" href="${baseUrl}/q/${question.id}/${question.titleAsUrl}"
                                   onclick="this.blur();"><span>Annuler</span></a>
                            </div>
                        </div>
                        <br/>
                    </div>
                </sec:authorize>
                <sec:authorize ifNotGranted="ROLE_USER">
                    <h1>Vous devez &ecirc;tre <a href="javascript:authentication();">authentifi&eacute;</a>
                        pour pouvoir r&eacute;pondre &agrave; des questions</h1>
                </sec:authorize>
            </div>
        </form>
        <div style="display: none;">
            <form action="${baseUrl}/answer/preview" id="previewAnswerForm"
                  name="previewAnswerForm" method="post" target="preview">
                <textarea name="previewText" id="previewText"></textarea>
            </form>
        </div>
    </div>
    <jsp:include page="../fragments/footer.jsp"/>
    <script type="text/javascript" src="${staticContent}/syntaxhighlighter_2.1.364/scripts/shBrushAll.js"></script>
    <script type="text/javascript" src="${context}/static/jquery/tinymce-3.3.2/jquery.tinymce.js"></script>
    <script type="text/javascript">
        $().ready(function() {
            SyntaxHighlighter.all();
            <jsp:include page="../fragments/tinymce.jsp"/>
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
