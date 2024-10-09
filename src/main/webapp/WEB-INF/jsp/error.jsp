<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error</title>
</head>
<body>
    <b>${requestScope.statusCode} ERROR OCCURRED</b>
    <br>
    <a href="${pageContext.request.contextPath}/compiler">HOME</a>
</body>
</html>
