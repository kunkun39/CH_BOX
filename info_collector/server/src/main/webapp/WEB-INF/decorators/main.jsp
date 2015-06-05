<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312"/>
    <link rel="Bookmark" href="${pageContext.request.contextPath}/images/favicon.gif">
    <link rel="Shortcut Icon" href="${pageContext.request.contextPath}/images/favicon.gif">
    <title><decorator:title default="后台管理平台"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/theme/default/public.css" type="text/css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/theme/default/main.css" type="text/css"/>
    <decorator:head/>
</head>
<body>
<div class="wrapper">
    <jsp:include page="header.jsp"/>
    <div class="main-container"><decorator:body/></div>
</div>
</body>
</html>