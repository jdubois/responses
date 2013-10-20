<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table>
    <tr>
        <td class="top-questions-header">
            <c:if test="${type eq 0}"><b>Les + populaires</b></c:if>
            <c:if test="${type eq 1}">
                <input type="hidden" id="top-type" value="1"/>
                <a id="top-type" href="javascript:showTopQuestions(0, $('#top-when').val())">Les + populaires</a>
            </c:if>
        </td>
        <td>&nbsp;|&nbsp;</td>
        <td class="top-questions-header">
            <c:if test="${type eq 1}"><b>Les + vues</b></c:if>
            <c:if test="${type eq 0}">
                <input type="hidden" id="top-type" value="0"/>
                <a href="javascript:showTopQuestions(1, $('#top-when').val())">Les + vues</a>
            </c:if>
        </td>
        <td width="350px"></td>
        <td class="top-questions-header">Quand: <select id="top-when" onchange="showTopQuestions($('#top-type').val(), $('#top-when').val())">
            <option value="0" <c:if test="${when eq 0}">selected</c:if>>Aujourd'hui</option>
            <option value="1" <c:if test="${when eq 1}">selected</c:if>>Cette semaine</option>
            <option value="2" <c:if test="${when eq 2}">selected</c:if>>Ce mois-ci</option>
            <option value="3" <c:if test="${when eq 3}">selected</c:if>>Toujours</option>
        </select></td>
    </tr>
</table><br/>
<jsp:include page="questions.jsp"/>