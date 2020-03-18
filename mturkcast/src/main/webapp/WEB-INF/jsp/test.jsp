<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html xmlns:th="https://www.thymeleaf.org">
<head>
<script src="https://assets.crowd.aws/crowd-html-elements.js"></script>
</head>
<style>
.instructions {
	border:1px solid black;
	margin-left: 20px;
	padding-left: 10px;
  	background-color: silver;
}
</style>
<body>
	<crowd-form> <crowd-classifier name="sentiment"
		categories="['Perfectly Relevant', 'Highly Relevant', 'Somewhat Relevant', 'Slightly Relevant', 'Not Relevant', 'N/A']"
		header="Suppose someone is having a conversation with Alexa, Siri, or the Google Assistant.">

	<classification-target>
	<p>
		<strong>Search conversation:<br /></strong>
		<c:forEach items="${queryList}" var="query">
			${query}<br/>
		</c:forEach>
	</p>
	<p><strong>Use these definitions to judge whether the below
	text relevant to the last question in this conversation.</strong></p>
	<div class="instructions">
	<p>
		<strong>Perfectly Relevant</strong> The passage is a perfect answer. It
		fully answers the question, focuses only on the topic, and contains
		little extra information.
	</p>
	<p>
		<strong>Highly Relevant</strong> The passage answers the question
		and is a satisfactory answer, but it may contain limited extraneous
		information.
	</p>
	<p>
		<strong>Somewhat Relevant</strong> The passage fully or partially answers 
		the question, but is focused on other information that is unrelated to the question.
	</p>
	<p>
		<strong>Slightly Relevant</strong> The passage includes some information about 
		the question, but does not directly answer it. It is better than nothing.
	</p>
	<p>
		<strong>Not Relevant</strong>: The passage is unrelated to the target
		query.
	</p>
	<p>
		<strong>N/A</strong>: when the text cannot be understood
	</p>
	<p>
	</p>
</div>
	<p>
		<strong>Text Result:<br /></strong> ${document}
	</p>

	</classification-target> <full-instructions header="Relevance Analysis Instructions">
	<p>
		<strong>Highly Relevant</strong> The passage is a perfect answer. It
		fully answers the question, focuses only on the topic, and contains
		little extra information.
	</p>
	<p>
		<strong>Somewhat Relevant</strong> The passage answers the question
		and is a satisfactory answer, but it may contain limited extraneous
		information.
	</p>
	<p>
		<strong>Not Relevant</strong>: The passage is unrelated to the target
		query.
	</p>
	<p>
		<strong>N/A</strong>: when the text cannot be understood
	</p>
	<p>When the sentiment is mixed, such as both joy and sadness, use
		your judgment to choose the stronger emotion.</p>
	</full-instructions> <short-instructions> Choose how relevant the text
	is to the query. </short-instructions> </crowd-classifier> </crowd-form>
</body>
</html>