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
.tablestyle {
	width: 60%;
}

.cellstyle {
	padding-left: 5px;
	padding-right: 5px;
	padding-bottom: 5px;
}
button.one {
 width: 180px;
 height:180px;
 color: #FFFFFF;
 border-radius: 25px;
 background-color: #de3c3c;
 padding: 10px;
 text-decoration:none;
 display: block;
 font-weight: bold;
 font-size: large;
 text-align: center;
 line-height: 150px;
 }
 button.one:hover, button.one:active, button.one.focus, button.one_selected {
 width: 180px;
 height: 180px;
 color: #FFFFFF;
 border-radius: 25px;
 background-color: #962727;
 padding: 10px;
 font-weight: bold;
 font-size: large;
 text-decoration:none;
 display: block;
 text-align: center;
 line-height: 150px;
 }
 button.two {
 width: 180px;
 height:180px;
 color: #FFFFFF;
 border-radius: 25px;
 background-color: #4a72c2;
 padding: 10px;
 text-decoration:none;
 display: block;
 font-weight: bold;
 font-size: large;
 text-align: center;
 line-height: 150px;
 }
 button.two:hover, button.two:active, button.two.focus, button.two_selected {
 width: 180px;
 height: 180px;
 color: #FFFFFF;
 border-radius: 25px;
 background-color: #324c80;
 padding: 10px;
 font-weight: bold;
 font-size: large;
 text-decoration:none;
 display: block;
 text-align: center;
 line-height: 150px;
 }
  button.three {
 width: 180px;
 height:180px;
 color: #FFFFFF;
 border-radius: 25px;
 background-color: #de7b18;
 padding: 10px;
 text-decoration:none;
 display: block;
 font-weight: bold;
 font-size: large;
 text-align: center;
 line-height: 150px;
 }
 button.three:hover, button.three:active, button.three.focus, button.three_selected {
 width: 180px;
 height: 180px;
 color: #FFFFFF;
 border-radius: 25px;
 background-color: #94510f;
 padding: 10px;
 font-weight: bold;
 font-size: large;
 text-decoration:none;
 display: block;
 text-align: center;
 line-height: 150px;
 }
   button.four {
 width: 180px;
 height:180px;
 color: #FFFFFF;
 border-radius: 25px;
 background-color: #a435d4;
 padding: 10px;
 text-decoration:none;
 display: block;
 font-weight: bold;
 font-size: large;
 text-align: center;
 line-height: 150px;
 }
 button.four:hover, button.four:active, button.four.focus, button.four_selected {
 width: 180px;
 height: 180px;
 color: #FFFFFF;
 border-radius: 25px;
 background-color: #702491;
 padding: 10px;
 font-weight: bold;
 font-size: large;
 text-decoration:none;
 display: block;
 text-align: center;
 line-height: 150px;
 }
    button.five {
 width: 180px;
 height:180px;
 color: #FFFFFF;
 border-radius: 25px;
 background-color: #21cc46;
 padding: 10px;
 text-decoration:none;
 display: block;
 font-weight: bold;
 font-size: large;
 text-align: center;
 line-height: 150px;
 }
 button.five:hover, button.five:active, button.five.focus, button.five_selected {
 width: 180px;
 height: 180px;
 color: #FFFFFF;
 border-radius: 25px;
 background-color: #188c31;
 padding: 10px;
 font-weight: bold;
 font-size: large;
 text-decoration:none;
 display: block;
 text-align: center;
 line-height: 150px;
 }
</style>
<script type="text/javascript"></script>
</head>
<body class="formstyle">
	<form:form action="https://boston.lti.cs.cmu.edu/boston-2-34/searchResultsBERT" method="post"
		modelAttribute="searchObject">
		<input type="hidden" name="assignmentId" value="${assignmentId}"></input>
		<div align="center" class="divstyle">
			<b>Select a category that you have searched on within the past
				week.</b>
		</div>
		<div align="center" class="divstyle">
		<table class="tablestyle">
			<tr>
				<td class="cellstyle"><button class="one" type="button">Shopping</button></td>
				<td class="cellstyle"><button class="two" type="button">Travel</button></td>
				<td class="cellstyle"><button class="three" type="button">Home</button></td>
				<td class="cellstyle"><button class="four" type="button">Food & Drink</button></td>
				<td class="cellstyle"><button class="five" type="button">Health & Wellness</button></td>
			</tr>
		</table>
		</div>
		<div align="center" class="divstyle">
			<div class="spanstyle">
				<b>Type a query that you entered into a search
					engine within the past week.</b>
			</div>
			<form:input path="queryString" size="100" id="queryInput" />
		</div>
		<div align="center" class="divstyle">
			<div class="spanstyle">
				<b>Please describe in a few sentences what this query intended
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
	</form:form>
</body>
</html>