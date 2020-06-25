<html>
<head>
<script src="https://assets.crowd.aws/crowd-html-elements.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script>
$( document ).ready(function() {
    document.querySelector('#crowd-form').onsubmit = function(e ) {
    	e.preventDefault();
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
	<h1>Error</h1>
	<div>${message}</div>
</crowd-form>
</body>
</html>