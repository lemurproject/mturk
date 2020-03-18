<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri = "http://www.springframework.org/tags/form" prefix = "form"%>
<!DOCTYPE html>
<html xmlns:th="https://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Document</title>
<link href="/css/main.css" rel="stylesheet">
</head>
<body>
	<form name='mturk_form' method='post' id='mturk_form' action='https://workersandbox.mturk.com/mturk/externalSubmit'>
	<h2 class="hello-title">Document Relevance</h2>
	<script src="/js/main.js"></script>

	<h3>Suppose you are having a conversation with Alexa, Siri, or
		Google, and you ask these questions:</h3>
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
				<input type="hidden" id="doc1_id" name="doc1_id" value="${doc1Id}">
				<input type="hidden" id="doc1_score" name="doc1_score" value="${doc1Score}">
				<input type="hidden" id="doc2_id" name="doc2_id" value="${doc2Id}">
				<input type="hidden" id="doc2_score" name="doc2_score" value="${doc2Score}">
				<input type="hidden" id="doc3_id" name="doc3_id" value="${doc3Id}">
				<input type="hidden" id="doc3_score" name="doc1_score" value="${doc3Score}">
				<input type="hidden" id="doc4_id" name="doc4_id" value="${doc4Id}">
				<input type="hidden" id="doc4_score" name="doc4_score" value="${doc4Score}">
				<input type="hidden" id="doc5_id" name="doc5_id" value="${doc5Id}">
				 
                    <label class="radio">
                     <input type="radio" name="doc5_score" id="relevance2" value="2">
                      Highly relevant
                    </label><br>
                    <label class="radio">
                      <input type="radio" name="doc5_score" id="relevance1" value="1">
                      Somewhat relevant
                    </label><br>
                    <label class="radio">
                      <input type="radio" name="doc5_score" id="relevance0" value="0">
                      Not relevant
                    </label>

                </div>
                <br/>
                <input type = "submit" value = "Finish & Submit"/>
                </form>
</body>
</html>