<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Compiler</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
    <c:if test="${sessionScope.user.getRole() == 'GUEST'}">
        <a href="${pageContext.request.contextPath}/login"><button>Sign in</button></a>
        <a href="${pageContext.request.contextPath}/registration"><button>Sign up</button></a>
        <br>
        <br>
    </c:if>
    <c:if test="${sessionScope.user.getRole() == 'USER'}">
        <form action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit">Logout</button>
        </form>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/compiler" id="codeForm">
        <label for="code">Code:</label>
        <br>
        <textarea spellcheck="false" name="code" id="code" rows="30" cols="80">${requestScope.code}</textarea>
        <br>
        <button type="submit" name="action" value="compile">Compile</button>
        <c:if test="${sessionScope.user.getRole() == 'USER'}">
            <button type="button" name="action" value="save" onclick="showSaveModal()">Save</button>
            <button type="submit" name="action" value="download">Download</button>
        </c:if>
        <br>
        <label for="input">Input:</label>
        <br>
        <textarea spellcheck="false" name="input" id="input" rows="5" cols="80">${requestScope.input}</textarea>
    </form>
    <div id="saveModal" style="display:none;">
        <div>
            <label for="savedCodeTitle">Enter code name:</label>
            <input type="text" id="savedCodeTitle" name="savedCodeTitle"/>
            <button onclick="submitForm()">Submit</button>
            <button onclick="closeModal()">Cancel</button>
        </div>
    </div>
    <c:if test="${sessionScope.user.getRole() == 'USER'}">
        <div class="favorite-container">
            <p>My codes:</p>
            <c:forEach var="code" items="${sessionScope.savedCodes}">
                <a href="${pageContext.request.contextPath}/compiler?savedCodeTitle=${code.getTitle()}">${code.getTitle()}</a>
                <a href="${pageContext.request.contextPath}/compiler?deleteSavedCode=${code.getId()}">Delete</a>
                <br>
            </c:forEach>
        </div>
    </c:if>
    <br>
    <p>Output:</p>
    <p><c:out value="${requestScope.result}" escapeXml="false"/></p>
    <div class="last-compilations-container">
        <p>Recent codes:</p>
        <c:forEach var="entry" items="${sessionScope.recentCodes}">
            <a href="${pageContext.request.contextPath}?recentCodeNumber=${entry.key}"><fmt:formatDate value="${entry.value.getCompilationTime()}" pattern="HH:mm:ss"/></a>
            <br>
        </c:forEach>
    </div>
    <script src="${pageContext.request.contextPath}/script.js"></script>
</body>
</html>
