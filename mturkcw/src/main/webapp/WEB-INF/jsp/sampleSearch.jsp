<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Query Relevance</title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css"
	integrity="sha384-HSMxcRTRxnN+Bdg0JdbxYKrThecOKuH5zCYotlSAcp1+c8xmyTe9GYg1l9a69psu"
	crossorigin="anonymous">
</head>
<body class="formstyle">
	<form:form action="./sampleResults" method="post"
		modelAttribute="searchObject">
		<input type="hidden" name="assignmentId" value="${assignmentId}"></input>
		<div align="center" class="divstyle">
			<div class="spanstyle"><b>Type a query that you have entered in a search engine within the past week:</b></div>
			<form:input path="queryString" size="100" id="queryInput"/>
		</div>
		<div align="center" class="divstyle">
			<div class="spanstyle"><b>Please describe within a few sentences what this query intended to find:</b></div>
			<form:textarea path="queryDescription" rows="5" cols="100" id="descriptionInput"/>
		</div>
		<div align="center">
			<input type="submit" name="submitButton" id="submitButton"
				class="btn btn-primary" value="Search" />
		</div>
	</form:form>
</body>
</html>