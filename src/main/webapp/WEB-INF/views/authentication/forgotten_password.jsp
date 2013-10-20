<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - Mot de passe oubli&eacute;</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Mot de passe oubli&eacute;</h1>

        <h2>Envoi de votre mot de passe par e-mail</h2>
        <form id="forgottenPasswordForm" action="forgotten_password" method="post">
            <fieldset style="width:470px;margin: auto">
                <table style="width:470px">
                        <td><label for="email">E-mail</label>
                            <em>*</em></td>
                        <td><input id="email" name="email" size="20" maxlength="255" class="required email"
                                   value=""/></td>
                    </tr>
                </table>
                <br/>
                <br/>

                <p>
                    <a style="padding-left: 130px" class="button" href="javascript:submitForgottenPasswordForm();"
                       onclick="this.blur();"><span>Envoyer</span></a>
                </p>
            </fieldset>

        </form>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
    <script type="text/javascript" src="${staticContent}/jquery/validate-1.7/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${staticContent}/jquery/validate-1.7/messages_fr.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#forgottenPasswordForm").validate();
            <c:if test="${message != null}">
                showMessage("${message}");
            </c:if>
            <c:if test="${error != null}">
                showError("${error}");
            </c:if>
        });
        function submitForgottenPasswordForm() {
            if ($("#forgottenPasswordForm").valid()) {
                document.forms["forgottenPasswordForm"].submit();
            }
          }
    </script>
</div>
</body>
</html>