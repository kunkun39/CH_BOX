<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring-form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>后台管理平台</title>
    <script type="text/javascript">
    </script>
</head>
<body>
<div class="action">
    &nbsp;
</div>

<table cellpadding="0" cellspacing="0" width="100%" class="box">
    <tr>
        <td valign="top">
            <spring-form:form  commandName="userPassword" method="POST" cssClass="form">
            <table cellspacing="0" width="100%">
                <input type="hidden" name="userId" value="${userPassword.userId}"/>
                <tr>
                    <td width="20%">
                        姓名
                    </td>
                    <td>
                        ${userPassword.name}
                    </td>
                </tr>
                <tr>
                    <td width="20%">
                        编号
                    </td>
                    <td>
                        ${userPassword.username}
                    </td>
                </tr>
                <tr>
                    <td class="name">
                        输入原密码&nbsp;<span class="required">*</span>
                    </td>
                    <td>
                        <input type="password" id="oldPassword" name="oldPassword" value="" maxlength="30" style="width:200px;"/>
                        <spring-form:errors path="oldPassword" cssClass="error" cssStyle="color:red"/>
                    </td>
                 </tr>
                <tr>
                    <td class="name">
                        输入新密码&nbsp;<span class="required">*</span>
                    </td>
                    <td>
                        <input type="password" id="newPassword" name="newPassword" value="" maxlength="30" style="width:200px;"/>
                    </td>
                 </tr>
                 <tr>
                    <td class="name">
                        再次输入新密码&nbsp;<span class="required">*</span>&nbsp;
                    </td>
                    <td>
                        <input type="password" id="newPasswordAgain" name="newPasswordAgain" value="" maxlength="30" style="width:200px;"/>
                        <spring-form:errors path="newPasswordAgain" cssClass="error" cssStyle="color:red"/>
                    </td>
                </tr>
                <tr>
                    <td>

                    </td>
                    <td>
                        <button name="comfirm" type="submit" class="thoughtbotform">保存</button>
                    </td>
                </tr>
                </table>
            </spring-form:form>
        </td>
    </tr>
</table>
</body>
</html>

</body>
</html>