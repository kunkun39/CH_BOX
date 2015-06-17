<%--
  Created by IntelliJ IDEA.
  User: kerio
  Date: 2015/6/8
  Time: 17:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<html>
<head>
    <title>用户信息收集</title>
    <script src="${pageContext.request.contextPath}/js/jquery-1.7.2.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/highslide/highcharts.js"></script>
    <script src="${pageContext.request.contextPath}/js/highslide/exporting.js"></script>
    <script src="${pageContext.request.contextPath}/js/highslide/dark-green.js"></script>
    <script src="${pageContext.request.contextPath}/js/highslide/collector/feedback.js"></script>
    <script src="${pageContext.request.contextPath}/js/highslide/export_csv.js"></script>
    <script src="${pageContext.request.contextPath}/dwr/engine.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/dwr/util.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/dwr/interface/SystemDWRHandler.js" type="text/javascript"></script>
</head>
<body>
<div class="action">
    &nbsp;
</div>
<table cellpadding="0" cellspacing="0" width="100%" class="box">
    <tr>
        <td width="200" valign="top" style="background: #e8e8e8;border-right: 1px solid #CCC;">
            <jsp:include page="../statisticcollector.jsp"/>
        </td>

        <td valign="top">
            <form action="#" class="search_form" method="POST">
                <div class="search">
                    <span>
                        反馈信息:
                    </span>
                    <span>
                        频道名:
                        <select id="tvChannelName" style="width: 200px;">
                            <c:forEach items="${tvChannelInfos}" var="tvChannelInfo">
                                <option value="${tvChannelInfo.tvChannelName}" >${tvChannelInfo.tvChannelName}</option>
                            </c:forEach>
                        </select>
                    </span>
                    <span>
                        时间:
                        <select id="reportYear" style="width: 80px;">
                            <option value="2015" <c:if test="${reportYear==2015}">selected="true"</c:if>>2015</option>
                            <option value="2016" <c:if test="${reportYear==2016}">selected="true"</c:if>>2016</option>
                            <option value="2017" <c:if test="${reportYear==2017}">selected="true"</c:if>>2017</option>
                            <option value="2018" <c:if test="${reportYear==2018}">selected="true"</c:if>>2018</option>
                            <option value="2019" <c:if test="${reportYear==2019}">selected="true"</c:if>>2019</option>
                            <option value="2020" <c:if test="${reportYear==2020}">selected="true"</c:if>>2020</option>
                        </select>
                        &nbsp;年
                    </span>
                    <span>
                        <select id="reportMonth" style="width: 80px;">
                            <option value="0" <c:if test="${reportMonth==0}">selected="true"</c:if>>全年</option>
                            <option value="1" <c:if test="${reportMonth==1}">selected="true"</c:if>>1</option>
                            <option value="2" <c:if test="${reportMonth==2}">selected="true"</c:if>>2</option>
                            <option value="3" <c:if test="${reportMonth==3}">selected="true"</c:if>>3</option>
                            <option value="4" <c:if test="${reportMonth==4}">selected="true"</c:if>>4</option>
                            <option value="5" <c:if test="${reportMonth==5}">selected="true"</c:if>>5</option>
                            <option value="6" <c:if test="${reportMonth==6}">selected="true"</c:if>>6</option>
                            <option value="7" <c:if test="${reportMonth==7}">selected="true"</c:if>>7</option>
                            <option value="8" <c:if test="${reportMonth==8}">selected="true"</c:if>>8</option>
                            <option value="9" <c:if test="${reportMonth==9}">selected="true"</c:if>>9</option>
                            <option value="10" <c:if test="${reportMonth==10}">selected="true"</c:if>>10</option>
                            <option value="11" <c:if test="${reportMonth==11}">selected="true"</c:if>>11</option>
                            <option value="12" <c:if test="${reportMonth==12}">selected="true"</c:if>>12</option>
                        </select>
                        &nbsp;月
                    </span>
                    <span>
                        报表类型:
                        <select id="reportType" style="width: 100px;">
                            <option value="1" <c:if test="${reportType=='1'}">selected="true"</c:if>>柱状图</option>
                        </select>
                        &nbsp;
                    </span>
                    <input type="button" value="统计" onclick="generateReport(); "/>
                </div>
            </form>

            <table cellpadding="0" cellspacing="0" width="100%">
                <tr>
                    <td>
                        &nbsp;
                    </td>
                </tr>
                <tr id="container2_tr" style="width: 90%; height: 350px; ">
                    <td>
                        <div id="container2" style="width:80%; height: 350px; padding-left: 20px"></div>
                        <p style="margin-left:20px;">
                            <button id="collectorcsv">获取CVS数据</button> <button id="collectorexcel">导出Excel文件</button>
                        </p>
                    </td>
                </tr>
                <tr>
                    <td>
                        &nbsp;
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<script type="text/javascript">

    function renew_sta_container(tvChannelName, year, month, reportType) {
        jQuery("#container2_tr").show();

        if("0" == month) {
            //全年的报表
            SystemDWRHandler.obtainCollectorInfoAmountByProgram(tvChannelName, year, month, function(result) {
                var statisticData = JSON.parse(result);
                var total = statisticData[0].total.split(",");

                var programs = statisticData[0].series.split(",");
                sta_container2.xAxis.categories = programs;

                var newData = new Array();

                for(var i=0; i<total.length; i++) {
                    newData[i] = parseInt(total[i]);
                }
                sta_container2.series[0].data = newData;

                sta_container2.title.text = "频道 " + tvChannelName +" "+ year  + " 收视排行" ;

                new Highcharts.Chart(sta_container2);
            });
        } else {
            //一个月的报表
            SystemDWRHandler.obtainCollectorInfoAmountByProgram(tvChannelName, year, month, function(result) {
                var statisticData = JSON.parse(result);
                var total = statisticData[0].total.split(",");

                var programs = statisticData[0].series.split(",");
                sta_container2.xAxis.categories = programs;

                var newData = new Array();
                for(var i=0; i<total.length; i++) {
                    newData[i] = parseInt(total[i]);
                }
                sta_container2.series[0].data = newData;

                sta_container2.title.text = "频道 " + tvChannelName +" "+year +"年" + month + "月" + " 收视排行" ;

                new Highcharts.Chart(sta_container2);
            });
        }
    }

    function generateReport() {
        var tvChannelName=jQuery("#tvChannelName").val();
        var year = jQuery("#reportYear").val();
        var month = jQuery("#reportMonth").val();
        var reportType = jQuery("#reportType").val();
        renew_sta_container(tvChannelName, year, month, reportType);
    }

</script>
</body>
</html>
