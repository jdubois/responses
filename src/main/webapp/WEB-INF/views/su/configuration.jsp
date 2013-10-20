<%@ page import="com.github.jdubois.responses.service.impl.ConfigurationServiceImpl" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Configuration</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Configuration</h1>
        <table>
            <tr>
                <jsp:include page="su_menu.jsp"/>
                <td style="width: 700px" class="border">
                    <h2>Actions</h2>

                    <form method="post" action="">
                        <input type="hidden" name="action" value="rebuildLuceneIndex"/>
                        <button type="submit">Rebuild the Lucene Index</button>
                    </form>
                    <br/><br/>
                    <form method="post" action="">
                        <input type="hidden" name="action" value="cleanUpApplication"/>
                        <button type="submit">Clean up the application</button>
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