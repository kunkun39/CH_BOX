<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="ch" uri="http://www.chanhong.com" %>
<html>
<head>
    <title>后台管理平台</title>
</head>
<body>
<div class="action">
    &nbsp;
</div>
<table cellpadding="0" cellspacing="0" width="100%" class="box">
    <tr>
        <td width="200" valign="top" style="background: #e8e8e8;border-right: 1px solid #CCC;">
            <jsp:include page="../systemtype.jsp"/>
        </td>
        <td valign="top">
            <div style="float: left; padding-right: 5px; padding-top: 5px; padding-left: 5px; padding-bottom: 5px">
                <a href="${pageContext.request.contextPath}/backend/appform.html"><button class="thoughtbot">添加应用</button></a>
            </div>
            <%--<form action="${pageContext.request.contextPath}/backend/useroverview.html" class="search_form" method="POST">--%>
                <%--<div class="search">--%>
                    <%--<span><label>姓名:</label><input type="text" name="filername" class="text" value="${paging.name}"/></span>--%>
                    <%--<input type="button" value="查询" onclick="this.form.submit();"/>--%>
                <%--</div>--%>
            <%--</form>--%>

            <table width="100%" cellpadding="0" cellspacing="0" class="list">
                <thead>
                <td width="10%">&nbsp;&nbsp;应用名</td>
                <td width="20%">API Key</td>
                <td width="20%">描述</td>
                <td width="10%">创建时间</td>
                </thead>
                <tbody>
                <c:set var="turns" value="true"/>
                <c:forEach items="${apps}" var="app">
                    <c:set var="color" value="${turns ? 'r1' :'r2'}"/>
                    <tr class="${color}" onmouseover="this.className='over'" onmouseout="this.className='${color}'">
                    <c:set var="turns" value="${!turns}"/>
                        <td>&nbsp;&nbsp;${app.appname}</td>
                        <td>${app.appkey} </td>
                        <td>${app.appdes}</td>
                        <td>${app.dateTime}</td>
                        <td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="paging">
                <ch:paging urlMapping="${pageContext.request.contextPath}/backend/appoverview.html" showGoTo="false" paging="${paging}"/>
            </div>
        </td>
    </tr>
</table>
</body>
</html>