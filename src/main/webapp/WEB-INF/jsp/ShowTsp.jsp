<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>

    <title>CRS.Simple example - Leaflet</title>

    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">


  <%--  <link rel="stylesheet" href="/EmbassyWarehouse/resource/css/leaflet.css"
           />--%>
    <script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.0.0-rc.3/leaflet.css" />
    <script src="/EmbassyWarehouse/resource/js/leaflet-src.js"></script>
   <%-- <script src="/EmbassyWarehouse/resource/js/leaflet-src.js"></script>--%>
<%--    <script src="https://unpkg.com/leaflet" type="text/javascript"></script>--%>

    <script src="/EmbassyWarehouse/resource/js/leaflet-ant-path.js" type="text/javascript"></script>
    <script src="/EmbassyWarehouse/resource/js/svg-icon.js"></script>
    <script src="/EmbassyWarehouse/resource/js/L.LabeledCircle-src.js"></script>
    <script src="/EmbassyWarehouse/resource/js/jquery.json.min.js"></script>
    <script src="/EmbassyWarehouse/resource/js/L.Control.MousePosition.js"></script>
    <script src="/EmbassyWarehouse/resource/js/L.LabelTextCollision.js"></script>
    <style>
        html, body {
            height: 100%;
            margin: 0;
        }
        #map {
            width: 100%;
            height: 100%;
            background: white;
        }
    </style>


</head>
<body>

<div id='map'></div>
<div style="position: fixed;top: 100px; left: 10px;z-index: 1000">
    <button onclick="createKeys('L');" style="display: block;margin-bottom: 5px;" >创建左边界</button>
    <button onclick="createKeys('R');" style="display: block;margin-bottom: 5px;" >创建右边界</button>
    <button onclick="createKeys('M');" style="display: block;margin-bottom: 5px;" >创建分割</button>


    <select id="direction" type="select">
        <option value ="0">东</option>
        <option value ="1">西</option>
        <option value="2">南</option>
        <option value="3">北</option>
    </select>
    <button onclick="reCreatePath();"  style="display: block;margin-bottom: 5px;" id="reCreate">重新生成路径</button>
    <button  id="C_virtual_Point" type="button"  style="margin-bottom: 5px;" >创建虚拟仓库</button>

</div>

