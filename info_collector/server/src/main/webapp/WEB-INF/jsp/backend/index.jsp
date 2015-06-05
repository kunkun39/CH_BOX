<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<html>
<head>
    <link rel="Bookmark" href="${pageContext.request.contextPath}/images/favicon.gif">
    <link rel="Shortcut Icon" href="${pageContext.request.contextPath}/images/favicon.gif">
    <title>后台管理平台</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/theme/default/index.css" type="text/css"/>
    <style type="text/css">
        img, div { behavior: url(iepngfix.htc) }
    </style>
</head>

<body id="login">

    <div id="wrappertop"></div>
        <div id="wrapper">
            <div id="content">
                <div id="header">
                </div>
                <div id="darkbanner" class="banner320">
                    <h2>后台管理平台</h2>
                </div>
                <div id="darkbannerwrap">
                </div>
                <form name="form" method="post" action="${pageContext.request.contextPath}/j_spring_security_check">
                    <fieldset class="form">
                        <p>
                            <label for="user_name">用户名:</label>
                            <input name="j_username" id="user_name" type="text">
                        </p>
                        <p>
                            <label for="user_password">密  码:</label>
                            <input name="j_password" id="user_password" type="password">
                        </p>
                        <button type="submit" class="positive" name="Submit">
                            <img src="${pageContext.request.contextPath}/images/key.png" alt="Announcement">
                            登录
                        </button>

                        <c:if test="${SPRING_SECURITY_LAST_EXCEPTION != null}">
                            <br/>
                            <div class="error">
                                 <span style="color:red;font-size:12px;" class="error">
                                    对不起, 用户名或者密码不正确!
                                 </span>
                            </div>
                        </c:if>
                        <c:if test="${SPRING_SECURITY_LAST_EXCEPTION == null}">
                            <br/>
                            <br/>
                            <br/>
                        </c:if>
                        <br/>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        提示建议使用FireFox或Chrome浏览器
                    </fieldset>
                </form>
            </div>
        </div>
    </div>

</body>
</html>