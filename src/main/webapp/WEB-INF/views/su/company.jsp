<%@ page import="com.github.jdubois.responses.service.impl.ConfigurationServiceImpl" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Companies management</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Companies management</h1>
        <table>
            <tr>
                <jsp:include page="su_menu.jsp"/>
                <td style="width: 600px" class="border">

                    <p>Company : <b>${company.name}</b></p>

                    <h2>Nouvelle Instance</h2>

                    <form method="post" action="instance">
                        <input type="hidden" name="companyId" value="${company.id}"/>
                        <input type="hidden" name="action" value="add"/>
                        Nom court : <input name="newInstanceName"
                                           value="">
                        Nom Long : <input name="newInstanceLongName"
                                          value="">
                        <button type="submit">Ajouter</button>
                    </form>
                    <h2>Instance existantes</h2>
                    <table>
                        <tr>
                            <th>ID</th>
                            <th>Nom</th>
                            <th>Nom long</th>
                            <th>Url</th>
                            <th>Action</th>
                        </tr>
                        <c:forEach var="instance" items="${company.instances}">
                            <tr>
                                <form method="post" action="instance">
                                    <input type="hidden" name="companyId" value="${company.id}"/>
                                    <input type="hidden" name="instanceId" value="${instance.id}"/>
                                    <input type="hidden" name="action" value="edit"/>
                                    <td>${instance.id}</td>
                                    <td>${instance.name}</td>
                                    <td>${instance.longName}</td>
                                    <td>
                                        <a href="<%=ConfigurationServiceImpl.siteUrl%>/i/${instance.name}">
                                            <%=ConfigurationServiceImpl.siteUrl%>/i/${instance.name}
                                        </a>
                                    </td>
                                    <td>
                                        <a href="javascript:if (confirm('Are you sure?')){window.location.href='instance?action=delete&companyId=${company.id}&instanceId=${instance.id}'}">Effacer</a>
                                    </td>
                                </form>
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