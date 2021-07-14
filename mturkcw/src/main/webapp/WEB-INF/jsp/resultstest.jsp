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
		action="https://www.mturk.com/mturk/externalSubmit"
		method="post" modelAttribute="searchResult">
		<form:input path="assignmentId" type="hidden"
			value="${searchResult.assignmentId}" />
		<form:input path="query" type="hidden" value="${searchResult.query}" />
		<form:input path="description" type="hidden"
			value="${searchResult.description}" />
		<form:input path="category" type="hidden"
			value="${searchResult.category}" />
		<div align="left">
			<label>Searched for: </label>&nbsp;&nbsp;
			<form:input path="query" size="100" disabled="true" />
		</div>
		<div class="instructionstyle">Please note that these documents are from the year 2009.  
		Place a check mark next to every document that you think is worth examining.  You do not need to open
		the links (many do not work any more), but check the documents that you feel you would open.<br/>
		</div>
		<hr />
		<table class="tablestyle">
			<col width="10">
			<c:forEach items="${searchResult.documents}" var="document"
				varStatus="tagStatus">
				<form:input path="documents[${tagStatus.index}].docId" type="hidden"
					value="${document.docId}" />
				<tr>
					<td></td>
					<td class="cellstyle"><div class="urlstyle">${document.url}</div></td>
				</tr>
				<tr>
					<td></td>
					<td class="cellstyle"><div class="urlstyle">${document.docId}</div></td>
				</tr>
				<tr>
					<td class="cellstyle"><form:checkbox
							path="documents[${tagStatus.index}].selected"
							id="${document.docId}" name="${document.docId}" value="selected" /></td>
					<td class="cellstyle">
						<div>
							<a class="titlestyle" target="_blank" href="${document.url}">${document.title}</a>
							<!--<a class="titlestyle" href="./document?docid=${document.docId}">${document.title}</a>
							 <a class="titlestyle" href="./documentFrame?docurl=${document.url}">${document.title}</a> -->
						</div>
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