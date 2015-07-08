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
                <a href="${pageContext.request.contextPath}/backend/tvchannelform.html"><button class="thoughtbot">添加频道</button></a>

            </div>
            <form action="${pageContext.request.contextPath}/backend/tvchannelinfooverview.html" class="search_form" method="POST">
                <div class="search">
                    <span><label>频道名:</label><input type="text" name="filername" class="text" value="${paging.channelName}"/></span>
                    <input type="button" value="查询" onclick="this.form.submit();"/>
                </div>
            </form>


            <table width="100%" cellpadding="0" cellspacing="0" class="list">
                <thead>
                <td width="40%">&nbsp;&nbsp;序号</td>
                <td width="40%">频道名</td>
                <td width="20%">操作</td>

                </thead>
                <tbody>
                <c:set var="turns" value="true"/>
                <c:forEach items="${tvChannels}" var="tvChannel">
                    <c:set var="color" value="${turns ? 'r1' :'r2'}"/>
                    <tr class="${color}" onmouseover="this.className='over'" onmouseout="this.className='${color}'">
                        <c:set var="turns" value="${!turns}"/>
                        <td>&nbsp;&nbsp;${tvChannel.channelSequence}</td>
                        <td>${tvChannel.tvChannelName} </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/backend/tvchannelform.html?tvChannelId=${tvChannel.id}&filername=${filername}&current=${current}"><button class="thoughtbot">编辑</button></a>

                        </td>

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