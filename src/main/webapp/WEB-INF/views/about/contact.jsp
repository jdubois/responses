<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Contactez Responses</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Contactez Responses</h1>
        <c:if test="${abuse eq 1}">
            <h2>Vous signalez un abus</h2>
        </c:if>
        <form id="contactForm" action="contact" method="post">

            <fieldset style="width:700px;margin: auto">
                <table style="width:700px">
                    <tr>
                        <td style="width:200px"><label for="email">Votre E-mail</label>
                            <em>*</em></td>
                        <td><input id="email" name="email" size="20" maxlength="255" class="required email"
                                   value="${email}"/></td>
                    </tr>
                    <c:if test="${abuse ne 1}">
                        <input type="hidden" name="abuse" value="0"/>
                    </c:if>
                    <c:if test="${abuse eq 1}">
                        <input type="hidden" name="abuse" value="${abuse}"/>
                        <tr>
                            <td><label for="abuseUrl">Page du site</label></td>
                            <td><a href="${abuseUrl}">${abuseUrl}</a><input type="hidden" id="abuseUrl" name="abuseUrl" value="${abuseUrl}"/></td>
                        </tr>
                    </c:if>
                    <c:if test="${questionId ne null}">
                        <tr>
                            <td>Identifiant de la question</td>
                            <td>${questionId}<input type="hidden" id="questionId" name="questionId" value="${questionId}"/></td>
                        </tr>
                    </c:if>
                    <c:if test="${answerId ne null}">
                        <tr>
                            <td>Identifiant de la r&eacute;ponse</td>
                            <td>${answerId}<input type="hidden" id="answerId" name="answerId" value="${answerId}"/></td>
                        </tr>
                    </c:if>
                    <tr>
                        <td><label for="subject">Objet de votre message</label></td>
                        <td><input id="subject" name="subject" size="40" maxlength="255" class="required"/></td>
                    </tr>
                </table>
                <br/>
                Votre Message<br/>
                <textarea rows="15" cols="76" id="messageText" name="messageText" class="required"></textarea>
                <br/>
                <p style="margin-left: 250px">
                    <a class="button" href="javascript:submitForm();"
                       onclick="this.blur();"><span>Envoyer le message</span></a>
                </p>
            </fieldset>

        </form>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
    <script type="text/javascript" src="${staticContent}/jquery/validate-1.7/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${staticContent}/jquery/validate-1.7/messages_fr.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#contactForm").validate({});
            <c:if test="${message != null}">
                showMessage("${message}");
            </c:if>
            <c:if test="${error != null}">
                showError("${error}");
            </c:if>
         });
        function submitForm() {
            if ($("#contactForm").valid()) {
                document.forms["contactForm"].submit();
            }
        }
    </script>
</div>
</body>
</html>