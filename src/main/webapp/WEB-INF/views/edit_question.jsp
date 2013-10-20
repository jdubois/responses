<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - Nouvelle question</title>
    <meta name="description" content="Poser une nouvelle question sur com.github.jdubois.responses.net"/>
    <jsp:include page="../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../fragments/header.jsp"/>
    <div id="content">
    <form id="editQuestionForm" method="post">
        <div id="ask_question_1" class="ask_question">
            <sec:authorize ifAnyGranted="ROLE_USER">
                <div id="questionPanel">
                    <h1>Edition de la question</h1>
                    <input id="questionUrl" type="hidden" name="questionUrl" value="${baseUrl}/q/${question.id}/${question.titleAsUrl}"/>
                    Titre:
                    <input id="questionTitle" type="text" name="questionTitle"
                           value="${question.title}"
                           style="width: 630px;"
                           maxlength="140"/>
                    <br/><br/>
                    Description:<br/>
                    <textarea rows="15" cols="83" id="questionText" name="questionText" class="tinymce" style="width: 720px;height:300px">${questionText}</textarea>
                    <i>Astuce: </i> Utilisez l'ic&ocirc;ne <img src="${staticContent}/jquery/tinymce-3.3.2/plugins/codehighlighting/img/codehighlight.gif" height="16" width="16" alt="src"/> pour ins&eacute;rer du code (Java, etc.).<br/>
                    <br/>

                    <div style="margin-left: 150px">
                        <a class="button" href="javascript:previewQuestion();"
                            onclick="this.blur();"><span>Pr&eacute;visualiser la question</span></a>
                        <a class="button" href="javascript:doEditQuestion();"
                           onclick="this.blur();"><span>Sauvegarder</span></a>
                        <a class="button" href="javascript:cancelEditQuestion();"
                           onclick="this.blur();"><span>Annuler</span></a>
                    </div>
                    <br/><br/>
                </div>
            </sec:authorize>
        </div>
    </form>
    <div style="display: none;">
        <form action="${context}/i/${instance.name}/question/preview" id="previewQuestionForm"
              name="previewQuestionForm" method="post" target="preview">
            <input type="text" name="previewTitle" id="previewTitle"/>
            <textarea name="previewText" id="previewText"></textarea>
        </form>
    </div>
    </div>
    <jsp:include page="../fragments/footer.jsp"/>
    <script type="text/javascript" src="${context}/static/jquery/tinymce-3.3.2/jquery.tinymce.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            <jsp:include page="../fragments/tinymce.jsp"/>
            $("#add_ask_tag").autocomplete(baseUrl + "/tag/search", {
                width: 130,
                cacheLength: 50
            });
            var tagsArray = selectedTagsList.split("\+");
            if (tagsArray.length == 1) {
                selectedTag = tagsArray[0];
                if (selectedTag.length > 0) {
                    internalAddAskTag(selectedTag);
                    $("#ask_tags_tip").hide();
                }
            } else if (tagsArray.length > 1) {
                tagsArray.sort();
                for (key in tagsArray) {
                    internalAddAskTag(tagsArray[key]);
                }
                $("#ask_tags_tip").hide();
            }
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