<!DOCTYPE html>
<html>
<head runat="server">
    <title></title>
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="this is my page">
    <meta http-equiv="content-type" content="text/html; charset=gb2312">
    <script type="text/javascript" src="http://api.map.baidu.com/api?&v=1.3">
    </script>
</head>
<body>
<p>
    地址：<input id="txtSearch" type="text" />
    <input type="button" value="搜索" onclick="search()" /></p>

<div style="width: 800px; height: 600px; border: 1px solid gray;" id="container">

</div>
</body>
<script type="text/javascript">
    function $(id) {
        return document.getElementById(id); //定义$
    }
    var map = new BMap.Map("container"); //创建地图
    map.centerAndZoom(new BMap.Point(113.927832,22.496724), 15); //初始化地图


    var point = new BMap.Point(113.927832,22.496724);
    var marker = new BMap.Marker(point);
    var label = new BMap.Label('深圳-招商蛇口',{"offset":new BMap.Size(9,-15)});
    marker.setLabel(label);
    map.addOverlay(marker);


    map.enableScrollWheelZoom();  // 开启鼠标滚轮缩放    
    map.enableKeyboard();         // 开启键盘控制    
    map.enableContinuousZoom();   // 开启连续缩放效果    
    map.enableInertialDragging(); // 开启惯性拖拽效果  


    map.addControl(new BMap.NavigationControl()); //添加标准地图控件(左上角的放大缩小左右拖拽控件)  
    map.addControl(new BMap.ScaleControl());      //添加比例尺控件(左下角显示的比例尺控件)  
    map.addControl(new BMap.OverviewMapControl()); // 缩略图控件  
    map.addControl(new BMap.MapTypeControl());


    var city = new BMap.LocalSearch(map, { renderOptions: { map: map, autoViewport: true} }); //地图显示到查询结果处

    /*var city= new BMap.LocalSearch(map, {

     renderOptions: {map: map, panel: "r_result"}

     }); */ // 初始化带选择的下拉框的地图


    function search() {
        var s = $("txtSearch").value;

        city.setSearchCompleteCallback(onSearchComplete);  // 设置检索结束后的回调函数。参数： results: LocalResult 或  Array<LocalResult> ,如果是多关键字检索，回调    // 函数参数为LocalResult 的数组，数组中的结果顺序和检索中多关键字数组中顺序一致。
        city.search(s); //查找城市
    }


    function onSearchComplete(result){
        var n = result.getNumPois();   // 返回搜索结果数
    }

    map.addEventListener("click",function(e){   //单击地图，形成折线覆盖物
        newpoint = new BMap.Point(e.point.lng,e.point.lat);
        //    if(points[points.length].lng==points[points.length-1].lng){alert(111);}
        points.push(newpoint);  //将新增的点放到数组中
        polyline.setPath(points);   //设置折线的点数组
        map.addOverlay(polyline);   //将折线添加到地图上
        document.getElementById("info").innerHTML += "new BMap.Point(" + e.point.lng + "," + e.point.lat + "),</br>";    //输出数组里的经纬度

    });
</script>
</html> 