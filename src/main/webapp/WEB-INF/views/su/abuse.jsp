<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Abuse management</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div>
<div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Abuse management</h1>
        <table>
            <tr>
                <jsp:include page="su_menu.jsp"/>
                <td style="width: 800px" class="border">
                    <h2>
                         Delete
                    </h2>
                    <form method="post" action="">
                    <table>
                        <tr><th>Element</th><th>ID</th><th>Delete</th></tr>
                        <tr>
                            <td>Question</td>
                            <td><input name="questionId" size="10" value="0"/></td>
                            <td><button type="submit">Supprimer</button></td>
                        </tr>
                        <tr>
                            <td>Answer</td>
                            <td><input name="answerId" size="10" value="0"/></td>
                            <td><button type="submit">Supprimer</button></td>
                        </tr>
                        <tr>
                            <td>Question Comment</td>
                            <td><input name="questionCommentId" size="10" value="0"/></td>
                            <td><button type="submit">Supprimer</button></td>
                        </tr>
                        <tr>
                            <td>Answer Comment</td>
                            <td><input name="answerCommentId" size="10" value="0"/></td>
                            <td><button type="submit">Supprimer</button></td>
                        </tr>
                    </table>
                    </form>

                    <h2>Latest abuse requests</h2>
                    <table>
                        <tr>
                            <th>email</th>
                            <th>subject</th>
                            <th>message</th>
                            <th>questionId</th>
                            <th>answerId</th>
                            <th>url</th>
                        </tr>
                        <c:forEach var="abuse" items="${abuses}">
                            <tr>
                                <td>${abuse.email}</td>
                                <td>${abuse.subject}</td>
                                <td>${abuse.message}</td>
                                <td>${abuse.questionId}</td>
                                <td>${abuse.answerId}</td>
                                <td><a href="${abuse.url}">visit</a></td>
                            </tr>
                        </c:forEach>
                    </table>

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