<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Configuration de l'instance "${instance.name}"</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Configuration de l'instance "${instance.longName}"</h1>
        <table>
            <tr>
                <jsp:include page="admin_menu.jsp"/>
                <td style="width: 600px" class="border">
                    <form method="post" action="${baseUrl}/admin" id="instanceForm">
                        <table>
                            <tr>
                                <td>Nom long</td>
                                <td><input type="text" name="instanceLongName" size="50" maxlength="100" value="${instance.longName}"/></td>
                            </tr>
                            <tr>
                                <td>Description</td>
                                <td><textarea rows="8" cols="60" name="instanceDescription">${instance.description}</textarea></td>
                            </tr>
                        </table>
                        <div style="padding-left: 200px">
                            <a class="button" href="javascript:document.forms['instanceForm'].submit();" onclick="this.blur();"><span>Sauvegarder</span></a>
                        </div>
                    </form>
                </td>
            </tr>
        </table>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
    <script type="text/javascript">
        <c:if test="${message != null}">
            showMessage("${message}");
        </c:if>
        <c:if test="${error != null}">
            showError("${error}");
        </c:if>
    </script>
</div>
</body>
</html>