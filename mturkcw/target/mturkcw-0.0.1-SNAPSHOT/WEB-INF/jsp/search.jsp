<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Query Relevance</title>
<link href="/css/main.css" rel="stylesheet">
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css" integrity="sha384-HSMxcRTRxnN+Bdg0JdbxYKrThecOKuH5zCYotlSAcp1+c8xmyTe9GYg1l9a69psu" crossorigin="anonymous">
</head>
<body>
	<h2 class="hello-title">Search Relevance</h2>
	<script src="/js/main.js"></script>

<form:form action="./searchResults" method="post" modelAttribute="searchObject">
	<div align="center">
	<form:input path="queryString" size="100"/>&nbsp;&nbsp;&nbsp;<input type="submit" class="btn btn-primary" value="Search"/>
	</div>
</form:form>
</body>
</html>