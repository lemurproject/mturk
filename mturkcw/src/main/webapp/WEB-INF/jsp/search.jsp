<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Query Relevance</title>
<link href="/css/main.css" rel="stylesheet">
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css"
	integrity="sha384-HSMxcRTRxnN+Bdg0JdbxYKrThecOKuH5zCYotlSAcp1+c8xmyTe9GYg1l9a69psu"
	crossorigin="anonymous">
</head>
<body>
	<h2 class="hello-title">Search Relevance</h2>
	<script src="/js/main.js"></script>


	<table border="0">
		<tr>
			<td><img src="lemur150.gif" alt="Lemur Project Logo"
				height="119px" width="150px"></td>
			<td width="15px">&nbsp;</td>
			<td align=center valign=center><font size="+2" color=#0000FF><strong>ClueWeb09
						Category B</strong></font></br>
				<form action="lemur.cgi" method=GET>
					<input type="hidden" name="x" value="false"><input
						type="text" name="q" size="100%" value="horse hooves"><input
						type="submit" value="Search">
				</form></td>
			<td width="15px">&nbsp;</td>
			<td valign=center>
				<ul>
					<li><a href="help-qry.html">Query language help</a></li>
					<li><a href="help-db.html">Documents in database</a></li>
					<li><A href="lemur.cgi?h=">CGI query language help</a></li>
				</ul>
			</td>
		</tr>
	</table>

	<form:form action="./searchResults" method="post"
		modelAttribute="searchObject">
		<div align="center">
			<form:input path="queryString" size="100" />
			&nbsp;&nbsp;&nbsp;<input type="submit" class="btn btn-primary"
				value="Search" />
		</div>
	</form:form>
</body>
</html>