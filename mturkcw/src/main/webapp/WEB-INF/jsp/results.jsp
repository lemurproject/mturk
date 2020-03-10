<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Query Relevance</title>
<link href="/css/main.css" rel="stylesheet">
</head>
<body>
	<h2 class="hello-title">Search Results</h2>
	<script src="/js/main.js"></script>

<form action="/results" method="get">
	<c:forEach items="${fulldocs}" var="document">
				<div><a href="./document?docid=${document.docId}">${document.title}</a></div>
				<div>${document.docId}&nbsp;${document.url}</div>
				<div>${document.snippet}</div><br/>
	</c:forEach>
</form>
</body>
</html>