<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="ch" uri="http://www.chanhong.com" %>
<html>
<head>
    <title>后台管理平台</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/theme/default/module.css" type="text/css"/>
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

            <div style="float: left; padding-right: 5px;margin-top: 10px; padding-left: 5px; padding-bottom: 5px">
                <a href="${pageContext.request.contextPath}/backend/appform.html">
                    <button class="thoughtbot">添加应用</button>
                </a>
            </div>
            <div class="tv_main">
                <div class="channel">
                    <ul class="list">
                        <c:forEach items="${apps}" var="app">
                            <li class="c1">
                                <a class="edit"
                                   href="${pageContext.request.contextPath}/backend/appform.html?appId=${app.id}">编辑</a>

                                <p class="icon ico1"></p>
                                <p ><a href="${applicationWebAddress}${app.actualFileName}" target="_blank">${applicationWebAddress}${app.actualFileName}</a></p>

                                <p class="name">${app.appname}</p>
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </div>


        <td/>
    <tr/>


</table>
</body>
</html>