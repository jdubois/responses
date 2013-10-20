<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>G&eacute;rer les acc&egrave;s</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>G&eacute;rer les acc&egrave;s &agrave; l'instance "${instance.longName}"</h1>
        <form method="post" action="${baseUrl}/admin/manageInstance" id="manageInstanceForm">
        <input type="hidden" id="action" name="action"/>
        <input type="hidden" id="userIds" name="userIds"/>
        </form>
        <table>
            <tr>
                <jsp:include page="admin_menu.jsp"/>
                <td style="vertical-align: top;" class="border">
                    <p><b>Utilisateurs disponibles</b></p>
                    <select name="availableUsers" id="availableUsers" size="32" multiple="true" style="width: 280px;" class="bodyFont">
                        <c:forEach var="user" items="${availableUsers}">
                             <option value="${user.id}">${user.firstName} ${user.lastName}</option>
                        </c:forEach>
                    </select>
                </td>
                <td style="width: 140px">
                    <a class="button" href="javascript:manageInstanceAddAll();" onclick="this.blur();"><span style="width: 120px;">&gt;&gt; Tout s&eacute;lectionner</span></a><br/><br/>
                    <a class="button" href="javascript:manageInstanceAddSelected();" onclick="this.blur();"><span style="width: 120px;">&gt; S&eacute;lectionner</span></a><br/><br/>
                    <a class="button" href="javascript:manageInstanceRemoveSelected();" onclick="this.blur();"><span style="width: 120px;">&lt; Supprimer</span></a><br/><br/>
                    <a class="button" href="javascript:manageInstanceRemoveAll();" onclick="this.blur();"><span style="width: 120px;">&lt;&lt; Tout supprimer</span></a><br/><br/>
                </td>
                <td style="vertical-align: top;" class="border">
                    <p><b>Utilisateurs ayant acc&egrave;s &agrave; l'instance</b></p>
                    <select name="selectedUsers" id="selectedUsers" size="32" multiple="true" style="width: 280px;" class="bodyFont">
                        <c:forEach var="user" items="${selectedUsers}">
                             <option value="${user.id}">${user.firstName} ${user.lastName}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
        </table>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
    <script type="text/javascript">
        $(document).ready(function() {
            <c:if test="${message != null}">
                showMessage("${message}");
            </c:if>
            <c:if test="${error != null}">
                showError("${error}");
            </c:if>
        });
        function manageInstanceAddAll() {
            $("#action").val("add");
            var userIds = "";
            $("#availableUsers option").each(function () {
                userIds += $(this).val() + " ";
            });
            $("#userIds").val(userIds);
            if (userIds != "") {
                document.forms["manageInstanceForm"].submit();
            }
        }
        function manageInstanceAddSelected() {
            $("#action").val("add");
            var userIds = "";
            $("#availableUsers option:selected").each(function () {
                userIds += $(this).val() + " ";
            });
            $("#userIds").val(userIds);
            if (userIds != "") {
                document.forms["manageInstanceForm"].submit();
            }
        }
        function manageInstanceRemoveSelected() {
            $("#action").val("remove");
            var userIds = "";
            $("#selectedUsers option:selected").each(function () {
                userIds += $(this).val() + " ";
            });
            $("#userIds").val(userIds);
            if (userIds != "") {
                document.forms["manageInstanceForm"].submit();
            }
        }
        function manageInstanceRemoveAll() {
            $("#action").val("remove");
            var userIds = "";
            $("#selectedUsers option").each(function () {
                userIds += $(this).val() + " ";
            });
            $("#userIds").val(userIds);
            if (userIds != "") {
                document.forms["manageInstanceForm"].submit();
            }
        }
    </script>

</div>
</body>
</html>