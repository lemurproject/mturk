<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<!DOCTYPE html>
<html xmlns:th="https://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Document</title>
<link href="/css/main.css" rel="stylesheet">
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
	<form action="#" th:action="@{/document}" th:object="${docRelevance}" method="post">
	<script src="/js/main.js"></script>

	<h3>Suppose you are having a conversation with Alexa, Siri, or
		Google Assitant, and you have asked these questions:</h3>
	<c:forEach items="${queryList}" var="query">
		<c:choose>
			<c:when test="${query == subQuery}">
				<div>
					<b>${query}</b>
				</div>
			</c:when>
			<c:otherwise>
				<div>${query}</div>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<br />
	<h3>
		Please judge how relevant this document is to the
		<b>bold</b> question above.
	</h3>
	<div class=docstyle>${document}</div><br/><br/>
	<div class="control-group">
				  <form:radiobutton path = "docRelevance.relevance" value = "2" label = "Highly Relevant" /><br/>
                  <form:radiobutton path = "docRelevance.relevance" value = "1" label = "Somewhat Relevant" /><br/>
                  <form:radiobutton path = "docRelevance.relevance" value = "0" label = "Not Relevant" /><br/>
<!--  
                    <label class="radio">
                     <input type="radio" name="relevance" id="relevance2" value="2">
                      Highly relevant
                    </label><br>
                    <label class="radio">
                      <input type="radio" name="relevance" id="relevance1" value="1">
                      Somewhat relevant
                    </label><br>
                    <label class="radio">
                      <input type="radio" name="relevance" id="relevance0" value="0">
                      Not relevant
                    </label>
                    -->
                </div>
                <br/>
                <input type = "submit" value = "Next Document"/>
                </form>
</body>
</html>