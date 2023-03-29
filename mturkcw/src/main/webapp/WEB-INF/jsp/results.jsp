<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	crossorigin="anonymous"></link>
<style type="text/css" media="screen">
.formstyle {
	margin-top: 10px;
	margin-bottom: 20px;
	margin-left: 50px;
	margin-right: 50px;
}

.tablestyle {
	width: 60%;
}

.cellstyle {
	padding-left: 5px;
	padding-right: 5px;
	padding-bottom: 5px;
}

.snippetcellstyle {
	padding-left: 5px;
	padding-right: 5px;
	padding-bottom: 25px;
}

.titlestyle {
	font-size: 125%;
	color: #285cb0;
}

.urlstyle {
	font-size: 80%;
	color: gray;
}
.snippetstyle {
	font-size: 90%;
}

.instructionstyle {
	width: 60%;
	margin-top: 10px;
	font-size: 150%;
	color: blue;
	font-size: 150%;
}

.buttonstyle {
	
}
</style>
</head>
<body class="formstyle">
	<form:form
		action="${searchResult.submitUrl}"
		method="post" modelAttribute="searchResult">
		<form:input path="assignmentId" type="hidden"
			value="${searchResult.assignmentId}" />
			
		<form:input path="prevQuery" type="hidden"
			value="${searchResult.prevQuery}" />
		<form:input path="prevDescription" type="hidden"
			value="${searchResult.prevDescription}" />
		<form:input path="prevCategory" type="hidden"
			value="${searchResult.prevCategory}" />
		<form:input path="prevDoc1id" type="hidden" value="${searchResult.prevDoc1id}" />
		<form:input path="prevDoc1selection" type="hidden" value="${searchResult.prevDoc1selection}" />
		<form:input path="prevDoc2id" type="hidden" value="${searchResult.prevDoc2id}" />
		<form:input path="prevDoc2selection" type="hidden" value="${searchResult.prevDoc2selection}" />
		<form:input path="prevDoc3id" type="hidden" value="${searchResult.prevDoc3id}" />
		<form:input path="prevDoc3selection" type="hidden" value="${searchResult.prevDoc3selection}" />
		<form:input path="prevDoc4id" type="hidden" value="${searchResult.prevDoc4id}" />
		<form:input path="prevDoc4selection" type="hidden" value="${searchResult.prevDoc4selection}" />
		<form:input path="prevDoc5id" type="hidden" value="${searchResult.prevDoc5id}" />
		<form:input path="prevDoc5selection" type="hidden" value="${searchResult.prevDoc5selection}" />
		<form:input path="prevDoc6id" type="hidden" value="${searchResult.prevDoc6id}" />
		<form:input path="prevDoc6selection" type="hidden" value="${searchResult.prevDoc6selection}" />
		<form:input path="prevDoc7id" type="hidden" value="${searchResult.prevDoc7id}" />
		<form:input path="prevDoc7selection" type="hidden" value="${searchResult.prevDoc7selection}" />
		<form:input path="prevDoc8id" type="hidden" value="${searchResult.prevDoc8id}" />
		<form:input path="prevDoc8selection" type="hidden" value="${searchResult.prevDoc8selection}" />
		<form:input path="prevDoc9id" type="hidden" value="${searchResult.prevDoc9id}" />
		<form:input path="prevDoc9selection" type="hidden" value="${searchResult.prevDoc9selection}" />
		<form:input path="prevDoc10id" type="hidden" value="${searchResult.prevDoc10id}" />
		<form:input path="prevDoc10selection" type="hidden" value="${searchResult.prevDoc10selection}" />
		<form:input path="prevDoc11id" type="hidden" value="${searchResult.prevDoc11id}" />
		<form:input path="prevDoc11selection" type="hidden" value="${searchResult.prevDoc11selection}" />
		<form:input path="prevDoc12id" type="hidden" value="${searchResult.prevDoc12id}" />
		<form:input path="prevDoc12selection" type="hidden" value="${searchResult.prevDoc12selection}" />
		<form:input path="prevDoc13id" type="hidden" value="${searchResult.prevDoc13id}" />
		<form:input path="prevDoc13selection" type="hidden" value="${searchResult.prevDoc13selection}" />
			
		<form:input path="assignmentId" type="hidden"
			value="${searchResult.assignmentId}" />
		<form:input path="query" type="hidden" value="${searchResult.query}" />
		<form:input path="description" type="hidden"
			value="${searchResult.description}" />
		<form:input path="category" type="hidden"
			value="${searchResult.category}" />
		<form:input path="queryTime" type="hidden"
			value="${searchResult.queryTime}" />
			<form:input path="inputTime" type="hidden"
			value="${searchResult.inputTime}" />
		<form:input path="filteredDocs" type="hidden"
			value="${searchResult.filteredDocs}" />
		<div align="left">
			<label>Searched for: </label>&nbsp;&nbsp;
			<form:input path="query" size="100" disabled="true" />
		</div>
		<div class="instructionstyle"> 
		Place a check mark next to every result that you would open. <br/>
		Note the results are in RANDOM order. <br/>
		</div>
		<hr />
		<table class="tablestyle">
			<col width="10">
			<c:forEach items="${searchResult.documents}" var="document"
				varStatus="tagStatus">
				<form:input path="documents[${tagStatus.index}].docId" type="hidden"
					value="${document.docId}" />
				<form:input path="documents[${tagStatus.index}].score" type="hidden"
					value="${document.score}" />
				<tr>
					<td></td>
					<td class="cellstyle"><div class="urlstyle">${document.url}</div></td>
				</tr>
				<tr>
					<td class="cellstyle"><form:checkbox
							path="documents[${tagStatus.index}].selected"
							id="${document.docId}" name="${document.docId}" value="selected" /></td>
					<td class="cellstyle">
						<div class="titlestyle">${document.title}</div>
					</td>
				</tr>
				<tr>
					<td></td>
					<td class="snippetcellstyle"><div class="snippetstyle">${document.highlight}</div></td>
				</tr>
			</c:forEach>
		</table>
		<div align="right">
			<input type="submit" name="submitButton" id="submitButton"
				class="btn btn-primary" value="Submit" />
		</div>
	</form:form>
</body>
</html>