<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
        <p>${requestScope.pass}</p>
    </c:if>
    <br>
    <br>
    <form method="post" action="${pageContext.request.contextPath}/compiler" id="codeForm">
        <label for="code">Input:</label>
        <br>
        <textarea spellcheck="false" name="code" id="code" rows="30" cols="80">${requestScope.code}</textarea>
        <br>
        <button type="submit" name="action" value="compile">Compile</button>
        <c:if test="${sessionScope.user.getRole() == 'USER'}">
            <button type="button" name="action" value="save" onclick="showSaveModal()">Save</button>
            <button type="submit" name="action" value="download">Download</button>
        </c:if>
    </form>
    <div id="saveModal" style="display:none;">
        <div>
            <label for="fileName">Enter file name:</label>
            <input type="text" id="fileName" name="fileName" />
            <button onclick="submitForm()">Submit</button>
            <button onclick="closeModal()">Cancel</button>
        </div>
    </div>
    <c:if test="${sessionScope.user.getRole() == 'USER'}">
        <div class="favorite-container">
            <p>Favorite compilations:</p>
        </div>
    </c:if>
    <br>
    <p>Output:</p>
    <p><c:out value="${requestScope.result}" escapeXml="false"/></p>
    <div class="last-compilations-container">
        <p>Last compilations:</p>
        <c:forEach var="entry" items="${sessionScope.latestCompilations}">
            <a href="${pageContext.request.contextPath}?selectedCompilation=${entry.key}"><fmt:formatDate value="${entry.value.getCompilationTime()}" pattern="HH:mm:ss"/></a>
            <br>
        </c:forEach>
    </div>
    <script src="${pageContext.request.contextPath}/script.js"></script>
</body>
</html>
