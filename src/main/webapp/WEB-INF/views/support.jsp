<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%
    String instanceName = (String) request.getAttribute("instanceName");
    request.setAttribute("baseUrl", request.getContextPath() + "/i/" + instanceName);
%>
<div id="support">
    <c:if test="${workflow eq null or workflow.state eq 3}">
        <sec:authorize ifAnyGranted="ROLE_USER">
            <p>
                Si cette question ne trouve pas de r&eacute;ponse, vous pouvez faire appel au service
                de support, qui vous propose un d&eacute;lai de r&eacute;ponse et un niveau de qualit&eacute;
                contractualis&eacute;s.
                <br/>
                Toute demande de support sera valid&eacute;e par un mod&eacute;rateur avant d'&ecirc;tre envoy&eacute;e
                au support.
            </p>

            <div style="padding-left: 250px">
                <a class="button" href="javascript:support('${questionId}', 1, '');"
                   onclick="this.blur();"><span>Faire une demande de support</span></a>
            </div>
        </sec:authorize>
    </c:if>
    <c:if test="${workflow.state eq 1}">

        <h2>La demande de support est en attente de validation</h2>
        <sec:authorize ifAnyGranted="ROLE_MODERATOR">
            <br/>

            <div style="padding-left: 200px">
                <a class="button" href="javascript:support('${questionId}', 2, '');"
                   onclick="this.blur();"><span>Valider la demande</span></a>

                <a class="button" href="javascript:support('${questionId}', 3, '');"
                   onclick="this.blur();"><span>Refuser la demande</span></a>
            </div>
        </sec:authorize>
    </c:if>
    <c:if test="${workflow.state eq 2}">

        <h2>La demande de support est en attente d'assignation</h2>

    </c:if>
    <c:if test="${workflow.state eq 4}">
        <h2>La demande de support est en attente de traitement</h2>
        <sec:authorize ifAnyGranted="ROLE_SUPPORT">
            <c:if test="${workflow.assignedUser eq currentUser}">
                <p>Cette demande vous a &eacute;t&eacute; assign&eacute;e. Si vous avez r&eacute;pondu &agrave; la
                question, vous pouvez la marquer comme r&eacute;solue.</p>
                <div style="padding-left: 250px">
                    <a class="button" href="javascript:support('${questionId}', 5, '');"
                       onclick="this.blur();"><span>R&eacute;soudre la demande</span></a>

                </div>
                <br/><br/><br/>
            </c:if>
        </sec:authorize>
    </c:if>
    <c:if test="${workflow.state eq 5}">
        <h2>La demande de support a &eacute;t&eacute; r&eacute;solue</h2>
        <sec:authorize ifAnyGranted="ROLE_MODERATOR">
            <p>Cette demande a &eacute;t&eacute; trait&eacute;e par un expert. Vous pouvez clore cette demande ou
                refuser la r&eacute;ponse de l'expert. Sans action de votre part, la demande sera automatiquement
                consid&eacute;r&eacute;e comme close dans une semaine.</p>
                <div style="padding-left: 200px">
                <a class="button" href="javascript:support('${questionId}', 7, '');"
                   onclick="this.blur();"><span>Clore la demande</span></a>

                <a class="button" href="javascript:support('${questionId}', 6, '');"
                   onclick="this.blur();"><span>Refuser la r&eacute;ponse</span></a>
            </div>
        </sec:authorize>
    </c:if>
    <c:if test="${workflow.state eq 6}">
        <h2>La r&eacute;solution de cette demande a &eacute;t&eacute; refus&eacute;e</h2>
        <sec:authorize ifAnyGranted="ROLE_SUPPORT">
            <c:if test="${workflow.user eq currentUser}">
                <p>Cette demande vous a &eacute;t&eacute; assign&eacute;e. Si vous avez r&eacute;pondu &agrave; la
                question, vous pouvez la marquer comme r&eacute;solue.</p>
                <div style="padding-left: 250px">
                    <a class="button" href="javascript:support('${questionId}', 5, '');"
                       onclick="this.blur();"><span>R&eacute;soudre la demande</span></a>

                </div>
                <br/><br/><br/>
            </c:if>
        </sec:authorize>
    </c:if>
    <c:if test="${workflow.state eq 2 or workflow .state eq 4 or workflow.state eq 6}">
        <sec:authorize ifAnyGranted="ROLE_SUPPORT">

            <p style="padding-left: 150px">
                Assigner la demande &agrave; un expert :
                <select name="assignedUser" id="assignedUser">
                    <c:forEach var="expert" items="${experts}">
                        <option value="${expert.id}">${expert.firstName} ${expert.lastName}</option>
                    </c:forEach>
                </select>
                <br/>
            </p>

            <div style="padding-left: 250px">
                <a class="button" href="javascript:support('${questionId}', 4, $('#assignedUser').val());"
                   onclick="this.blur();"><span>Assigner la demande</span></a>

            </div>
        </sec:authorize>
    </c:if>
    <c:if test="${workflow.state eq 7}">
        <h2>La r&eacute;solution de cette demande a &eacute;t&eacute; valid&eacute;e</h2>
    </c:if>
    <p><br/><br/></p>
    <p>
        <c:if test="${fn:length(workflows) gt 0}">
        <br/>
        <b style="padding-left: 250px">Historique du support pour cette question</b>
        <table style="margin: auto; border: #6560ab solid 1px;">
            <tr>
                <td style="width: 150px"><b>Date</b></td>
                <td><b>Etat</b></td>
            </tr>
            <c:forEach var="workflow" items="${workflows}" varStatus="status">
                <tr>
                    <td><fmt:formatDate pattern="dd/MM/yyyy kk:mm:ss" value="${workflow.stateDate}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${workflow.state eq 1}">
                                Support demand&eacute; par
                            </c:when>
                            <c:when test="${workflow.state eq 2}">
                                Demande valid&eacute;e par
                            </c:when>
                            <c:when test="${workflow.state eq 3}">
                                Demande refus&eacute;e par
                            </c:when>
                            <c:when test="${workflow.state eq 4}">
                                Support assign&eacute; par
                            </c:when>
                            <c:when test="${workflow.state eq 5}">
                                Demande r&eacute;solue par
                            </c:when>
                            <c:when test="${workflow.state eq 5}">
                                Demande r&eacute;solue par
                            </c:when>
                            <c:when test="${workflow.state eq 6}">
                                R&eacute;solution rejet&eacute;e par
                            </c:when>
                            <c:when test="${workflow.state eq 7}">
                                Demande close par
                            </c:when>
                        </c:choose>
                        <a href="${baseUrl}/profile/${workflow.user.id}/${workflow.user.profileUrl}"><b>${workflow.user.firstName} ${workflow.user.lastName}</b></a>
                        <c:if test="${workflow.state eq 4}">
                            &agrave; <a href="${baseUrl}/profile/${workflow.assignedUser.id}/${workflow.assignedUser.profileUrl}"><b>${workflow.assignedUser.firstName} ${workflow.assignedUser.lastName}</b></a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
    </p>
</div>
