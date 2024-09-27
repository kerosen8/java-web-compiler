<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Registration</title>
</head>
<body>
    <form method="post" action="${pageContext.request.contextPath}/registration">
        <label for="email">Email:</label>
        <input type="email" name="email" id="email">
        <br>
        <label for="password">Password:</label>
        <input type="password" name="password" id="password">
        <br>
        <div style="color: red">
            <c:forEach var="error" items="${requestScope.errors}">
                <p>${error}</p>
            </c:forEach>
        </div>
        <button type="submit">Register</button>
    </form>
</body>
</html>
