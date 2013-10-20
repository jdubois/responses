<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>User management</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>User management</h1>
        <table>
            <tr>
                <jsp:include page="su_menu.jsp"/>
                <td style="width: 800px" class="border">
                    <h2>Actions</h2>
                    <form method="post" action="">
                    <table>
                        <tr><th colspan="4">User enable/disable</th></tr>
                        <tr>
                            <td>User ID</td>
                            <td><input name="userId" size="10" value="0"/></td>
                            <td>
                                <select name="enable">
                                    <option value="0">disable</option>
                                    <option value="1">enable</option>
                                </select>
                            </td>
                            <td><button type="submit">Save</button></td>
                        </tr>
                    </table>
                    <h2>Users list</h2>
                    <table>
                        <tr>
                            <th>Id</th>
                            <th>Email</th>
                            <th>First Name</th>
                            <th>Last Name</th>
                            <th>Creation</th>
                            <th>Update</th>
                            <th>ok</th>
                        </tr>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>${user.id}</td>
                                <td>${user.email}</td>
                                <td>${user.firstName}</td>
                                <td>${user.lastName}</td>
                                <td><fmt:formatDate pattern="dd/MM/yyyy" value="${user.creationDate}" /></td>
                                <td><fmt:formatDate pattern="dd/MM/yyyy" value="${user.lastAccessDate}" /></td>
                                <td>${user.enabled}</td>
                            </tr>
                        </c:forEach>
                    </table>
                    <br/><br/>
                    <div style="text-align: center;"><a href="?index=${index + 100}">Next</a></div>
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