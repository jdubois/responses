<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.github.jdubois.responses.service.impl.ConfigurationServiceImpl" %>
<%
    String staticContent = ConfigurationServiceImpl.staticContent;
    String googleAnalytics = ConfigurationServiceImpl.googleAnalytics;
    request.setAttribute("staticContent", staticContent);
    request.setAttribute("googleAnalytics", googleAnalytics);
%>
<div id="about"><a href="${context}/">Accueil</a> | <a href="${context}/about/contact">Contactez nous</a> | <a href="${context}/about/mentions_legales">Mentions l&eacute;gales</a> | <a href="${context}/about/conditions_generales_d_utilisation">Conditions g&eacute;n&eacute;rales d'utilisation</a> | <a href="http://www.julien-dubois.com/">Site de Julien Dubois</a> </div>
<script type="text/javascript">
    var instanceId = "${instanceId}";
    var instanceName = "${instanceName}";
    var context = "${context}";
    var baseUrl = "${baseUrl}";
    var questionIndex = "${questionIndex}";
    <c:choose>
        <c:when test="${selectedTagsList != null}">
            var selectedTagsList = "${selectedTagsList}";
        </c:when>
        <c:otherwise>
            var selectedTagsList = "";
        </c:otherwise>
    </c:choose>
</script>
<%--<script type="text/javascript" src="${staticContent}/jquery/js/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="${staticContent}/jquery/js/jquery-ui-1.8.custom.min.js"></script>
<script type="text/javascript" src="${staticContent}/jquery/superfish-1.4.8/js/hoverIntent.js"></script>
<script type="text/javascript" src="${staticContent}/jquery/bgiframe-2.1.1/jquery.bgiframe.min.js"></script>
<script type="text/javascript" src="${staticContent}/jquery/superfish-1.4.8/js/superfish.js"></script>
<script type="text/javascript" src="${staticContent}/jquery/autocomplete-1.1/jquery.autocomplete.min.js"></script>
<script type="text/javascript" src="${staticContent}/jquery/tooltip-1.3/jquery.tooltip.min.js"></script>--%>

<script type="text/javascript" src="${staticContent}/jquery/all/all.js"></script>
<script type="text/javascript" src="${staticContent}/responses.js"></script>

<c:if test="${googleAnalytics ne ''}">
    <script type="text/javascript">
        var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
        document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
    </script>
    <script type="text/javascript">
        try {
            var pageTracker = _gat._getTracker("${googleAnalytics}");
            pageTracker._setDomainName("none");
            pageTracker._setAllowLinker(true);
            pageTracker._trackPageview();
        } catch(err) {
        }</script>
</c:if>


