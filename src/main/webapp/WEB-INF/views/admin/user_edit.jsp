<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Editer un utilisateur</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Editer un utilisateur</h1>
        <table>
            <tr>
                <jsp:include page="admin_menu.jsp"/>
                <td style="width: 290px; vertical-align: top;" class="border">
                    <p><b>Utilisateurs disponibles</b></p>
                    <select name="selectedUsers" id="selectedUsers" size="32" multiple="true" style="width: 280px;" class="bodyFont">
                        <c:forEach var="user" items="${users}">
                             <option onclick="editUser('${user.id}')" value="${user.id}">${user.firstName} ${user.lastName}</option>
                        </c:forEach>
                    </select>
                </td>
                <td style="width: 450px; vertical-align: top;" class="border">
                    <form method="post" action="${baseUrl}/admin/editUser" id="editUserForm">
                        <input type="hidden" name="userId" id="userId"/>
                        <table style="width:450px">
                            <tr><td colspan="2"><p><b>Utilisateur s&eacute;lectionn&eacute;</b></p></td></tr>
                            <tr>
                                <td style="width:120px"><label for="email">E-mail</label>
                                    <em>*</em></td>
                                <td><input id="email" name="email" size="20" maxlength="255" class="required email"/></td>
                            </tr>
                            <tr>
                                <td><label for="firstName">Pr&eacute;nom</label>
                                    <em>*</em></td>
                                <td><input id="firstName" name="firstName" size="20" maxlength="255" class="required"
                                           minlength="2"/></td>
                            </tr>
                            <tr>
                                <td><label for="lastName">Nom de famille</label>
                                    <em>*</em></td>
                                <td><input id="lastName" name="lastName" size="20" maxlength="255" class="required"
                                           minlength="2"/></td>
                            </tr>
                            <tr>
                                <td>Mod&eacute;rateur</td>
                                <td>
                                    <p class="border" style="width: 50px;">
                                        <input type="checkbox" name="isModerator" value="1"/> Oui
                                    </p>
                                </td>
                            </tr>
                            <tr>
                                <td>Expert support</td>
                                <td>
                                    <p class="border" style="width: 50px;">
                                        <input type="checkbox" name="isSupport" value="1"/> Oui
                                    </p>
                                </td>
                            </tr>
                            <tr>
                                <td>Administrateur</td>
                                <td>
                                    <p class="border" style="width: 50px;">
                                        <input type="checkbox" name="isAdmin" value="1"/> Oui
                                    </p>
                                </td>
                            </tr>
                            <tr>
                                <td>Actif</td>
                                <td>
                                    <table>
                                        <tr>
                                            <td>
                                                <p class="border">
                                                    <input type="radio" name="enabled" value="1"/> Actif
                                                </p>
                                            </td>
                                            <td>
                                                <p class="border">
                                                    <input type="radio" name="enabled" value="0"/> Inactif</p>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                        <br/>
                        <br/>
                        <div style="padding-left: 200px">
                            <a class="button" href="javascript:submitForm();" onclick="this.blur();"><span>Sauver</span></a>
                        </div>
                    </form>
                </td>
            </tr>
        </table>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
    <script type="text/javascript" src="${staticContent}/jquery/validate-1.7/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${staticContent}/jquery/validate-1.7/messages_fr.js"></script>
    <script type="text/javascript">
            $(document).ready(function() {
                $("#editUserForm").validate({});
                <c:if test="${message != null}">
                    showMessage("${message}");
                </c:if>
                <c:if test="${error != null}">
                    showError("${error}");
                </c:if>
            });
            function submitForm() {
                if ($("#userId").val() == "") {
                    showMessage("Vous devez s&eacute;lectionner un utilisateur.");
                } else {
                    if ($("#editUserForm").valid()) {
                        $.post("${baseUrl}/admin/editUser", $("#editUserForm").serialize(), function(data){
                            if (data.userDto != undefined) {
                                showMessage("Utilisateur mis &agrave; jour.");
                            } else {
                                if (data.error != undefined) {
                                    showError(data.error);
                                } else {
                                    showError("Une erreur technique s'est produite.");
                                }
                            }
                        }, "json");
                    }
                }
            }
        </script>

</div>
</body>
</html>