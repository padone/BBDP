$(document).ready(function() {
	getNotificationSetting();
});

//系統設定裡通知設定
function getNotificationSetting() {
	$.ajax({
		type: "POST",
		url: "NotificationServlet",
		async: false,
		data: {
			option: "getNotificationSetting"
		},
		dataType: "json",
		success: function(response) {
			if(response.notification.clinicPush == "yes") {
				$("#clinicPushCheckbox").attr("checked", true);
			}
			else if(response.notification.clinicPush == "no") {
				$("#clinicPushCheckbox").attr("checked", false);
			}
			if(response.notification.questionnaire == "yes") {
				$("#questionnaireCheckbox").attr("checked", true);
			}
			else if(response.notification.questionnaire == "no") {
				$("#questionnaireCheckbox").attr("checked", false);
			}
			if(response.notification.folder == "yes") {
				$("#folderCheckbox").attr("checked", true);
			}
			else if(response.notification.folder == "no") {
				$("#folderCheckbox").attr("checked", false);
			}
			if(response.notification.patientInstruction == "yes") {
				$("#patientInstructionCheckbox").attr("checked", true);
			}
			else if(response.notification.patientInstruction == "no") {
				$("#patientInstructionCheckbox").attr("checked", false);
			}
		},
		error: function() {
			console.log("notificationSetting.js getNotificationSetting error");
		}
	});
}

//修改通知設定
function saveNotificationSetting() {
	var notification = "{\"notification\":{\"clinicPush\":\"";
	if($("#clinicPushCheckbox").is(":checked")) notification += "yes";
	else notification += "no";
	notification += "\",\"questionnaire\":\"";
	if($("#questionnaireCheckbox").is(":checked")) notification += "yes";
	else notification += "no";
	notification += "\",\"folder\":\"";
	if($("#folderCheckbox").is(":checked")) notification += "yes";
	else notification += "no";
	notification += "\",\"patientInstruction\":\"";
	if($("#patientInstructionCheckbox").is(":checked")) notification += "yes";
	else notification += "no";
	notification += "\"}}";
	$.ajax({
		type: "POST",
		url: "NotificationServlet",
		async: false,
		data: {
			option: "modifyNotificationSetting",
			notification: notification
		},
		dataType: "text",
		success: function(response) {
			$("#notificationSettingAlertModal").modal("show");
		},
		error: function() {
			console.log("notificationSetting.js saveNotificationSetting error");
		}
	});
}