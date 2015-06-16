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
            <form action="${pageContext.request.contextPath}/backend/tvchannelinfooverview.html" class="search_form" method="POST">
                <div class="search">
                    <span><label>频道名:</label><input type="text" name="filername" class="text" value="${paging.channelName}"/></span>
                    <input type="button" value="查询" onclick="this.form.submit();"/>
                </div>
            </form>

            <table width="100%" cellpadding="0" cellspacing="0" class="list">
                <thead>
                <td width="10%">&nbsp;&nbsp;频道名</td>
                <td width="10%">节目名</td>
                <td width="10%">MAC地址</td>
                <td width="45%">时间</td>
                </thead>
                <tbody>
                <c:set var="turns" value="true"/>
                <c:forEach items="${tvChannelInfos}" var="tvChannelInfo">
                    <c:set var="color" value="${turns ? 'r1' :'r2'}"/>
                    <tr class="${color}" onmouseover="this.className='over'" onmouseout="this.className='${color}'">
                        <c:set var="turns" value="${!turns}"/>
                        <td>&nbsp;&nbsp;${tvChannelInfo.tvChannelName}</td>
                        <td>${tvChannelInfo.tvProgramName} </td>
                        <td>${tvChannelInfo.userMac}</td>
                        <td>${tvChannelInfo.year}-${tvChannelInfo.month}-${tvChannelInfo.day}-${tvChannelInfo.hour}</td>

                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="paging">
                <ch:paging urlMapping="${pageContext.request.contextPath}/backend/tvchannelinfooverview.html" showGoTo="false" paging="${paging}"/>
            </div>
        </td>
    </tr>
</table>
</body>
</html>