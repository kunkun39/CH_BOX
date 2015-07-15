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
            <jsp:include page="../statisticlocation.jsp"/>
        </td>
        <td valign="top">
            <table width="100%" cellpadding="0" cellspacing="0" class="list">
                <thead>
                <td width="5%">&nbsp;&nbsp;编号</td>
                <td width="30%">客户名</td>
                <td width="15%">MAC地址</td>
                <td width="5%">经度</td>
                <td width="10%">纬度</td>
                <td>操作</td>
                </thead>
                <tbody>
                <c:set var="turns" value="true"/>
                <c:forEach items="${clientLocationDTOs}" var="clientLocation">
                    <c:set var="color" value="${turns ? 'r1' :'r2'}"/>
                    <tr class="${color}" onmouseover="this.className='over'" onmouseout="this.className='${color}'">
                        <c:set var="turns" value="${!turns}"/>
                        <td>&nbsp;&nbsp;${clientLocation.id}</td>
                        <td>${clientLocation.clientName}</td>
                        <td>${clientLocation.userMac}</td>
                        <td>${clientLocation.lon} </td>
                        <td>${clientLocation.lat}</td>

                        <td>
                            <a href="${pageContext.request.contextPath}/backend/clientlocationmap.html?lon=${clientLocation.lon}&&lat=${clientLocation.lat}" onclick=""><button class="thoughtbot">查看</button></a>
                        </td>

                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="paging">
                <ch:paging urlMapping="${pageContext.request.contextPath}/backend/clientlocationoverview.html" showGoTo="false" paging="${paging}"/>
            </div>
        </td>
    </tr>
</table>
</body>
</html>