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
	crossorigin="anonymous"/>
<style type="text/css" media="screen">
.formstyle {
	margin-top: 30px;
	margin-bottom: 20px;
	margin-left: 50px;
	margin-right: 20px;
}
.tablestyle {
	width: 60%;
}

.cellstyle {
	padding-left: 5px;
	padding-right: 5px;
	padding-bottom: 5px;
}
a.one {
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
 a.one:hover {
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
 a.two {
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
 a.two:hover {
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
  a.three {
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
 a.three:hover {
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
   a.four {
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
 a.four:hover {
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
    a.five {
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
 a.five:hover {
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
 .spanstyle {
	margin-top: 0px;
	margin-bottom: 20px;
	margin-left: 0px;
	margin-right: 0px;
	font-size: large;
}
</style>
</head>
<body class="formstyle">
	<div class="spanstyle"><b>Select a category that you have searched on within the past week.</b></div>
	<table class="tablestyle">
		<tr>
			<td class="cellstyle"><a href="./search?category=shopping&assignmentId=${assignmentId}" class="one">Shopping</a></td>
			<td class="cellstyle"><a href="./search?category=travel&assignmentId=${assignmentId}" class="two">Travel</a></td>
			<td class="cellstyle"><a href="./search?category=home&assignmentId=${assignmentId}" class="three">Home</a></td>
			<td class="cellstyle"><a href="./search?category=food&assignmentId=${assignmentId}" class="four">Food & Drink</a></td>
			<td class="cellstyle"><a href="./search?category=health&assignmentId=${assignmentId}" class="five">Health & Wellness</a></td>
		</tr>
	</table>
</body>
</html>