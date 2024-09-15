<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Compiler</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
    <c:if test="${sessionScope.user.getRole() == 'GUEST'}">
        <a href="${pageContext.request.contextPath}/login"><button>Sign in</button></a>
        <a href="${pageContext.request.contextPath}/registration"><button>Sign up</button></a>
    </c:if>
    <c:if test="${sessionScope.user.getRole() == 'USER'}">
        <p>You are logged in!</p>
        <form action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit">Logout</button>
        </form>
    </c:if>
    <br>
    <br>
    <form method="post" action="${pageContext.request.contextPath}/compiler">
        <label for="code">Input:</label>
        <br>
        <textarea spellcheck="false" name="code" id="code" rows="30" cols="80">${requestScope.code}</textarea>
        <br>
        <button type="submit">Compile</button>
    </form>
    <br>
    <p>Output:</p>
    <div>${requestScope.result}</div>
    <div>
        <p>Last compiles:</p>
        <c:forEach var="entry" items="${requestScope.lastCompiles}">
            <a href="${pageContext.request.contextPath}?selectedCompilation=${entry.key}">${entry.value}</a>
            <br>
        </c:forEach>
    </div>
    <script src="${pageContext.request.contextPath}/script.js"></script>
</body>
</html>
