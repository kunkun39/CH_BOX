<%@ page contentType="text/html; charset=gb2312"%>
<!DOCTYPE html>
<html>
<head>
    <title>百度地图javascript</title>
    <script type="text/javascript">
        function initialize() {
            var lng = document.getElementById('x').value;
            var lat = document.getElementById('y').value;
            createMap(lng, lat); //创建地图
        }

        function createMap(lng, lat) {
            //在百度地图容器中创建一个地图
            var point = new BMap.Point(lng, lat); //定义一个中心点坐标
//            map.centerAndZoom(point, 17); //设定地图的中心点和坐标并将地图显示在地图容器中
//            window.map = map; //将map变量存储在全局

            var mp = new BMap.Map('map');
            mp.centerAndZoom(point, 15);
        }

        function loadScript() {
            var script = document.createElement("script");
            script.src = "http://api.map.baidu.com/api?v=1.4&callback=initialize";
            document.body.appendChild(script);
        }

        window.onload = loadScript;
    </script>
</head>
<body>
<p>
    经度：<input id="x" type="text" value="${lon}"/>  &nbsp; &nbsp; 维度：<input id="y" type="text" value="${lat}"/>
    <%--<input type="button" value="搜索" onclick="initialize()" />--%>
</p>
<div id="map" style="width: 1500px; height: 1000px"></div>
</body>
</html> 