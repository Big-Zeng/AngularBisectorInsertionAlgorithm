<%--
  Created by IntelliJ IDEA.
  User: ZXF
  Date: 2018-12-08
  Time: 17:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TSP测试</title>
</head>
<body>

<div>

<form method="post" action="readATSP" enctype="multipart/form-data">
    选择一个Atsp文件:
    <input type="file" name="uploadFile" />
    <br/><br/>
    <input type="submit" value="上传" />
</form>
</div>

<form method="post" action="readATSPDir" enctype="multipart/form-data" style="border: double;">
    选择一个Atsp文件夹:
    <input type="file" name="file" webkitdirectory mozdirectory />
    <br/><br/>
    <input type="submit" value="提交文件夹" />
</form>


</body>
</html>
