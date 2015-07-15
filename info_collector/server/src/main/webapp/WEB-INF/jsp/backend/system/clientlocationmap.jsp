<%@ page contentType="text/html; charset=gb2312"%>
<!DOCTYPE html>
<html>
<head>
    <title>�ٶȵ�ͼjavascript</title>
    <script type="text/javascript">
        function initialize() {
            var lng = document.getElementById('x').value;
            var lat = document.getElementById('y').value;
            createMap(lng, lat); //������ͼ
        }

        function createMap(lng, lat) {
            //�ڰٶȵ�ͼ�����д���һ����ͼ
            var point = new BMap.Point(lng, lat); //����һ�����ĵ�����
//            map.centerAndZoom(point, 17); //�趨��ͼ�����ĵ�����겢����ͼ��ʾ�ڵ�ͼ������
//            window.map = map; //��map�����洢��ȫ��

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
    ���ȣ�<input id="x" type="text" value="${lon}"/>  &nbsp; &nbsp; ά�ȣ�<input id="y" type="text" value="${lat}"/>
    <%--<input type="button" value="����" onclick="initialize()" />--%>
</p>
<div id="map" style="width: 1500px; height: 1000px"></div>
</body>
</html> 