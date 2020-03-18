<html>
<head>
<script src="https://assets.crowd.aws/crowd-html-elements.js"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script>
	$(document)
			.ready(
					function() {
						var subQuery = $
						{
							subQueryNum
						}
						;
						if (subQuery == 1) {
							$('#questionDiv')
									.html(
											'Suppose you ask Alexa, Siri, or Google Assistant this question:');
							$('#judgementDiv')
									.html(
											'How relevant is the document below to the question above?');
						} else {
							$('#questionDiv')
									.html(
											'Suppose you are having this conversation with Alexa, Siri, or Google Assistant:');
							$('#judgementDiv')
									.html(
											'How relevant is the document below to the last question in this conversation?');
						}
					});
</script>
</head>
<style>
<
style type ="text/css" media ="screen">.background {
	background-color: LightGray;
}

.buttonstyle {
	background-color: Blue;
}

.docstyle {
	background-color: LightGray;
	border: 1px solid #999;
	display: block;
	padding: 20px;
	word-wrap: normal;
}

.querystyle {
	display: block;
	padding: 20px;
	word-wrap: normal;
}

span {
	background-color: #FFFF00;
}
</style>
</style>
<body class="background">

	<!-- For the full list of available Crowd HTML Elements and their input/output documentation,
      please refer to https://docs.aws.amazon.com/sagemaker/latest/dg/sms-ui-template-reference.html -->

	<!-- You must include crowd-form so that your task submits answers to MTurk -->
	<crowd-form answer-format="flatten-objects"
		style="background-color: LightGray;"
		action='https://workersandbox.mturk.com/mturk/externalSubmit'>

	<br />
	<div id="questionDiv" style="font-size: 125%"></div>
	<div class="querystyle">${query}</div>
	<div id="judgementDiv" style="font-size: 125%"></div>
	
	<div class="docstyle">${text1}</div>
	<br />
	<crowd-radio-group name="relevance_1"> <crowd-radio-button
		name="relevance4_1" value="4">The passage is a
	perfect answer. It fully answers the question, focuses only on the
	topic, and contains little extra information.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance3_1" value="3">The
	passage answers the question and is a satisfactory answer, but it may
	contain limited extraneous information.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance2_1" value="2">The
	passage fully or partially answers the question, but is focused on
	other information that is unrelated to the question.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance1_1" value="1">The
	passage includes some information about the question, but does not
	directly answer it. It is better than nothing.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance0_1" value="0">The
	passage is unrelated to the target query. </crowd-radio-button> </crowd-radio-group>
	
	<div class="docstyle">${text2}</div>
	<br />
	<crowd-radio-group name="relevance_2"> <crowd-radio-button
		name="relevance4_2" value="4">The passage is a
	perfect answer. It fully answers the question, focuses only on the
	topic, and contains little extra information.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance3_2" value="3">The
	passage answers the question and is a satisfactory answer, but it may
	contain limited extraneous information.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance2_2" value="2">The
	passage fully or partially answers the question, but is focused on
	other information that is unrelated to the question.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance1_2" value="1">The
	passage includes some information about the question, but does not
	directly answer it. It is better than nothing.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance0_2" value="0">The
	passage is unrelated to the target query. </crowd-radio-button> </crowd-radio-group> 
	
	<div class="docstyle">${text3}</div>
	<br />
	<crowd-radio-group name="relevance_3"> <crowd-radio-button
		name="relevance4_3" value="4">The passage is a
	perfect answer. It fully answers the question, focuses only on the
	topic, and contains little extra information.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance3_3" value="3">The
	passage answers the question and is a satisfactory answer, but it may
	contain limited extraneous information.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance2_3" value="2">The
	passage fully or partially answers the question, but is focused on
	other information that is unrelated to the question.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance1_3" value="1">The
	passage includes some information about the question, but does not
	directly answer it. It is better than nothing.</crowd-radio-button>
	<br />
	<crowd-radio-button name="relevance0_3" value="0">The
	passage is unrelated to the target query. </crowd-radio-button> </crowd-radio-group> 
	
	<short-instructions>
	Choose the options that best describes how relevant the document is to
	the conversation. </short-instructions> <!-- Use the full-instructions section for more detailed instructions that the 
            Worker can open while working on the task. Including more detailed 
            instructions and additional examples of good and bad answers here can
            help get good results. You can include any HTML here. --> <full-instructions
		header="Conversation Relevance Instructions">
	<p>
		<strong>Example 1</strong>
	</p>
	<p>
		<strong>Conversation: </strong><br />What is throat cancer?<br />Is it
		treatable?
	</p>
	<p>
		<strong>Document: </strong>Throat cancer is a type of head and neck
		cancer. Throat cancer has different names, depending on what part of
		the throat is affected. The different parts of the throat are called
		the oropharynx, the hypopharynx, and the nasopharynx. Sometimes the
		larynx, or voice box, is also included. The main risk factors for
		throat cancer are smoking or using smokeless tobacco and use of
		alcohol. Treatments include surgery, radiation therapy, and
		chemotherapy.
	</p>
	<p>
		<strong>Answer: </strong>The passage is a perfect answer. It fully
		answers the question, focuses only on the topic, and contains little
		extra information.
	</p>
	<p>
		<strong>Example 2</strong>
	</p>
	<p>
		<strong>Conversation: </strong><br />Why is blood red?<br />What are
		red blood cells?<br />How are they created?
	</p>
	<p>
		<strong>Document: </strong>The red blood cells The red blood cells or
		erythrocytes are formed in the bone marrow by the process of
		erythropoeisis. Stem cells, under the influence of erythropoietin,
		develop into erythroblasts, at which point they start to synthesise
		haemoglobin.
	</p>
	<p>
		<strong>Answer: </strong>The passage answers the question and is a
		satisfactory answer, but it may contain limited extraneous
		information.
	</p>
	<p>
		<strong>Example 3</strong>
	</p>
	<p>
		<strong>Conversation: </strong><br />What are the different types of
		sharks?
	</p>
	<p>
		<strong>Document: </strong>There are more than 350 different kinds of
		sharks, such as the great white and whale sharks. Fossils show that
		sharks have been around for 420 million years, since the early
		Silurian. Most sharks are predators, meaning they hunt and eat fish,
		marine mammals, and other sea creatures. However, the largest shark
		eats krill, like whales. This is the whale shark, the largest fish in
		the world.
	</p>
	<p>
		<strong>Answer: </strong>The passage fully or partially answers the
		question, but is focused on other information that is unrelated to the
		question.
	</p>
	<p>
		<strong>Example 4</strong>
	</p>
	<p>
		<strong>Conversation: </strong><br />Tell me about the Neverending
		Story film.<br />What is it about?
	</p>
	<p>
		<strong>Document: </strong>The Neverending Story (German: Die
		unendliche Geschichte) is a German fantasy novel by Michael Ende that
		was first published in 1979. The standard English translation, by
		Ralph Manheim, was first published in 1983.The novel was later adapted
		into several films.he NeverEnding Story was the first film adaptation
		of the novel. It was released in 1984, directed by Wolfgang Petersen
		and starring Barret Oliver as Bastian, Noah Hathaway as Atreyu, and
		Tami Stronach as the Childlike Empress.
	</p>
	<p>
		<strong>Answer: </strong>The passage includes some information about
		the question, but does not directly answer it. It is better than
		nothing.
	</p>
	<p>
		<strong>Example 5</strong>
	</p>
	<p>
		<strong>Conversation: </strong><br />How do you sleep after jet lag?<br />Does
		melatonin help?
	</p>
	<p>
		<strong>Document: </strong>Melatonin is a hormone made by the pineal
		gland, a small gland in the brain. Melatonin helps control your sleep
		and wake cycles. Very small amounts of it are found in foods such as
		meats, grains, fruits, and vegetables. You can also buy it as a
		supplement.
	</p>
	<p>
		<strong>Answer: </strong>The passage is unrelated to the target query.
	</p>
	</full-instructions> </crowd-form>
</body>
</html>