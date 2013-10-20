<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.github.jdubois.responses.service.impl.ConfigurationServiceImpl" %>
<%@ page import="com.github.jdubois.responses.model.Instance" %>
<%
    String staticContent = ConfigurationServiceImpl.staticContent;
    request.setAttribute("staticContent", staticContent);
    request.setAttribute("context", request.getContextPath());
    Instance instance = (Instance) request.getAttribute("instance");
    if (instance != null) {
        request.setAttribute("baseUrl", request.getContextPath() + "/i/" + instance.getName());
    } else {
        request.setAttribute("baseUrl", request.getContextPath());
    }
%>
<meta http-equiv="content-type" content="text/html; charset=iso-8859-1"/>
<meta name="author" content="Julien Dubois"/>
<link rel="shortcut icon" href="${staticContent}/favicon.ico"/>
<style type="text/css" title="currentStyle" media="screen">
    @import "${staticContent}/style.css";
<%--
    @import "${staticContent}/jquery/css/custom-theme/jquery-ui-1.7.2.custom.css";
    @import "${staticContent}/jquery/superfish-1.4.8/css/superfish.css";
    @import "${staticContent}/jquery/autocomplete-1.1/jquery.autocomplete.css";--%>
   @import "${staticContent}/jquery/all/all.css";
</style>
