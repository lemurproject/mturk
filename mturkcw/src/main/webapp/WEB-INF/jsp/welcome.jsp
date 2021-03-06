<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Query Relevance</title>
<link href="/css/main.css" rel="stylesheet">
</head>
<body>
	<h2 class="hello-title">Document Relevance</h2>
	<script src="/js/main.js"></script>

<form action="/test" method="get">
	<h3>Suppose you are having a conversation with Alexa, Siri, or
		Google Assitant, and you have asked these questions:</h3>
	<c:forEach items="${queryList}" var="query">
		<c:choose>
			<c:when test="${query == subQuery}">
				<div>
					<b>${query}</b>
				</div>
			</c:when>
			<c:otherwise>
				<div>${query}</div>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<br />
	<h3>
		<p>You will be shown 5 documents/answers in the upcoming task.  Please judge how 
		well each of these documents relates to the last question (in bold above) of 
		this conversation.</p>
		<br/>
		<br/>
		<input type = "submit" value = "Judge Documents"/>
	</h3>
</form>
</body>
</html>