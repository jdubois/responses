<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Configuration de la soci&eacute;t&eacute; "${company.name}"</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Configuration de la soci&eacute;t&eacute; "${company.name}"</h1>
        <table>
            <tr>
                <jsp:include page="admin_menu.jsp"/>
                <td style="width: 600px" class="border">
                    
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