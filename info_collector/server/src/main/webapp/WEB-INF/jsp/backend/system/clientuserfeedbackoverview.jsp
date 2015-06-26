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
            <jsp:include page="../statisticfeedback.jsp"/>
        </td>
        <td valign="top">
            <table width="100%" cellpadding="0" cellspacing="0" class="list">
                <thead>
                <td width="5%">&nbsp;&nbsp;编号</td>
                <td width="30%">反馈内容</td>
                <td width="15%">反馈时间</td>
                <td width="5%">用户</td>
                <td width="10%">MAC地址</td>
                <td width="5%">状态</td>
                <td>操作</td>
                </thead>
                <tbody>
                <c:set var="turns" value="true"/>
                <c:forEach items="${feedbacks}" var="feedback">
                    <c:set var="color" value="${turns ? 'r1' :'r2'}"/>
                    <tr class="${color}" onmouseover="this.className='over'" onmouseout="this.className='${color}'">
                    <c:set var="turns" value="${!turns}"/>
                        <td>&nbsp;&nbsp;${feedback.id}</td>
                        <td>${feedback.content}</td>
                        <td>${feedback.dateTime}</td>
                        <td>${feedback.username} </td>
                        <td>${feedback.usermac}</td>
                        <td>
                            <c:if test="${feedback.status=='1'}">
                            已处理
                            </c:if>
                            <c:if test="${feedback.status=='0'}">
                            未处理
                            </c:if>
                        </td>

                        <td>
                            <%--<c:if test="${feedback.status=='0'}">--%>
                                <%--<a href="${pageContext.request.contextPath}/backend/clientuserfeedbackform.html?feedbackId=${feedback.id}&&current=${current}" onclick=""><button class="thoughtbot">回复</button></a>--%>
                            <%--</c:if>--%>
                                <%--<c:if test="${feedback.status=='0'}">--%>
                                <a href="${pageContext.request.contextPath}/backend/clientuserfeedbackchangestatus.html?feedbackId=${feedback.id}&&current=${current}" onclick=""><button class="thoughtbot">审查</button></a>
                                <%--</c:if>--%>

                        </td>

                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="paging">
                <ch:paging urlMapping="${pageContext.request.contextPath}/backend/clientuserfeedbackoverview.html" showGoTo="false" paging="${paging}"/>
            </div>
        </td>
    </tr>
</table>
</body>
</html>