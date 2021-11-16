<html>
<head>
<script src="https://assets.crowd.aws/crowd-html-elements.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script>
$( document ).ready(function() {
    var subQuery = ${hit.subQueryNum};
        $('#questionDiv').html('Suppose you are writing a report, and you type in this query:');
        $('#judgementDiv').html('How relevant is the document below to the query above?');
    var sample = ${sample};
    if (sample == -1) {
    	$('#sampleDiv').html('SAMPLE DATA<br/><br/>');
    }
    document.querySelector('#crowd-form').onsubmit = function(e) {
    	var alertText = "";
    	var error = 0;
    	if(document.querySelector('#relevance4').checked || document.querySelector('#relevance3').checked
    			|| document.querySelector('#relevance2').checked || document.querySelector('#relevance1').checked
    			|| document.querySelector('#relevance0').checked) {

    	} else {
    		alertText = alertText.concat("You must select an answer<br/>");
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
<style>
<style type="text/css" media="screen">
.background {
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
.countstyle {
  display: block;
  padding: 20px;
  word-wrap: normal;
  color: blue;
}
.samplestyle {
	font-size: 150%;
	color: Red;
}
.errorstyle {
	font-size: 100%;
	color: Red;
}
span{
    background-color: #FFFF00;
}
</style>
</style>
<body class="background">

<!-- For the full list of available Crowd HTML Elements and their input/output documentation,
      please refer to https://docs.aws.amazon.com/sagemaker/latest/dg/sms-ui-template-reference.html -->

<!-- You must include crowd-form so that your task submits answers to MTurk -->
<crowd-form id="crowd-form">
<input type="hidden" name="queryNum" value="${hit.queryNum}"></input>
<div id="sampleDiv" class="samplestyle">
    </div>
    <div id="errorDiv" class="errorstyle">
    </div>
    <div id="questionDiv" style="font-size: 125%">
    </div>
    <div class="querystyle">
        ${hit.query}
    </div>
    <div class="countstyle">
        ${hit.hitCount}
    </div>
    <div id="judgementDiv" style="font-size: 125%">
    </div>
        <!-- Your conversations will be substituted for the "conversation" variable when 
               you publish a batch with an input file containing multiple HTML-formatted conversations. -->
        <embed
		    src="${hit.pdf}"
		    type="application/pdf"
		    frameBorder="0"
		    scrolling="auto"
		    height="100%"
		    width="100%"
		></embed>
<br/>
<input type="hidden" name="docId" value="${hit.docId}"></input>
<input type="hidden" name="score" value="${hit.score}"></input>
<crowd-radio-group name="relevance">
        <crowd-radio-button id="relevance4" name="relevance4" value="selected">The passage is a perfect answer. It fully answers the question, focuses only on the topic, and contains little extra information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance3" name="relevance3" value="selected">The passage answers the question and is a satisfactory answer, but it may contain limited extraneous information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance2" name="relevance2" value="selected">The passage fully or partially answers the question, but is focused on other information that is unrelated to the question.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance1" name="relevance1" value="selected">The passage includes some information about the question, but does not directly answer it. It is better than nothing.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance0" name="relevance0" value="selected">The passage is unrelated to the target query. </crowd-radio-button>
</crowd-radio-group>
<br/>
<br/>

<short-instructions>
       Choose the options that best describes how relevant the document is to the conversation.<br/><br/>
       <b>IMPORTANT:</b>  Please uncheck "Auto-accept next HIT" and submit current HIT when you are ready
       to take a break.  Do NOT press Return.  Please always press either Submit or Return upon judging all 5 documents so that 
       we can capture accurate time information.</short-instructions>

      <!-- Use the full-instructions section for more detailed instructions that the 
            Worker can open while working on the task. Including more detailed 
            instructions and additional examples of good and bad answers here can
            help get good results. You can include any HTML here. -->
      <full-instructions header="Conversation Relevance Instructions">
      <p><strong>Example 1</strong></p>
      <p><strong>Conversation: </strong><br/>What is throat cancer?<br/>Is it treatable?</p>
      <p><strong>Document: </strong>Throat cancer is a type of head and neck cancer. Throat cancer has different names, depending on what part of the throat is affected. The different parts of the throat are called the oropharynx, the hypopharynx, and the nasopharynx. Sometimes the larynx, or voice box, is also included. The main risk factors for throat cancer are smoking or using smokeless tobacco and use of alcohol. Treatments include surgery, radiation therapy, and chemotherapy.</p>
      <p><strong>Answer: </strong>The passage is a perfect answer. It fully answers the question, focuses only on the topic, and contains little extra information.</p>
      <p><strong>Example 2</strong></p>
      <p><strong>Conversation: </strong><br/>Why is blood red?<br/>What are red blood cells?<br/>How are they created?</p>
      <p><strong>Document: </strong>The red blood cells The red blood cells or erythrocytes are formed in the bone marrow by the process of erythropoeisis. Stem cells, under the influence of erythropoietin, develop into erythroblasts, at which point they start to synthesise haemoglobin.</p>
      <p><strong>Answer: </strong>The passage answers the question and is a satisfactory answer, but it may contain limited extraneous information.</p>
      <p><strong>Example 3</strong></p>
      <p><strong>Conversation: </strong><br/>What are the different types of sharks?</p>
      <p><strong>Document: </strong>There are more than 350 different kinds of sharks, such as the great white and whale sharks. Fossils show that sharks have been around for 420 million years, since the early Silurian. Most sharks are predators, meaning they hunt and eat fish, marine mammals, and other sea creatures. However, the largest shark eats krill, like whales. This is the whale shark, the largest fish in the world.</p>
      <p><strong>Answer: </strong>The passage fully or partially answers the question, but is focused on other information that is unrelated to the question.</p>
      <p><strong>Example 4</strong></p>
      <p><strong>Conversation: </strong><br/>Tell me about the Neverending Story film.<br/>What is it about?</p>
      <p><strong>Document: </strong>The Neverending Story (German: Die unendliche Geschichte) is a German fantasy novel by Michael Ende that was first published in 1979. The standard English translation, by Ralph Manheim, was first published in 1983.The novel was later adapted into several films.he NeverEnding Story was the first film adaptation of the novel. It was released in 1984, directed by Wolfgang Petersen and starring Barret Oliver as Bastian, Noah Hathaway as Atreyu, and Tami Stronach as the Childlike Empress.</p>
      <p><strong>Answer: </strong>The passage includes some information about the question, but does not directly answer it. It is better than nothing.</p>
      <p><strong>Example 5</strong></p>
      <p><strong>Conversation: </strong><br/>How do you sleep after jet lag?<br/>Does melatonin help?</p>
      <p><strong>Document: </strong>Melatonin is a hormone made by the pineal gland, a small gland in the brain. Melatonin helps control your sleep and wake cycles. Very small amounts of it are found in foods such as meats, grains, fruits, and vegetables. You can also buy it as a supplement.</p>
      <p><strong>Answer: </strong>The passage is unrelated to the target query.</p>
      </full-instructions>

</crowd-form>
</body>
</html>