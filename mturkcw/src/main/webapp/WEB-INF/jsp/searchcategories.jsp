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
	$("#searchdiv").hide();
	$(".btn-group > button.btn").on("click", function(){
	    category = this.innerHTML;
	    $('input[name="category"]').val(category);
	    $("#catname").html(category);
	    $("#catname2").html(category);
	    $("#searchdiv").show();
	});
	document.querySelector('#search-form').onsubmit = function(e) {
    	var error = 0;
    	var queryLength = $('#queryInput').val().length;
    	var descriptionLength = $('#descriptionInput').val().length;
    	var alertText = "";
    	if(queryLength == 0) {
    		alertText = alertText.concat("You must enter a query.<br/>");
    		error = 1;
    	}
    	if(descriptionLength <= queryLength) {
    		alertText = alertText.concat("You must enter a description that is longer than the query.<br/>");
    		error = 1;
    	}
    	if (error == 1) {
    		$('#errorDiv').html(alertText);
    		e.preventDefault();
    	}
	}
});
</script>
</head>
<body class="formstyle">
	<form:form action="https://boston.lti.cs.cmu.edu/boston-2-34/searchResultsBERT" method="post"
		modelAttribute="searchObject" id="search-form">

		<input type="hidden" name="assignmentId" value="${assignmentId}"></input>
		<input type="hidden" name="category" id="category"></input>
		<div align="center" id="errorMessage" class="errorstyle">${errorMessage}</div>
		<div align="center" class="divstyle">
			<b>Select a category that you have searched on within the past
				week.</b><br/><br/>
			<div class="btn-group" role="group" aria-label="Basic example">
				<c:forEach items="${categories}" var="category"
				varStatus="tagStatus">
			  		<button type="button" class="btn btn-primary">${category}</button>
			  	</c:forEach>
			</div>
		</div>
		<div align="center" id="errorDiv" class="errorstyle"></div>
		<div id="searchdiv">
		<div align="center" class="divstyle">
			<div class="spanstyle">
				<b>Type a <span class="categorystyle" id="catname"></span> query that you entered into a search
					engine within the past week.</b>
			</div>
			<form:input path="queryString" size="100" id="queryInput" />
		</div>
		<div align="center" class="divstyle">
			<div class="spanstyle">
				<b>Please describe in a few sentences what this <span class="categorystyle" id="catname2"></span> query intended
					to find,<br />i.e., what would make a document relevant or useful.
				</b>
			</div>
			<form:textarea path="queryDescription" rows="5" cols="100"
				id="descriptionInput" />
		</div>
		<div align="center">
			<input type="submit" name="submitButton" id="submitButton"
				class="btn btn-primary" value="Search" />
		</div>
		</div>
	</form:form>
</body>
</html>