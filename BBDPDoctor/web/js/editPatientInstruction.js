var url = "PatientInstructionServlet";
var froalaUrl = "InstructionFroalaServlet";

var editURL = window.location.href;
var urlparts = editURL.split("?");
var IDparts = urlparts[1].split("=");
var patientInstructionID = IDparts[1];


$(document).ready(function() {
	checkInstructionID(patientInstructionID);	//檢查InstructionID
	
	getInstruction();	//取得衛教資訊
});

//取得衛教資訊
function getInstruction(){
	$.ajax({
		url : url,
		data : {
			state : "getInstruction",
			patientInstructionID : patientInstructionID
		},
		dataType : "json",
		success : function(response) {
			$("#title").append(returnEscapeCharacter(response.title));
			$("#date").append(response.date);
			$("#html").append(response.content);
			$("#firstDate").append(response.date);
			$("#lastDate").append(response.editDate);
		},
		error : function() {console.log("錯誤訊息");}
	});
}

//刪除衛教資訊和留言//顯示modal
function deleteInstruction(){
	modalGeneratorCancel("刪除", "確定刪除此衛教資訊嗎？");
}
//刪除衛教資訊和留言和收藏文章的人//modalGeneratorCancel()
function determine(){
	$.ajax({
		url : url,
		data : {
			state : "deleteInstruction",
			patientInstructionID : patientInstructionID
		},
		dataType : "json",
		success : function(response) {
			if(response){
				deleteFolder(patientInstructionID);	//刪除資料夾
				window.location.href = 'PatientInstruction.html';
			}
		},
		error : function() {console.log("錯誤訊息");}
	});
}
