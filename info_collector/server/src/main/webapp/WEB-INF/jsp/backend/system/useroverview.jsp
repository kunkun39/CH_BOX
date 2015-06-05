<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="ch" uri="http://www.chanhong.com" %>
<html>
<head>
    <title>后台管理平台</title>
    <script type="text/javascript">

        function userDeleteConfirm() {
            return confirm('确定要停用该用户吗?');
        }

        function userEnableConfirm() {
            return confirm('确定要激活该用户吗?');
        }
    </script>
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
            <div style="float: left; padding-right: 5px; padding-top: 5px; padding-left: 5px;">
                <a href="${pageContext.request.contextPath}/backend/userform.html"><button class="thoughtbot">添加用户</button></a>
            </div>
            <form action="${pageContext.request.contextPath}/backend/useroverview.html" class="search_form" method="POST">
                <div class="search">
                    <span><label>姓名:</label><input type="text" name="filername" class="text" value="${paging.name}"/></span>
                    <input type="button" value="查询" onclick="this.form.submit();"/>
                </div>
            </form>

            <table width="100%" cellpadding="0" cellspacing="0" class="list">
                <thead>
                <td width="10%">&nbsp;&nbsp;姓名</td>
                <td width="10%">编号</td>
                <td width="10%">密码</td>
                <td width="45%">联系方式</td>
                <td width="5%">状态</td>
                <td>操作</td>
                </thead>
                <tbody>
                <c:set var="turns" value="true"/>
                <c:forEach items="${users}" var="user">
                    <c:set var="color" value="${turns ? 'r1' :'r2'}"/>
                    <tr class="${color}" onmouseover="this.className='over'" onmouseout="this.className='${color}'">
                    <c:set var="turns" value="${!turns}"/>
                        <td>&nbsp;&nbsp;${user.name}</td>
                        <td>${user.username} </td>
                        <td>${user.password}</td>
                        <td>${user.contactWay}</td>
                        <td>
                            <c:if test="${user.enabled}">
                                在用
                            </c:if>
                            <c:if test="${!user.enabled}">
                                停止使用
                            </c:if>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/backend/userform.html?userId=${user.id}&filername=${filername}&current=${current}"><button class="thoughtbot">编辑</button></a>

                            <c:if test="${user.username != 'qsadmin'}">
                                <c:if test="${user.enabled}">
                                    <a href="${pageContext.request.contextPath}/backend/userchangeenable.html?userId=${user.id}&filername=${filername}&current=${current}" onclick="return userDeleteConfirm();"><button class="thoughtbot">停用</button></a>
                                </c:if>
                                <c:if test="${!user.enabled}">
                                    <a href="${pageContext.request.contextPath}/backend/userchangeenable.html?userId=${user.id}&filername=${filername}&current=${current}" onclick="return userEnableConfirm();"><button class="thoughtbot">激活</button></a>
                                </c:if>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="paging">
                <ch:paging urlMapping="${pageContext.request.contextPath}/backend/useroverview.html" showGoTo="false" paging="${paging}"/>
            </div>
        </td>
    </tr>
</table>
</body>
</html>