<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring-form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>后台管理平台</title>
    <script src="${pageContext.request.contextPath}/js/jquery-1.7.2.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-ui/jquery-ui-1.8.16.custom.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/jstree/jquery.jstree.js" type="text/javascript"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/theme/default/jquery-ui-1.8.22.custom.css" type="text/css"/>
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
            <spring-form:form commandName="app" method="POST" cssClass="form" enctype="multipart/form-data">
                <table cellspacing="0" width="100%">
                    <input type="hidden" name="id" value="${app.id}"/>
                    <input type="hidden" name="appIconId" value="${app.appIconId}"/>

                    <tr>
                        <td width="200px;">
                             应用名称 <span class="required">*</span>
                        </td>
                        <td>
                            <spring-form:input path="appname" maxlength="30" cssStyle="width:300px;"/>&nbsp;
                            <spring-form:errors path="appname" cssClass="required"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            应用描述
                        </td>
                        <td>
                            <spring-form:input id="appdes" path="appdes" cssStyle="width:300px;"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            应用图标
                        </td>

                        <td>
                            <input type="file" id="appIconFile" name="appIconFile" class="file" style="width:300px;"/>
                            <c:if test="${app.appIconId > 0}">
                                <br/>
                                <br/>
                                已上传文件:${app.uploadFileName}
                                <br/>
                                <br/>
                                <img src="${applicationWebAddress}${app.actualFileName}" style="width: 600px;" />
                            </c:if>
                            <spring-form:errors path="appIconFile" cssClass="required"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                        </td>
                        <td>
                            <button type="button" class="thoughtbotform" onclick="window.location.href='${pageContext.request.contextPath}/backend/appoverview.html?&current=${current}'">
                                返回
                            </button>
                        	<button name="" type="submit" class="thoughtbotform" onclick="javascript: return validate();">保存</button>
                        </td>
                    </tr>
                </table>
            </spring-form:form>
        </td>
    </tr>
</table>

</body>
</html>