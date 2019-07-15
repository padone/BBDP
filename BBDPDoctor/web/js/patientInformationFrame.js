//prepend 深藍色病患資訊nav-tabs
$("#page-wrapper > .row:first").prepend(
"<ul class='nav nav-tabs'>"
	 + "<li id='patientNameOnTab'>病患姓名</li>"
	 + "<li id='basicInformation'><a href='PatientBasicInformation.html'><b>基本資料</b></a></li>"
	 + "<li id='medicalRecord'><a href='PatientMedicalRecord.html'><b>病歷摘要</b></a></li>"
	 + "<li id='Questionnaire'><a href='PatientQuestionnaire.html'><b>問卷區</b></a></li>"
	 + "<li id='healthTracking'><a href='PatientHealthTracking.html'><b>健康狀況追蹤</b></a></li>"
	 + "<li id='folder'><a href='PatientFolder.html'><b>檔案夾</b></a></li>"
	 + "<li id='notice'><a href='PatientNotice.html'><b>注意事項</b></a></li>"
+ "</ul>");

//拿html檔名
function getHTMLName() {
	var path = window.location.pathname;
	var page = path.split("/").pop();
	var name = page.split(".");
	return name[0];
}

//在 tab 加上 active class
$(function(){
	var name = getHTMLName();
	var i;
	
	switch(name) {
	case "PatientBasicInformation": 
		document.getElementById("basicInformation").className = "active";
		break;
	case "PatientMedicalRecord":
	case "EditPatientMedicalRecord":
	case "NewPatientMedicalRecord":
		document.getElementById("medicalRecord").className = "active";
		break;
	case "PatientQuestionnaire":
	case "EditPatientQuestionnaire":
	case "NewPatientQuestionnaire":
		document.getElementById("Questionnaire").className = "active";
		break;
	case "PatientHealthTracking":
	case "EditPatientHealthTracking":
	case "EditPatientHealthTrackingData":
	case "NewPatientHealthTracking":
		document.getElementById("healthTracking").className = "active";
		break;
	case "PatientFolder":
	case "EditPatientFolder":
		document.getElementById("folder").className = "active";
		break;
	case "PatientNotice":
		document.getElementById("notice").className = "active";
		break;
	}
});
//取得病患姓名
$(document).ready(function(){
	$.ajax({
		type: "POST",
		url: "PatientBasicInformationServlet",
		data: {
			option: "patientName"
		},
		dataType: "text",
		success: function(response) {
			$("#patientNameOnTab").html("&nbsp;&nbsp;&nbsp;" + response + "&nbsp;&nbsp;&nbsp;");
		},
		error: function() {
			console.log("patientBasicInformationFrame.js error");
			$("#patientNameOnTab").html("病患姓名");
		}
	});
});