<input type="hidden" id="points" data = '${requestScope.item}' >
<input type="hidden" id="result" data = '${requestScope.result}' >
<input type="hidden" id="keyPoints" data = '${requestScope.keyPoints}' >
<script>

    /*打开轮询*/
    $.fn.toggle = function( fn, fn2 ) {
        var args = arguments,guid = fn.guid || $.guid++,i=0,
                toggle = function( event ) {
                    var lastToggle = ( $._data( this, "lastToggle" + fn.guid ) || 0 ) % i;
                    $._data( this, "lastToggle" + fn.guid, lastToggle + 1 );
                    event.preventDefault();
                    return args[ lastToggle ].apply( this, arguments ) || false;
                };
        toggle.guid = guid;
        while ( i < args.length ) {
            args[ i++ ].guid = guid;
        }
        return this.click( toggle );
    };
    var labelTextCollision = new L.LabelTextCollision({
        collisionFlg : false
    });
    var map = L.map('map', {
        crs: L.CRS.Simple,
        maxZoom:10,
        minZoom:-10,

    });
    L.control.mousePosition().addTo(map);
    var thisPoints = [];
    var startPoint ;
    var path ;
    var PointGroup;
    var vitualPoint ;
    $(document).ready(function () {
        var result = $('#result').attr("data");
         //设置路径
        setPath(result);

        setPoints(result);

        $("#C_virtual_Point").toggle(function () {
            map.on('click', function(e) {
                if(vitualPoint!=undefined){
                    map.removeLayer(vitualPoint);
                }
                vitualPoint =  new L.Marker(e.latlng).addTo(map);
            });
        },
                function () {
                    map.off('click');
                    $.ajax({
                        url:"VirtualPoint",
                        type:"POST",
                        data:{
                            points:$('#result').attr("data"),
                            virtualPoint:JSON.stringify(vitualPoint.getLatLng()),
                            dir:$("#direction").find("option:selected").val()

                        },
                        success:function (data) {
                            var result=  data.data;
                            path.clearLayers();
                            PointGroup.clearLayers();
                            thisPoints = [];
                            setPoints(result);
                            setPath(result);
                            setPointStyle();
                        },
                        error:function () {
                            console.log("error");
                        },
                    });

                }

        );

        //四个关键点
        var keyPointJsonStr = $('#keyPoints').attr("data");
        if(keyPointJsonStr != ""){
            var keyPoints = jQuery.parseJSON(keyPointJsonStr);
            startPoint = keyPoints[0];

            $(keyPoints).each(function (i, value) {
                if(value.y == undefined) {
                    return true;
                }
                var pos1 = [value.y, value.x];
                var geoCoordinates = pos1;
                if(value.name == "mid"){
                    geoCoordinates = [startPoint.y,startPoint.x];
                }else{
                    pos1 =  [startPoint.y,startPoint.x];
                }
             // createOnePoint(pos1,geoCoordinates,"#7B68EE",value.name);

                //marker1.bindPopup("<label><span>location:"+value.x+";"+value.y+"</span></label>").openPopup();
            });
        }



    });


   //设置样式

    window.onload = function () {
      //  setPointStyle();
        $('svg').first().css("z-index","1");
    };
   function setPointStyle() {
      /* $(thisPoints).each(function (i, value) {
           $(value._marker._path).attr("stroke", "#000000");
           $(value._marker._path).attr("stroke-width", "#000000");
       });*/
   }
    function setCollisionDetection(flg) {
        labelTextCollision.options.collisionFlg = flg;
        map.fitBounds(map.getBounds());
    }

    //设置点
    function setPoints(result) {
        var layers = L.featureGroup().addTo(map);
                $(JSON.parse(result)).each(function (i, value) {
            if(value.userLocation == null|| value.userLocation == undefined){
                return true;
            }

            var pos1 = [value.userLocation.y, value.userLocation.x];

             layers.addLayer(setCircle(value.userLocation.y, value.userLocation.x,value.addOrder,i,value.areaInnerId).bindPopup("<label>This ID = "+value.areaInnerId+"<span>location:"+value.userLocation.x+";"+value.userLocation.y+"</span></label>").openPopup());


        });
        PointGroup= layers;

    }
    
    
    function setCircle(lat,lng,text,i,areaInnerId) {
        var latlng = L.latLng(lat, lng);
        console.log(lng+" "+lat+" "+text);
        if(i == 0) {
            var myIcon = L.icon({
                iconUrl: '/EmbassyWarehouse/resource/img/store.png',
                iconSize: [16, 16],
              iconAnchor: [8, 10],
              //  popupAnchor: [-3, -76],
            });
            return new L.marker(latlng,{icon:myIcon});
        }else {
           var off =  burma14(areaInnerId);
            var marker1=  new L.circleMarker(latlng,{
                radius:3,
                text:i,
                fillColor:"#000000",
                color:"#00000",
                fill:true,
                textColor:"#000000",
                fillOpacity:1,
                offsetX:off[0],
                offsetY:off[1],
                renderer : labelTextCollision
            });
            return marker1;
        }
    };


    /*******************************************************************************/
    var zoomValue = 0;
    function  ulysses(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text == "14"){
             offsetX = -15;
             offsetY = 15;
        }
        if(text == "17"){
            offsetX = -20;
            offsetY = 10;
        }
        if(text == "13"){
            offsetX = -15;
            offsetY = 15;
        }
        if(text == "2"){
            offsetX = -15;
        }
        if(text == "5"){
            offsetY = 15;
        }
        if(text == "6"){
            offsetY = 15;
            offsetX = 0;
        }
        if(text == "12"){
            offsetY = 15;
            offsetX = 0;
        }

        zoomValue=4;
        return [offsetX,offsetY];
    }

    function  ulysses16(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text=="16") {
            offsetX = -20;
        }
        zoomValue=4;
        return [offsetX,offsetY];
    }
    function Oliver30(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text =="9"){
            offsetX = -15;
        }
        if(text =="13"){
            offsetX = -10;
            offsetY = -10;
        }
        if(text =="15"){
            offsetX = -20;
        }
        if(text =="29"){
            offsetX = -23;
        }
        if(text =="24"){
            offsetY = -10;
        }
        return [offsetX,offsetY];
    }

    function Fri26(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text =="7"){
            offsetX = -15;
        }
        if(text =="23"){
            offsetY = 10;
        }
        if(text =="15"){
            offsetX = -20;
        }
        if(text =="29"){
            offsetX = -23;
        }
        if(text =="24"){
            offsetY = -10;
        }
        return [offsetX,offsetY];
    }

    function Gr17(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text =="17"){
            offsetX = -15;
            offsetY = 17;
        }
        if(text =="16"){
            offsetX = -20;
            offsetY = 17;
        }
        return [offsetX,offsetY];
    }
    
    function Gr24(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text =="11"){
            offsetX = -20;
        }
        if(text =="16"){
            offsetY = 10;
        }
        if(text =="20"){
            offsetY = -10;
            offsetX = -10;
        }
        if(text == "7"){
            offsetY = -10;
            offsetX = -10;
        }
        return [offsetX,offsetY];
    }

    function Gr48(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text =="20"){
            offsetX = -20;
        }
        if(text =="36"){
            offsetX = -20;
        }
        if(text =="2"){
            offsetX = -20;
        }
        return [offsetX,offsetY];
    }
    
    function burma14(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text =="6"){
            offsetX = -20;
            offsetY = 0;
        }
        if(text =="8"){
            offsetX = -5;
            offsetY = -15;
        }
        if(text =="10"){

            offsetY = 20;
        }
        return [offsetX,offsetY];
    }
    function brazil58(text){
        var offsetX = 10;
        var offsetY = 0;
        if(text == "15"){
            offsetX = 10;
        }



    }


    function dantzig42(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text=="27"){
            offsetX = -20;
        }
        if(text=="42"){
            offsetX = -25;
            offsetY = 10;
        }
        return [offsetX,offsetY];
    }
    function  swiss42(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text=="27"){
            offsetX = -20;
        }
        if(text=="42"){
            offsetX = -25;
            offsetY = 10;
        }
        if(text=="32"){
            offsetX = -25;
            offsetY = -5;
        }
        if(text=="7"){
            offsetY = 5;
        }
        return [offsetX,offsetY];
    }

    function att48(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text=="8"){
            offsetX = -20;
        }
        if(text=="24"){
            offsetX = -20;
        }
        if(text=="23"){
            offsetX = -20;
        }
        if(text=="26"){
            offsetX = -20;
        }
        if(text=="20"){
            offsetY =15;
        }
        if(text=="17"){
            offsetY =15;
            offsetX =3;
        }
        if(text=="15"){
            offsetY =10;
        }
        if(text=="5"){
            offsetY =7;
        }
        if(text=="7"){
            offsetX =5;
        }
        if(text=="22"){
            offsetY =-5;
        }
        return [offsetX,offsetY];
    }

    function NONE(text) {
        var offsetX = 10;
        var offsetY = 0;
        return [offsetX,offsetY];
    }
    function KroA100(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text == "62") {
            offsetX = -20;
        }
        if(text == "63") {
            offsetX = 0;
            offsetY = 15;
        }
        if(text == "49") {
            offsetX = -30;
        }
        if(text == "95") {
            offsetX = -30;
        }
        if(text == "53") {
            offsetY = 10;
        }
        if(text == "31") {
            offsetY = 10;
            offsetX = -10;
        }
        if(text == "71") {
            offsetY = 15;
        }
        if(text == "21") {
            offsetY = -10;
            offsetX = 0;
        }
        if(text == "74") {
            offsetY = 10;
        }
        if(text == "70") {
            offsetY = 10;
            offsetX = 0;
        }
        if(text == "6") {
            offsetY = -10;
            offsetX = 0;
        }
        if(text == "12") {
            offsetY = -10;
            offsetX = 0;
        }
        if(text == "31") {
            offsetY = -10;
            offsetX = -10;
        }
        if(text == "70") {
            offsetY = 20;
        }
        return [offsetX,offsetY];
    }

    function berlin52(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text=="43"){
            offsetX = -5;
            offsetY = -10;
        }
        if(text=="29"){
            offsetX = -20;
            offsetY = 5;
        }
        if(text=="25"){
            offsetX = -15;
            offsetY = -5;
        }
        if(text=="22"){
            offsetX = -15;
            offsetY = -8;
        }
        if(text=="21"){
            offsetX = -10;
            offsetY = -8;
        }
        if(text=="17"){
            offsetX = 5;
        }
        if(text=="30"){
            offsetY = 18;
            offsetX =0;
        }
        if(text=="23"){
            offsetX = 5;
            offsetY = -5;
        }
        if(text=="34"){
            offsetY = 18;
            offsetX = 0;
        }
        if(text=="20"){
            offsetY = -10;
            offsetX = 0;
        }
        if(text=="31"){
            offsetY = 10;
        }
        if(text=="27"){
            offsetY = 13;
            offsetX = 7;
        }
        if(text=="28"){
            offsetY = -8;
            offsetX = 0;
        }
        return [offsetX,offsetY];
    }
    function St70(text) {
        var offsetX = 10;
        var offsetY = 0;
        if(text=="26"){
            offsetX = -20;
        }
        if(text=="54"){
            offsetY = 10;
        }
        if(text=="58"){
            offsetY = 10;
        }
        if(text=="60"){
            offsetX = -20;
        }
        if(text=="70"){
            offsetY = 10;
        }

        if(text=="35"){
            offsetY = -10;
            offsetX = -10;
        } if(text=="38"){
            offsetY = 10;
        }
        if(text=="50"){
            offsetY = 10;
        }
        if(text=="63"){
            offsetY = 15;
            offsetX = -5;
        }
        if(text=="32"){
            offsetY = 10;
            offsetX = 0;
        }
        if(text=="62"){
            offsetY = -10;
            offsetX = 0;
        }
        return [offsetX,offsetY];
    }
    
    /*********************************************************************************/
    


    function setPath(pointsJson){
        var resulutPoints = [];
        var datas = JSON.parse(pointsJson);
        $(datas).each(function (i, value) {
            if(value.userLocation == null || value.userLocation == undefined){
                resulutPoints.push([datas[0].userLocation.y,datas[0].userLocation.x]);
            }else
            resulutPoints.push([value.userLocation.y, value.userLocation.x]);
        });

        //设置路线
         path = L.polyline.antPath(resulutPoints,
                {"delay":10,"dashArray":[1,1],"weight":3,"color":"#000000","pulseColor":"#000000","paused":false,"reverse":false}
        ).addTo(map);
      //  var pathGroup = L.layerGroup(path2).addTo(map);
        //map.addLayer(pathGroup);
        map.setView( [path.getBounds().getCenter().lat,path.getBounds().getCenter().lng],zoomValue);

    }


    /**
     * 创建关键点
     * */

    var L_Point,R_Point,M_Point=[];


    function createKeys(text) {
        var point =  [startPoint.y,startPoint.x];
        var marker = createOnePoint(point, point, "#FFFF00", text);
      //   createKeyPoints.push({name:text,point:createOnePoint(point, point, "#FFFF00", text)});
        //curFlag++;
        switch (text){
            case "L":
                L_Point = marker;
                break;
            case "R":
                R_Point = marker;
                break;
            case "M":
                M_Point.push(marker);
                break;
        }
    }



    /**
     * 创建一个带线点
     * @param labelPosition
     * @param coordinates
     * @param color
     * @param text
     */
    function createOnePoint(labelPosition,coordinates,color,text) {
      //  console.log(labelPosition);
       // console.log(coordinates);
         //  new L.Polyline([labelPosition,coordinates]).addTo(map);
        var marker1 = new L.LabeledCircleMarker(coordinates.slice(), {
            "type": "Feature",
            "properties": {
                "text":text,
                "labelPosition": labelPosition.slice().reverse()
            },

            "geometry": {
                "type": "Point",
                "coordinates": coordinates
            },
        }, {
            markerOptions: {  radius:12,color:color  },
            interactive: false
        }).addTo(map);

        return marker1;
      /*  var pos1 = [114.1952, 22.42658];
        var marker1 =new L.LabeledCircleMarker(pos1.slice().reverse(), {
            "type": "Feature",
            "properties": {
                "text": "yolo",
                "labelPosition": [114.29819682617189, 22.477347822506356]
            },
            "geometry": {
                "type": "Point",
                "coordinates": pos1
            }
        }, {
            markerOptions: { color: '#050' },
            interactive: true
        }).addTo(map);*/



        //   return marker1;
    }

    //重新生成路径
    function reCreatePath() {
        var M_PointData = [];
        var L_PointData = {end:L_Point._marker.getLatLng()};
        var R_PointData = {end:R_Point._marker.getLatLng()};
        $(M_Point).each(function (i, value) {
            var end = value._marker.getLatLng();
            var data = {
                end:end,
            };
            M_PointData.push(data);
        });
        var M_PointStr = JSON.stringify(M_PointData);
        var L_PointStr = JSON.stringify(L_PointData);
        var R_PointStr = JSON.stringify(R_PointData);
      /*  var str = $("#direction").change(function () {
           console.log ($("#direction").find("option:selected").text());



        });*/
      $.ajax({
         url:"reCreatePath",
          type:"POST",
          data:{
              points:$('#result').attr("data"),
              Lpoint:L_PointStr,
              Rpoint:R_PointStr,
              Mpoint:M_PointStr,
              direction:$("#direction").find("option:selected").val()
          },
          success:function (data) {
            var result=  data.data;
              path.clearLayers();
              PointGroup.clearLayers();
              thisPoints = [];
              setPoints(result);
              setPath(result);
             /* $(JSON.parse(result)).each(function (i, value) {
                  if(value.length!=0){
                      //console.log (JSON.stringify(value));
                      var json = JSON.stringify(value);
                      setPoints(json);
                      setPath(json);
                  }

              });*/
              setPointStyle();
          },
          error:function () {
            console.log("error");
          },
      });
    }




</script>



</body>
</html>
