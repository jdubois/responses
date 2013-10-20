<%@ page import="com.github.jdubois.responses.model.Question" %>
<%@ page import="com.github.jdubois.responses.model.Instance" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%
    Instance instance = (Instance) request.getAttribute("instance");
    Question question = (Question) request.getAttribute("question");
    response.sendRedirect(request.getContextPath() + "/i/" + instance.getName() + "/q/" + question.getId() + "/" + question.getTitleAsUrl() + "?newAnswer=ok");
%>