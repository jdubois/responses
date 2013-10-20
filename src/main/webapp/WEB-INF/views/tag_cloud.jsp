<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.github.jdubois.responses.service.impl.ConfigurationServiceImpl" %>
<%@ page import="com.github.jdubois.responses.model.Instance" %>
<%
    String staticContent = ConfigurationServiceImpl.staticContent;
    request.setAttribute("staticContent", staticContent);
    Instance instance = (Instance) request.getAttribute("instance");
    if (instance != null) {
        request.setAttribute("baseUrl", request.getContextPath() + "/i/" + instance.getName());
    }
%>
<div id="tag-cloud">
    <h1>Les 100 &eacute;tiquettes les plus populaires</h1>
    <br/><br/>
    <table style="margin: auto">
        <c:forEach var="tag" items="${tags}" varStatus="status">
            <c:if test="${status.index % 3 == 0}">
                <tr>
            </c:if>
            <td style="width:250px">
                <a href="${baseUrl}/tagged/${tag.text}"><span
                        class=questionTag>${tag.text}</span></a> <b>(${tag.size})</b>
            </td>
            <c:if test="${status.index % 3 == 2}">
                </tr>
            </c:if>
        </c:forEach>
        <c:if test="${fn:length(tags) % 3 != 0}">
            </tr>
        </c:if>
    </table>

</div>