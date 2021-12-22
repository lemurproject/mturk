<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<meta charset="UTF-8">
<title>Query Relevance</title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css"
	integrity="sha384-HSMxcRTRxnN+Bdg0JdbxYKrThecOKuH5zCYotlSAcp1+c8xmyTe9GYg1l9a69psu"
	crossorigin="anonymous" />
<style type="text/css" media="screen">
.formstyle {
	margin-top: 30px;
	margin-bottom: 20px;
	margin-left: 50px;
	margin-right: 20px;
}

.spanstyle {
	margin-top: 0px;
	margin-bottom: 10px;
	margin-left: 0px;
	margin-right: 0px;
}

.divstyle {
	margin-top: 0px;
	margin-bottom: 50px;
	margin-left: 50px;
	margin-right: 100px;
}
.errorstyle {
margin-top: 0px;
	margin-bottom: 50px;
	margin-left: 50px;
	margin-right: 100px;
	color: Red;
	font-weight: bold;
}
.tablestyle {
	width: 60%;
}

.cellstyle {
	padding-left: 5px;
	padding-right: 5px;
	padding-bottom: 5px;
}
.categorystyle {
	color: blue;
}
</style>
<script type="text/javascript">
$( document ).ready(function() {
	document.querySelector('#search-form').onsubmit = function(e) {
    	var error = 0;
    	var queryLength = $('#queryInput').val().length;
    	//var descriptionLength = $('#descriptionInput').val().length;
    	var alertText = "";
    	if(queryLength == 0) {
    		alertText = alertText.concat("You must enter a query.<br/>");
    		error = 1;
    	}
    	//if(descriptionLength <= queryLength) {
    	//	alertText = alertText.concat("You must enter a description that is longer than the query.<br/>");
    	//	error = 1;
    	//}
    	if (error == 1) {
    		$('#errorDiv').html(alertText);
    		e.preventDefault();
    	}
	}
});
</script>
</head>
<body class="formstyle">
	<form:form action="./searchResultsBERT" method="post"
		modelAttribute="searchObject" id="search-form">
		<input type="hidden" name="prevQuery" value="${prevQuery}"></input>
		<input type="hidden" name="prevDescription" value="${prevDescription}"></input>
		<input type="hidden" name="prevCategory" value="${prevCategory}"></input>
		<input type="hidden" name="prevDoc1id" value="${prevDoc1id}"></input>
		<input type="hidden" name="prevDoc1selection" value="${prevDoc1selection}"></input>
		<input type="hidden" name="prevDoc2id" value="${prevDoc2id}"></input>
		<input type="hidden" name="prevDoc2selection" value="${prevDoc2selection}"></input>
		<input type="hidden" name="prevDoc3id" value="${prevDoc3id}"></input>
		<input type="hidden" name="prevDoc3selection" value="${prevDoc3selection}"></input>
		<input type="hidden" name="prevDoc4id" value="${prevDoc4id}"></input>
		<input type="hidden" name="prevDoc4selection" value="${prevDoc4selection}"></input>
		<input type="hidden" name="prevDoc5id" value="${prevDoc5id}"></input>
		<input type="hidden" name="prevDoc5selection" value="${prevDoc5selection}"></input>
		<input type="hidden" name="prevDoc6id" value="${prevDoc6id}"></input>
		<input type="hidden" name="prevDoc6selection" value="${prevDoc6selection}"></input>
		<input type="hidden" name="prevDoc7id" value="${prevDoc7id}"></input>
		<input type="hidden" name="prevDoc7selection" value="${prevDoc7selection}"></input>
		<input type="hidden" name="prevDoc8id" value="${prevDoc8id}"></input>
		<input type="hidden" name="prevDoc8selection" value="${prevDoc8selection}"></input>
		<input type="hidden" name="prevDoc9id" value="${prevDoc9id}"></input>
		<input type="hidden" name="prevDoc9selection" value="${prevDoc9selection}"></input>
		<input type="hidden" name="prevDoc10id" value="${prevDoc10id}"></input>
		<input type="hidden" name="prevDoc10selection" value="${prevDoc10selection}"></input>
		<input type="hidden" name="prevDoc11id" value="${prevDoc11id}"></input>
		<input type="hidden" name="prevDoc11selection" value="${prevDoc11selection}"></input>
		<input type="hidden" name="prevDoc12id" value="${prevDoc12id}"></input>
		<input type="hidden" name="prevDoc12selection" value="${prevDoc12selection}"></input>
		<input type="hidden" name="prevDoc13id" value="${prevDoc13id}"></input>
		<input type="hidden" name="prevDoc13selection" value="${prevDoc13selection}"></input>
		
		<input type="hidden" name="assignmentId" value="${assignmentId}"></input>
		<input type="hidden" name="inputStart" value="${inputStart}"></input>
		<input type="hidden" name="category" id="category" value="${category}"></input>
		<div align="center" id="errorMessage" class="errorstyle">${errorMessage}</div>
		<div align="center" id="errorDiv" class="errorstyle"></div>
		<div id="searchdiv">
		<div align="center" class="divstyle">
			<div class="spanstyle">
				<b>Enter a query.  Please note, the web pages are from <span class="categorystyle">2009</span>,<br/> and this search engine does not
					have your location information.</b>
			</div>
			<form:input path="queryString" size="100" id="queryInput" />
		</div>
		<div align="center">
			<input type="submit" name="submitButton" id="submitButton"
				class="btn btn-primary" value="Search" />
		</div>
		</div>
	</form:form>
</body>
</html>