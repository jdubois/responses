<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - Mon compte</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Mon compte</h1>

        <h2>Modification des donn&eacute;es personnelles</h2>

        <form id="accountForm" action="account" method="post">
            <fieldset style="width:650px;margin: auto">
                <table style="width:650px">
                    <tr>
                        <td style="width:180px"><label for="firstName">Pr&eacute;nom</label>
                            <em>*</em></td>
                        <td colspan="2"><input id="firstName" name="firstName" size="20" maxlength="255" class="required"
                                   minlength="2" value="${user.firstName}"/></td>
                    </tr>
                    <tr>
                        <td><label for="lastName">Nom de famille</label>
                            <em>*</em></td>
                        <td colspan="2"><input id="lastName" name="lastName" size="20" maxlength="255" class="required"
                                   minlength="2" value="${user.lastName}"/></td>
                    </tr>
                    <tr>
                        <td><label for="email">E-mail</label>
                            <em>*</em></td>
                        <td colspan="2"><input id="email" name="email" size="20" maxlength="255" class="required email"
                                   value="${user.email}"/></td>
                    </tr>
                    <tr>
                        <td>Photo</td>
                        <td><img src="http://www.gravatar.com/avatar/${user.gravatarUrl}.jpg" id="gravatar" height="80" width="80" alt="gravatar"/></td>
                        <td>Votre photo est fournie par le site <a href="http://www.gravatar.com">http://www.gravatar.com</a> en fonction de votre adresse e-mail.</td>
                    </tr>
                    <tr>
                        <td><label for="website">Site Internet</label></td>
                        <td><input id="website" name="website" size="20" maxlength="100" class=""
                                   value="${user.website}"/></td>
                        <td>ex.: http://www.julien-dubois.com</td>
                    </tr>
                    <tr>
                        <td><label for="blog">Blog</label></td>
                        <td><input id="blog" name="blog" size="20" maxlength="100" class=""
                                   value="${user.blog}"/></td>
                        <td>ex.: http://www.julien-dubois.com/blog</td>
                    </tr>
                    <tr>
                        <td><label for="twitter">Compte Twitter</label></td>
                        <td><input id="twitter" name="twitter" size="20" maxlength="100" class=""
                                   value="${user.twitter}"/></td>
                        <td>ex.: juliendubois</td>
                    </tr>
                    <tr>
                        <td><label for="linkedIn">Profil professionnel</label></td>
                        <td><input id="linkedIn" name="linkedIn" size="20" maxlength="100" class=""
                                   value="${user.linkedIn}"/></td>
                        <td>ex.: http://fr.linkedin.com/in/juliendubois</td>
                    </tr>
                </table>
                <br/>
                <br/>

                <p>
                    <a style="padding-left: 250px" class="button" href="javascript:submitAccountForm();"
                       onclick="this.blur();"><span>Sauvegarder</span></a>
                </p>
            </fieldset>

        </form>

        <h2>Changement de mot de passe</h2>

        <form id="passwordForm" action="account" method="post">
            <fieldset style="width:650px;margin: auto">
                <table style="width:650px">
                    <tr>
                        <td style="width:180px"><label for="password">Mot de passe</label> <em>*</em></td>
                        <td><input id="password" type="password" name="password" size="20" maxlength="255"
                                   class="required"
                                   minlength="5"/></td>
                    </tr>
                    <tr>
                        <td><label for="password2">V&eacute;rification du mot de passe</label>
                            <em>*</em></td>
                        <td><input id="password2" type="password" name="password2" size="20" maxlength="255"
                                   class="required"
                                   minlength="5"/></td>
                    </tr>
                </table>
                <br/>
                <br/>

                <p>
                    <a style="padding-left: 250px" class="button" href="javascript:submitPasswordForm();"
                       onclick="this.blur();"><span>Sauvegarder</span></a>
                </p>
            </fieldset>

        </form>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
    <script type="text/javascript" src="${staticContent}/jquery/validate-1.7/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${staticContent}/jquery/validate-1.7/messages_fr.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#accountForm").validate();
            $("#passwordForm").validate({
                rules: {
                    password2: {
                        equalTo: "#password"
                    }
                }
            });
            <c:if test="${message != null}">
                showMessage("${message}");
            </c:if>
            <c:if test="${error != null}">
                showError("${error}");
            </c:if>
        });
        function submitAccountForm() {
            if ($("#accountForm").valid()) {
                document.forms["accountForm"].submit();
            }
        }
        function submitPasswordForm() {
            if ($("#passwordForm").valid()) {
                document.forms["passwordForm"].submit();
            }
        }
    </script>

</div>
</body>
</html>