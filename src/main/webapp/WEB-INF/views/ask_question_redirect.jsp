<%@ page import="com.github.jdubois.responses.model.Instance" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%
    Instance instance = (Instance) request.getAttribute("instance");
    String taglist = (String) request.getAttribute("taglist");
    if (taglist == null) {
        response.sendRedirect(request.getContextPath() + "/i/" + instance.getName() + "?newQuestion=ok");
    } else {
        response.sendRedirect(request.getContextPath() + "/i/" + instance.getName() + "/tagged/" + taglist+ "?newQuestion=ok");
    }
%>