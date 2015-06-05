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
            <spring-form:form commandName="user" method="POST" cssClass="form">
                <table cellspacing="0" width="100%">
                    <input type="hidden" name="id" value="${user.id}"/>

                    <tr>
                        <td width="200px;">
                             姓名 <span class="required">*</span>
                        </td>
                        <td>
                            <spring-form:input path="name" maxlength="30" cssStyle="width:300px;"/>&nbsp;
                            <spring-form:errors path="name" cssClass="required"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                             编号(用户名) <span class="required">*</span>
                        </td>
                        <td>
                            <c:set var="justRead" value="false"/>
                            <c:if test="${user.username == 'chupdate_admin'}">
                                <c:set var="justRead" value="true"/>
                            </c:if>
                            <spring-form:input path="username" maxlength="30" cssStyle="width:300px;" readonly="${justRead}"/>&nbsp;
                            <spring-form:errors path="username" cssClass="required"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            联系方式
                        </td>
                        <td>
                            <spring-form:input id="contactWay" path="contactWay" cssStyle="width:300px;"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                        </td>
                        <td>
                            <button type="button" class="thoughtbotform" onclick="window.location.href='${pageContext.request.contextPath}/backend/useroverview.html?filername=${filername}&current=${current}'">
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