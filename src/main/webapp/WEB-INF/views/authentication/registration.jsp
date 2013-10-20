<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - Nouveau compte</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Nouveau compte</h1>
        <p>La cr&eacute;tion d'un compte sur com.github.jdubois.responses.net est enti&egrave;rement gratuite. Nous nous engageons &agrave; respecter votre vie priv&eacute;e,
        et n'utiliserons pas votre adresse e-mail pour vous spammer ou la revendre &agrave; des tiers.</p>
        <h2>Donn&eacute;es personnelles</h2>
        <form id="registrationForm" action="registration" method="post">
            <fieldset style="width:700px;margin: auto">
                <table style="width:700px">
                    <tr>
                        <td style="width:200px"><label for="email">E-mail</label>
                            <em>*</em></td>
                        <td><input id="email" name="email" size="20" maxlength="255" class="required email"
                                   value="${email}"/></td>
                    </tr>
                    <tr>
                        <td><label for="firstName">Pr&eacute;nom</label>
                            <em>*</em></td>
                        <td><input id="firstName" name="firstName" size="20" maxlength="255" class="required"
                                   minlength="2" value="${firstName}"/></td>
                    </tr>
                    <tr>
                        <td><label for="lastName">Nom de famille</label>
                            <em>*</em></td>
                        <td><input id="lastName" name="lastName" size="20" maxlength="255" class="required"
                                   minlength="2" value="${lastName}"/></td>
                    </tr>
                    <tr>
                        <td>Conditions d'utilisation <em>*</em></td>
                        <td>
                            <a href="#" onclick="$('#registration-rules').toggle('blind', {}, 500);">Voir les conditions d'utilisation</a>
                            <div id="registration-rules" style="width: 500px;margin:auto;background-color: #d7d7ff;display: none;">
                                <jsp:include page="registration_rules.jsp"/>
                            </div>
                            <p class="border">
                            <input type="checkbox" name="rulesOk" value="1"/> J'accepte les conditions d'utilisation
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Image de v&eacute;rification <em>*</em>
                        </td>
                        <td>
                            <c:out value="${requestScope.captcha}" escapeXml="false"/>
                        </td>
                    </tr>
                </table>
                <br/><br/>
                <p><i>Information: </i> votre mot de passe sera envoy&eacute; &agrave; l'adresse e-mail que vous avez renseign&eacute;e.</p>
                <br/>
                <br/>
                <p style="margin-left: 250px">
                    <a class="button" href="javascript:submitForm();"
                       onclick="this.blur();"><span>Cr&eacute;er le compte</span></a>
                </p>
            </fieldset>

        </form>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
    <script type="text/javascript" src="${staticContent}/jquery/validate-1.7/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${staticContent}/jquery/validate-1.7/messages_fr.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#registrationForm").validate({});
        <c:if test="${errorRecaptcha != null}">
            showError("Le texte de l'image n'&eacute;tait pas correct.");
        </c:if>
        <c:if test="${errorUserAlreadyExists != null}">
            showError("Ce login est d&eacute;j&agrave; utilis&eacute;.");
        </c:if>
        <c:if test="${error != null}">
            showError("${error}");
        </c:if>
        <c:if test="${rulesNotOk != null}">
            showError("Vous n'avez pas accept&eacute; les conditions d'utilisation de Responses.");
        </c:if>
        });
        function submitForm() {
            if ($("#registrationForm").valid()) {
                document.forms["registrationForm"].submit();
            }
        }
    </script>

</div>
</body>
</html>