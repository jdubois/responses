<%@ page import="com.github.jdubois.responses.service.impl.ConfigurationServiceImpl" %>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String staticContent = ConfigurationServiceImpl.staticContent;
    request.setAttribute("staticContent", staticContent);
    request.setAttribute("context", request.getContextPath());
%>
<br/>
<form id="loginForm" action="${context}/login" method="post"  style="margin-left: 20px;">
    <p><input type="hidden" name="spring-security-redirect" value="${targetUrl}"/></p>
    <table>
        <tr>
            <td>E-mail:</td>
            <td><input type="text" name="j_username"
                       id="j_username"/>
            </td>
        </tr>
        <tr>
            <td>Mot de passe:</td>
            <td><input type="password" name="j_password"
                       id="j_password"/></td>
        </tr>
        <tr>
            <td>Authentification automatique:
            </td>
            <td><input type="checkbox"
                       name="_spring_security_remember_me" id="_spring_security_remember_me"/>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                &nbsp;<br/>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <div>
                    <a class="button" href="javascript:submitCompleteForm();"
                       onclick="this.blur();"><span>Authentification</span></a>
                    <a class="button" href="javascript:registration();"
                       onclick="this.blur();"><span>Cr&eacute;ation d'un nouveau compte</span></a>
                    <a class="button" href="javascript:forgottenPassword();"
                       onclick="this.blur();"><span>Mot de passe oubli&eacute;</span></a>
                </div>
            </td>
        </tr>
    </table>
</form>


<script type="text/javascript">
    function submitCompleteForm() {
        document.forms["loginForm"].submit();
    }
    function registration() {
        window.location.href = '${context}/authentication/registration';
    }
    function forgottenPassword() {
        window.location.href = '${context}/authentication/forgotten_password';
    }
</script>

