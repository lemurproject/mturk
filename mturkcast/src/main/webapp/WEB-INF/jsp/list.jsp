<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html xmlns:th="https://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>MTurk CAsT view list</title>
<style type="text/css" media="screen">
.docstyle {
  background-color: #eee;
  border: 1px solid #999;
  display: block;
  padding: 20px;
  word-wrap: normal;
}
</style>
<!-- <script src="https://assets.crowd.aws/crowd-html-elements.js"></script> -->
</head>
<body>
	<h3>${title}</h3>
	<c:forEach items="${list}" var="item">
				<div>${item}</div>
	</c:forEach>

</body>
</html>