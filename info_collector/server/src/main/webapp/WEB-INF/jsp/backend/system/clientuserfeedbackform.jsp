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
            <jsp:include page="../statisticfeedback.jsp"/>
        </td>
        <td valign="top">
            <spring-form:form commandName="feedback" method="POST" cssClass="form">
                <table cellspacing="0" width="100%">
                    <input type="hidden" name="id" value="${feedback.id}"/>

                    <%--<tr>--%>
                        <%--<td width="200px;">--%>
                             <%--用户 <span class="required">*</span>--%>
                        <%--</td>--%>
                        <%--<td>--%>
                            <%--<spring-form:input path="username" maxlength="30" cssStyle="width:300px;"/>&nbsp;--%>
                            <%--<spring-form:errors path="username" cssClass="required"/>--%>
                        <%--</td>--%>
                    <%--</tr>--%>

                    <tr>
                        <td>
                           MAC地址<span class="required">*</span>
                        </td>
                        <td>
                            <c:set var="justRead" value="false"/>
                            <spring-form:input path="usermac" maxlength="30" cssStyle="width:300px;" readonly="${justRead}"/>&nbsp;
                            <spring-form:errors path="usermac" cssClass="required"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                            反馈内容<span class="required">*</span>
                        </td>
                        <td>
                            <c:set var="justRead" value="false"/>
                            <spring-form:input path="content" maxlength="30" cssStyle="width:300px;" readonly="${justRead}"/>&nbsp;
                            <spring-form:errors path="content" cssClass="required"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            回复内容
                        </td>
                        <td>
                            <spring-form:input id="reply" path="reply" cssStyle="width:300px;"/>
                        </td>
                    </tr>

                    <tr>
                        <td>
                        </td>
                        <td>
                            <button type="button" class="thoughtbotform" onclick="window.location.href='${pageContext.request.contextPath}/backend/clientuserfeedbackoverview.html?&current=${current}'">
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