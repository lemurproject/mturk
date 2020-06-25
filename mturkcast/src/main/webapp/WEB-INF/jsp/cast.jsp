<html>
<head>
<script src="https://assets.crowd.aws/crowd-html-elements.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script>
$( document ).ready(function() {
    var subQuery = ${hit.subQueryNum};
    if (subQuery == 1) {
        $('#questionDiv').html('Suppose you ask Alexa, Siri, or Google Assistant this question:');
        $('#judgementDiv').html('1) How relevant is the document below to the question above?');
        $('#judgementDiv2').html('2) How relevant is the document below to the question above?');
        $('#judgementDiv3').html('3) How relevant is the document below to the question above?');
        $('#judgementDiv4').html('4) How relevant is the document below to the question above?');
        $('#judgementDiv5').html('5) How relevant is the document below to the question above?');
    } else {
        $('#questionDiv').html('Suppose you are having this conversation with Alexa, Siri, or Google Assistant:');
        $('#judgementDiv').html('1) How relevant is the document below to the last question in this conversation?');
        $('#judgementDiv2').html('2) How relevant is the document below to the last question in this conversation?');
        $('#judgementDiv3').html('3) How relevant is the document below to the last question in this conversation?');
        $('#judgementDiv4').html('4) How relevant is the document below to the last question in this conversation?');
        $('#judgementDiv5').html('5) How relevant is the document below to the last question in this conversation?');
    }
    var sample = ${sample};
    if (sample == -1) {
    	$('#sampleDiv').html('SAMPLE DATA<br/><br/>');
    }
    document.querySelector('#crowd-form').onsubmit = function(e) {
    	var alertText = "";
    	var error = 0;
    	if(document.querySelector('#relevance4_1').checked || document.querySelector('#relevance3_1').checked
    			|| document.querySelector('#relevance2_1').checked || document.querySelector('#relevance1_1').checked
    			|| document.querySelector('#relevance0_1').checked) {

    	} else {
    		alertText = alertText.concat("You must select an answer for the 1st document<br/>");
    		error = 1;
    	}
    	if(document.querySelector('#relevance4_2').checked || document.querySelector('#relevance3_2').checked
    			|| document.querySelector('#relevance2_2').checked || document.querySelector('#relevance1_2').checked
    			|| document.querySelector('#relevance0_2').checked) {

    	} else {
    		alertText =  alertText.concat("You must select an answer for the 2st document<br/>");
    		error = 1;
    	}
    	if(document.querySelector('#relevance4_3').checked || document.querySelector('#relevance3_3').checked
    			|| document.querySelector('#relevance2_3').checked || document.querySelector('#relevance1_3').checked
    			|| document.querySelector('#relevance0_3').checked) {

    	} else {
    		alertText = alertText.concat("You must select an answer for the 3rd document<br/>");
    		error = 1;
    	}
    	if(document.querySelector('#relevance4_4').checked || document.querySelector('#relevance3_4').checked
    			|| document.querySelector('#relevance2_4').checked || document.querySelector('#relevance1_4').checked
    			|| document.querySelector('#relevance0_4').checked) {

    	} else {
    		alertText = alertText.concat("You must select an answer for the 4th document<br/>");
    		error = 1;
    	}
    	if(document.querySelector('#relevance4_5').checked || document.querySelector('#relevance3_5').checked
    			|| document.querySelector('#relevance2_5').checked || document.querySelector('#relevance1_5').checked
    			|| document.querySelector('#relevance0_5').checked) {

    	} else {
    		alertText = alertText.concat("You must select an answer for the 5th document<br/>");
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
<input type="hidden" name="hitId" value="${hit.hitId}"></input>
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
        <div class="docstyle"> ${hit.text1} </div>
<br/>
<input type="hidden" name="docId_1" value="${hit.document1}"></input>
<input type="hidden" name="score_1" value="${hit.score1}"></input>
<crowd-radio-group name="relevance_1">
        <crowd-radio-button id="relevance4_1" name="relevance4_1" value="selected">The passage is a perfect answer. It fully answers the question, focuses only on the topic, and contains little extra information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance3_1" name="relevance3_1" value="selected">The passage answers the question and is a satisfactory answer, but it may contain limited extraneous information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance2_1" name="relevance2_1" value="selected">The passage fully or partially answers the question, but is focused on other information that is unrelated to the question.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance1_1" name="relevance1_1" value="selected">The passage includes some information about the question, but does not directly answer it. It is better than nothing.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance0_1" name="relevance0_1" value="selected">The passage is unrelated to the target query. </crowd-radio-button>
</crowd-radio-group>
<br/>
<br/>
    <div id="judgementDiv2" style="font-size: 125%">
    </div>
        <div class="docstyle"> ${hit.text2} </div>
<br/>
<input type="hidden" name="docId_2" value="${hit.document2}"></input>
<input type="hidden" name="score_2" value="${hit.score2}"></input>
<crowd-radio-group name="relevance_2">
        <crowd-radio-button id="relevance4_2" name="relevance4_2" value="selected">The passage is a perfect answer. It fully answers the question, focuses only on the topic, and contains little extra information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance3_2" name="relevance3_2" value="selected">The passage answers the question and is a satisfactory answer, but it may contain limited extraneous information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance2_2" name="relevance2_2" value="selected">The passage fully or partially answers the question, but is focused on other information that is unrelated to the question.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance1_2" name="relevance1_2" value="selected">The passage includes some information about the question, but does not directly answer it. It is better than nothing.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance0_2" name="relevance0_2" value="selected">The passage is unrelated to the target query. </crowd-radio-button>
</crowd-radio-group>
<br/>
<br/>
    <div id="judgementDiv3" style="font-size: 125%">
    </div>
        <div class="docstyle"> ${hit.text3} </div>
<br/>
<input type="hidden" name="docId_3" value="${hit.document3}"></input>
<input type="hidden" name="score_3" value="${hit.score3}"></input>
<crowd-radio-group name="relevance_3">
        <crowd-radio-button id="relevance4_3" name="relevance4_3" value="selected">The passage is a perfect answer. It fully answers the question, focuses only on the topic, and contains little extra information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance3_3" name="relevance3_3" value="selected">The passage answers the question and is a satisfactory answer, but it may contain limited extraneous information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance2_3" name="relevance2_3" value="selected">The passage fully or partially answers the question, but is focused on other information that is unrelated to the question.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance1_3" name="relevance1_3" value="selected">The passage includes some information about the question, but does not directly answer it. It is better than nothing.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance0_3" name="relevance0_3" value="selected">The passage is unrelated to the target query. </crowd-radio-button>
</crowd-radio-group>
<br/>
<br/>
    <div id="judgementDiv4" style="font-size: 125%">
    </div>
        <div class="docstyle"> ${hit.text4} </div>
<br/>
<input type="hidden" name="docId_4" value="${hit.document4}"></input>
<input type="hidden" name="score_4" value="${hit.score4}"></input>
<crowd-radio-group name="relevance_4">
        <crowd-radio-button id="relevance4_4" name="relevance4_4" value="selected">The passage is a perfect answer. It fully answers the question, focuses only on the topic, and contains little extra information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance3_4" name="relevance3_4" value="selected">The passage answers the question and is a satisfactory answer, but it may contain limited extraneous information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance2_4" name="relevance2_4" value="selected">The passage fully or partially answers the question, but is focused on other information that is unrelated to the question.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance1_4" name="relevance1_4" value="selected">The passage includes some information about the question, but does not directly answer it. It is better than nothing.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance0_4" name="relevance0_4" value="selected">The passage is unrelated to the target query. </crowd-radio-button>
</crowd-radio-group>
<br/>
<br/>
    <div id="judgementDiv5" style="font-size: 125%">
    </div>
        <div class="docstyle"> ${hit.text5} </div>
<br/>
<input type="hidden" name="docId_5" value="${hit.document5}"></input>
<input type="hidden" name="score_5" value="${hit.score5}"></input>
<crowd-radio-group name="relevance_5">
        <crowd-radio-button id="relevance4_5" name="relevance4_5" value="selected">The passage is a perfect answer. It fully answers the question, focuses only on the topic, and contains little extra information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance3_5" name="relevance3_5" value="selected">The passage answers the question and is a satisfactory answer, but it may contain limited extraneous information.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance2_5" name="relevance2_5" value="selected">The passage fully or partially answers the question, but is focused on other information that is unrelated to the question.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance1_5" name="relevance1_5" value="selected">The passage includes some information about the question, but does not directly answer it. It is better than nothing.</crowd-radio-button><br/>
        <crowd-radio-button id="relevance0_5" name="relevance0_5" value="selected">The passage is unrelated to the target query. </crowd-radio-button>
</crowd-radio-group>

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