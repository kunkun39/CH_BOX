<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
    <%--<div class="hello" style="padding-left: 190px">--%>
        <%--<span id="today">当前时间 <joda:format value="${today}" pattern="yyyy-MM-dd"/></span>--%>
    <%--</div>--%>

    <table cellpadding="0" cellspacing="0" width="100%" class="box">
        <tr>
            <td width="130" valign="top" style="padding-left: 70px;padding-top: 40px">
            </td>
            <td valign="top">
                <div class="calender">
                    <form action="${pageContext.request.contextPath}/backend/dashboard.html" method="POST" class="search_form">
                        <div class="search" style="margin-bottom:10px;">
                            消息搜索：
                            &nbsp;年 &nbsp;
                            <select name="year" style="width: 100px;">
                                <option value="2013" <c:if test="${selectYear==2013}">selected="true"</c:if>>2013</option>
                                <option value="2014" <c:if test="${selectYear==2014}">selected="true"</c:if>>2014</option>
                                <option value="2015" <c:if test="${selectYear==2015}">selected="true"</c:if>>2015</option>
                                <option value="2016" <c:if test="${selectYear==2016}">selected="true"</c:if>>2016</option>
                                <option value="2017" <c:if test="${selectYear==2017}">selected="true"</c:if>>2017</option>
                                <option value="2018" <c:if test="${selectYear==2018}">selected="true"</c:if>>2018</option>
                            </select>

                            &nbsp;月 &nbsp;
                            <select name="month" style="width: 100px;">
                                <option value="1" <c:if test="${selectMonth==1}">selected="true"</c:if>>1</option>
                                <option value="2" <c:if test="${selectMonth==2}">selected="true"</c:if>>2</option>
                                <option value="3" <c:if test="${selectMonth==3}">selected="true"</c:if>>3</option>
                                <option value="4" <c:if test="${selectMonth==4}">selected="true"</c:if>>4</option>
                                <option value="5" <c:if test="${selectMonth==5}">selected="true"</c:if>>5</option>
                                <option value="6" <c:if test="${selectMonth==6}">selected="true"</c:if>>6</option>
                                <option value="7" <c:if test="${selectMonth==7}">selected="true"</c:if>>7</option>
                                <option value="8" <c:if test="${selectMonth==8}">selected="true"</c:if>>8</option>
                                <option value="9" <c:if test="${selectMonth==9}">selected="true"</c:if>>9</option>
                                <option value="10" <c:if test="${selectMonth==10}">selected="true"</c:if>>10</option>
                                <option value="11" <c:if test="${selectMonth==11}">selected="true"</c:if>>11</option>
                                <option value="12" <c:if test="${selectMonth==12}">selected="true"</c:if>>12</option>
                            </select>
                            <input type="submit" value="搜索"/>
                        </div>
                    </form>
                    <c:forEach begin="1" end="${totalDays}" var="day">
                        <div class="_day <c:if test="${day == currentDay}">current</c:if>">
                            <div class="title">
                                <span>${day}</span>
                            </div>
                            <div class="content">
                                <c:set var="haveEvent" value="false"/>
                                <c:if test="${!haveEvent}">
                                    <c:if test="${day == 1}">
                                        <span class="tip">欢迎来到后台管理平台</span>
                                    </c:if>
                                </c:if>
                            </div>
                            <div class="right_corner"></div>
                        </div>
                    </c:forEach>
                </div>
            </td>
        </tr>
    </table>

</body>
</html>