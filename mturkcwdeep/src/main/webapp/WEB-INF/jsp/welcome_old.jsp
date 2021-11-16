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

	<h3>Suppose you are having a conversation with Alexa, Siri, or
		Google, and you ask these questions:</h3>
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
		Please click on each link below and judge whether that document is
		relevant to the <b>bold</b> question above.
	</h3>
	<c:forEach items="${documents}" var="doc">
		<div><a href="/document?docId=${doc.docId}&queryNum=${queryNum}&subQueryNum=${subQueryNum}">${doc}</a></div>
	</c:forEach>
</body>
</html>