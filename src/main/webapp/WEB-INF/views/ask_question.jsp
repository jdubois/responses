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
    <form id="askQuestionForm" method="post">
        <div id="ask_question_1" class="ask_question">
            <sec:authorize ifAnyGranted="ROLE_USER">
                <div id="askPanel">
                    <h1>Posez votre question (&eacute;tape 1/2)</h1>
                    Titre:
                    <input id="questionTitle" type="text" name="questionTitle"
                           value="${questionTitle}"
                           style="width: 630px;"
                           maxlength="140"/>
                    <br/><br/>
                    Description:<br/>
                    <textarea rows="15" cols="83" id="questionText" name="questionText" class="tinymce" style="width: 720px;height:300px">${questionText}</textarea>
                    <i>Astuce: </i> Utilisez l'ic&ocirc;ne <img src="${staticContent}/jquery/tinymce-3.3.2/plugins/codehighlighting/img/codehighlight.gif" height="16" width="16" alt="src"/> pour ins&eacute;rer du code (Java, etc.).<br/>
                    <br/>

                    <div style="margin-left: 250px">
                        <a class="button" href="javascript:askQuestionStep2();"
                           onclick="this.blur();"><span>Etape suivante</span></a>
                        <a class="button" href="javascript:cancelQuestion();"
                           onclick="this.blur();"><span>Annuler</span></a>
                    </div>
                    <br/><br/>
                </div>
            </sec:authorize>
            <sec:authorize ifNotGranted="ROLE_USER">
                <h1>Vous devez &ecirc;tre <a href="javascript:authentication();">authentifi&eacute;</a>
                    pour pouvoir poser des questions</h1>
            </sec:authorize>
        </div>
        <div id="ask_question_2" class="ask_question" style="display:none;">
            <h1>Ajoutez des &eacute;tiquettes &aacute; votre question (&eacute;tape 2/2)</h1>

            <p>Les &eacute;tiquettes permettent aux autres utilisateurs de trouver plus facilement votre question, il
                est donc important de bien les utiliser.</p><br/>

            <p id="ask_tags_tip"><b>Attention!</b> Vous n'avez aucune &eacute;tiquette de choisie pour l'instant. Veuillez en
            choisir une dans la liste ci-dessous, et l'ajouter en cliquant sur le bouton <img src="${staticContent}/style/add.png" class="tag_add_img" onclick="addAskTag()"/><br/><br/></p>
            <p id="ask_tags"></p>
            <div id="add_ask_tag_div">
                <input type="hidden" id="askTags" name="askTags" value="${askTags}"/>
                Nouvelle &eacute;tiquette:<br/>

                    <input id="add_ask_tag" name="add_ask_tag" maxlength="20" class="tag_auto_complete"/>
                    <img src="${staticContent}/style/add.png" class="tag_add_img" onclick="addAskTag()"/>
            </div>

            <br/><br/><br/><br/>

            <div style="margin-left: 100px">
                <a class="button" href="javascript:askQuestionStep1();" onclick="this.blur();"><span>Etape pr&eacute;c&eacute;dente</span></a>
                <a class="button" href="javascript:previewQuestion();"
                   onclick="this.blur();"><span>Pr&eacute;visualiser la question</span></a>
                <a class="button" href="javascript:askQuestionSubmit();"
                   onclick="this.blur();"><span>Poser la question</span></a>
                <a class="button" href="javascript:cancelQuestion();" onclick="this.blur();"><span>Annuler</span></a>
            </div>
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