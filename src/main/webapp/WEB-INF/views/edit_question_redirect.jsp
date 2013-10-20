<%@ page import="com.github.jdubois.responses.model.Instance" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%
    String questionUrl = (String) request.getAttribute("questionUrl");
    response.sendRedirect(questionUrl + "?edit=question");
%>