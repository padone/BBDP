var data;
var QData, FData;
var patientID, doctorID;
//init
$(document).ready(function() {
	$.ajax({
		type: "GET",
		url: "GetSessionServlet",	 
		data: {
			option:"getSession"
		},
		dataType: "json",							
		success : function(response){
			doctorID = response[0]["doctorID"];
			updateQList();
			updateFList();
		},
		error : function(){
			console.log("homepage.js getSession error");
		}
	});
});

//顯示或隱藏問卷列表
function clickQBody(state) {
	if(state == "show") {
		$("#todayQuestionnairePanel > .panel-body").attr("onclick", "clickQBody('hide');");
		$("#todayQuestionnairePanel > .panel-body > span:first").html("收回列表");
		$("#todayQuestionnairePanel > .panel-body > span > i").attr("class", "fa fa-arrow-circle-up");
	}
	else if(state == "hide") {
		$("#todayQuestionnairePanel > .panel-body").attr("onclick", "clickQBody('show');");
		$("#todayQuestionnairePanel > .panel-body > span:first").html("查看列表");
		$("#todayQuestionnairePanel > .panel-body > span > i").attr("class", "fa fa-arrow-circle-down");
	}
}

//顯示或隱藏檔案列表
function clickFBody(state) {
	if(state == "show") {
		$("#todayFolderPanel > .panel-body").attr("onclick", "clickFBody('hide');");
		$("#todayFolderPanel > .panel-body > span:first").html("收回列表");
		$("#todayFolderPanel > .panel-body > span > i").attr("class", "fa fa-arrow-circle-up");
	}
	else if(state == "hide") {
		$("#todayFolderPanel > .panel-body").attr("onclick", "clickFBody('show');");
		$("#todayFolderPanel > .panel-body > span:first").html("查看列表");
		$("#todayFolderPanel > .panel-body > span > i").attr("class", "fa fa-arrow-circle-down");
	}
}

//更新問卷列表
function updateQList() {
	$.ajax({
		type: "POST",
		url: "HomepageServlet",
		data: {
			option: "getData"
		},
		async: false,
		dataType: "json",
		success: function(response) {
			QData = response;
		},
		error: function() {
			console.log("homepage.js getData questionnaire error");
		}
	});
	$("#todayQuestionnaireNum").html(QData.length);
	$("#todayQuestionnairePanel > div > .panel-footer > .list-group").empty();
	if(QData.length == 0) {
		$("#todayQuestionnairePanel > div > .panel-footer > .list-group").append("今日尚無問卷");
	}
	else {
		for(var i=0; i<QData.length; i++) {
			$("#todayQuestionnairePanel > div > .panel-footer > .list-group").append(
"												<a onclick='clickQItem(\"" + QData[i].patientID + "\", \"" + QData[i].answerID + "\")' class='list-group-item left-list-item questionnairePink' style='cursor: pointer;'>" + "\n" + 
"													<div class='media'>" + "\n" + 
"														<div class='media-left media-top'>" + "\n" + 
"															<div id='profilePicture" + i + "'>" + "\n" + 
"																<img src='http://140.121.197.130:8004/BBDPPatient/ProfilePictureServlet?option=getProfilePicture&patientID=" + QData[i].patientID + "' onerror='failToLoadHomepagePatientPicture(\"" + i + "\");' class='media-object' style='width: 64px;' />" + "\n" + 
"															</div>" + "\n" + 
"														</div>" + "\n" + 
"														<div class='media-body'>" + "\n" + 
"															<h4 class='list-group-item-heading'>" + QData[i].patientName + "</h4>" + "\n" + 
"															<p>" + QData[i].questionnaireName + "</p>" + "\n" + 
"														</div>" + "\n" + 
"													</div>" + "\n" + 
"												</a>" + "\n");
		}
	}
}

//更新檔案列表
function updateFList() {
	$.ajax({
		type: "GET",
		url: "http://140.121.197.130:8600/BBDPFolderServer/HomepageFolderDataServlet",
		data: {
			doctorID: doctorID
		},
		async: false,
		dataType: "json",
		success: function(response) {
			FData = response;
		},
		error: function() {
			console.log("homepage.js getData folder error");
		}
	});
	$("#todayFolderNum").html(FData.length);
	$("#todayFolderPanel > div > .panel-footer > .list-group").empty();
	if(FData.length == 0) {
		$("#todayFolderPanel > div > .panel-footer > .list-group").append("今日尚無檔案");
	}
	else {
		for(var i=0; i<FData.length; i++) {
			//取得病患姓名
			var patientName = "病患姓名";
			$.ajax({
				type: "POST",
				url: "HomepageServlet",
				data: {
					option: "getPatientName",
					patientID: FData[i].patientID
				},
				async: false,
				dataType: "text",
				success: function(response) {
					patientName = response;
				},
				error: function() {
					console.log("homepage.js updateFList getPatientName error");
					patientName = "病患姓名";
				}
			});
			
			$("#todayFolderPanel > div > .panel-footer > .list-group").append(
"												<a onclick='clickFItem(\"" + FData[i].patientID + "\", \"" + FData[i].time + "\")' class='list-group-item left-list-item patientFolder' style='cursor: pointer;'>" + "\n" + 
"													<div class='media'>" + "\n" + 
"														<div class='media-left media-top'>" + "\n" + 
"															<img src='img/patientFolder/" + FData[i].pictureOrVideo + ".png' class='media-object' style='width: 64px;'>" + "\n" + 
"														</div>" + "\n" + 
"														<div class='media-body'>" + "\n" + 
"															<h4 class='list-group-item-heading'>" + patientName + "</h4>" + "\n" + 
"															<p>" + FData[i].description + "</p>" + "\n" + 
"														</div>" + "\n" + 
"													</div>" + "\n" + 
"												</a>" + "\n");
		}
	}
}

//點擊問卷列表的重新整理
function refreshQPanel() {
	updateQList();
}

//點擊檔案列表的重新整理
function refreshFPanel() {
	updateFList();
}

//點擊問卷列表裡的項目
function clickQItem(patientID, answerID) {
	$.ajax({
		type: "POST",
		url: "NotificationServlet",
		async: false,
		data: {
			option: "clickNotification", 
			patientID: patientID
		},
		dataType: "text",
		success: function(response) {
			location.href = "EditPatientQuestionnaire.html?num=" + answerID;
		},
		error: function() {
			console.log("homepage.js clickQItem error");
		}
	});
}

//點擊檔案列表裡的項目
function clickFItem(patientID, time) {
	$.ajax({
		type: "POST",
		url: "NotificationServlet",
		async: false,
		data: {
			option: "clickNotification", 
			patientID: patientID
		},
		dataType: "text",
		success: function(response) {
			location.href = "EditPatientFolder.html?time=" + time;
		},
		error: function() {
			console.log("homepage.js clickFItem error");
		}
	});
}

function failToLoadHomepagePatientPicture(i) {
	console.log("in failToLoadHomepagePatientPicture!");
	$("#profilePicture" + i).empty();
	$("#profilePicture" + i).append("<img src='img/frame/user.png' class='media-object' style='width: 64px;'/>");
}