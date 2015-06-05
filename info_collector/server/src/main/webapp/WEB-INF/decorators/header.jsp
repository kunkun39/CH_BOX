<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="ch" uri="http://www.chanhong.com" %>
<div class="header">
    <h1 class="logo"><a title="ST" href="${pageContext.request.contextPath}/backend/dashboard.html" onfocus="blur()"></a></h1>

    <div class="title">
    </div>

    <c:set value="${pageContext.request.requestURI}" var="pageUrl"/>
    <div class="account">
        <a href="#">
            <img src="${pageContext.request.contextPath}/images/clients.gif" alt=""/><ch:user/>
        </a>
        |
        <a href="${pageContext.request.contextPath}/backend/userchangepassword.html" style="margin-left: 0">
            <img src="${pageContext.request.contextPath}/images/password.gif" alt="Change Password"/> 修改密码
        </a>
        |
        <a href="${pageContext.request.contextPath}/j_spring_security_logout" style="margin-left: 0">
            <img src="${pageContext.request.contextPath}/images/logout.gif" alt="Logout"/> 退出
        </a>
    </div>

    <div class="menu">
        <a href="${pageContext.request.contextPath}/backend/dashboard.html" <c:if test="${'DASHBOARD' eq BROSWER_LOCATION}">style="font-weight:bold;background:#5d5c5c;"</c:if>>首页</a>

        <a href="${pageContext.request.contextPath}/backend/useroverview.html" <c:if test="${'USER' eq BROSWER_LOCATION}">style="font-weight:bold;background:#5d5c5c;"</c:if>>系统设置</a>
    </div>

    <div class="toolbar">
        <img src="${pageContext.request.contextPath}/theme/default/images/left.gif"/> &nbsp;
    </div>

</div>
