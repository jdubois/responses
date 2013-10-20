<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Responses - Centre de support</title>
    <jsp:include page="../../fragments/html_head.jsp"/>
</head>
<body id="main">
<div id="banner"></div><div id="container">
    <jsp:include page="../../fragments/header.jsp"/>
    <div id="content">
        <h1>Centre de support</h1>

        <p style="padding-left: 50px">
            <img src="${staticContent}/style/help.png" height="16" width="16"/>
            <a href="#" onclick="$('#workflow-help').toggle('blind', {}, 500);">Aide pour le Workflow</a>
            &nbsp;&nbsp;&nbsp;
            <img src="${staticContent}/style/page_refresh.png" height="16" width="16"/>
            <a href="#" onclick="reload();">Rafra&icirc;chir</a>

        <div id="workflow-help" style="padding-left: 150px; display: none;">
            <img src="${staticContent}/style/workflow_support.png" height="99" width="500"/>
        </div>
        </p>
        <form id="supportCenterForm" action="" method="get">
            <p>
            <table>
                <tr>
                    <th>Instance</th>
                    <th style="width: 450px">Question</th>
                    <th>Etat</th>
                    <th>Assign&eacute; &agrave;</th>
                    <th style="width:150px">Date</th>
                </tr>
                <tr>
                    <td>
                        <select name="selectedInstance" onchange="$('#supportCenterForm').submit()">
                            <option value="-1">Toutes</option>
                            <c:forEach var="instance" items="${instances}" varStatus="status">
                                <option
                                        <c:if test="${selectedInstance eq instance.id}">selected</c:if>
                                        value="${instance.id}">
                                    [${status.count}] ${instance.name}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td></td>
                    <td>
                        <select name="selectedState" onchange="$('#supportCenterForm').submit()">
                            <option value="-1">Tous</option>
                            <option
                                    <c:if test="${selectedState eq 1}">selected</c:if> value="1">[1]
                                Demand&eacute;</option>
                            <option
                                    <c:if test="${selectedState eq 2}">selected</c:if> value="2">[2]
                                Valid&eacute;</option>
                            <option
                                    <c:if test="${selectedState eq 3}">selected</c:if> value="3">[3]
                                Refus&eacute;</option>
                            <option
                                    <c:if test="${selectedState eq 4}">selected</c:if> value="4">[4]
                                Assign&eacute;</option>
                            <option
                                    <c:if test="${selectedState eq 5}">selected</c:if> value="5">[5] R&eacute;solu
                            </option>
                            <option
                                    <c:if test="${selectedState eq 6}">selected</c:if> value="6">[6]
                                Rejet&eacute;</option>
                            <option
                                    <c:if test="${selectedState eq 7}">selected</c:if> value="7">[7] Clos
                            </option>
                        </select>
                    </td>
                    <td>
                        <select name="selectedAssignedUser" onchange="$('#supportCenterForm').submit()">
                            <option value="-1">Tous</option>
                            <c:forEach var="expert" items="${experts}" varStatus="status">
                                <option
                                        <c:if test="${selectedAssignedUser eq expert.id}">selected</c:if>
                                        value="${expert.id}">
                                    [${status.count}] ${expert.firstName} ${expert.lastName}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td></td>
                </tr>

                <c:forEach var="question" items="${questions}" varStatus="i">
                    <c:choose>
                        <c:when test="${(i.count) % 2 == 0}">
                            <tr class="even">
                        </c:when>
                        <c:otherwise>
                            <tr class="odd">
                        </c:otherwise>
                    </c:choose>
                    <td>${question.instance.name}</td>
                    <td>
                        <a href="${context}/i/${question.instance.name}/q/${question.id}/${question.titleAsUrl}">${question.title}</a>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${question.wfState eq 1}">Demand&eacute;</c:when>
                            <c:when test="${question.wfState eq 2}">Valid&eacute;</c:when>
                            <c:when test="${question.wfState eq 3}">Refus&eacute;</c:when>
                            <c:when test="${question.wfState eq 4}">Assign&eacute;</c:when>
                            <c:when test="${question.wfState eq 5}">R&eacute;solu</c:when>
                            <c:when test="${question.wfState eq 6}">Rejet&eacute;</c:when>
                            <c:when test="${question.wfState eq 7}">Clos</c:when>
                        </c:choose>

                    </td>
                    <td>
                        <c:if test="${question.wfAssignedUser gt 0}">
                            <c:forEach var="expert" items="${experts}">
                                <c:if test="${question.wfAssignedUser eq expert.id}">${expert.firstName} ${expert.lastName}</c:if>
                            </c:forEach>
                        </c:if>
                    </td>
                    <td><fmt:formatDate pattern="yyyy/MM/dd hh:mm:ss" value="${question.wfDate}"/></td>
                    </tr>
                </c:forEach>
            </table>
            <br/>
            <br/>

            <div style="text-align: center;">
                <input type="hidden" name="selectedIndex" id="selectedIndex" value="${selectedIndex}"/>
                <c:if test="${selectedIndex gt 0}">
                    <a href="#" onclick="$('#selectedIndex').val('${selectedIndex - 50}');$('#supportCenterForm').submit()">[Questions Pr&eacute;c&eacute;dentes]</a>&nbsp;
                </c:if>
                <c:if test="${fn:length(questions) ge 50}">
                   &nbsp; <a href="#" onclick="$('#selectedIndex').val('${selectedIndex + 50}');$('#supportCenterForm').submit()">[Questions Suivantes]</a>
                </c:if>
            </div>
            </p>
        </form>
    </div>
    <jsp:include page="../../fragments/footer.jsp"/>
</div>
</body>
</html>
