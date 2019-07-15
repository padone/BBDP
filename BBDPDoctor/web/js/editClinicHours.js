var PSNumber = 0;
var originalCH;
var changed = false;
$(document).ready(function() {
	//取得門診時間
	$.ajax({
		type: "POST",
		url: "ClinicHoursServlet",
		async: false,
		data: {
			option: "getClinicHours"
		},
		dataType: "json",
		success: function(response) {
			originalCH = response;
			//連絡電話
			$("#phone").val(returnEscapeCharacter(response.CHPhone));
			//備註
			$("#ps").empty();
			for(var i=0; i<response.CHPS.length; i++) {
				$("#ps").append(
"									<div id='PS" + i + "' class='input-group' style='margin-top: 1vh; margin-bottom: 1vh;'>" + 
"										<span class='input-group-addon'><i class='glyphicon glyphicon-pencil'></i></span>" + 
"										<input type='text' class='form-control' style='width: 50vw;' placeholder='請輸入備註...' value='" + response.CHPS[i] + "' />" + 
"										<button type='button' class='btn btn-default' style='margin-left: 1vw;' onclick='deletePS(" + i + ")'>刪除</button>" + 
"									</div>");
				PSNumber++;
			}
			//門診時間
			$("#ch > tr:first > td:first > input").val(returnEscapeCharacter(response.CHTime.morning.time));
			$("#ch > tr:eq(1) > td:first > input").val(returnEscapeCharacter(response.CHTime.afternoon.time));
			$("#ch > tr:last > td:first > input").val(returnEscapeCharacter(response.CHTime.evening.time));
			for(var i=0; i<7; i++) {
				//morning
				if(response.CHTime.morning.week[i].yesOrNo == "yes") {
					$("#ch > tr:first > td:eq(" + (i+1) + ") > input:first").attr("checked", true);
					$("#ch > tr:first > td:eq(" + (i+1) + ")").append("<input type='text' class='form-control' placeholder='請輸入備註...' value='" + response.CHTime.morning.week[i].ps + "' />");
				}
				else if(response.CHTime.morning.week[i].yesOrNo == "no") {
					$("#ch > tr:first > td:eq(" + (i+1) + ") > input:first").attr("checked", false);
				}
				//afternoon
				if(response.CHTime.afternoon.week[i].yesOrNo == "yes") {
					$("#ch > tr:eq(1) > td:eq(" + (i+1) + ") > input:first").attr("checked", true);
					$("#ch > tr:eq(1) > td:eq(" + (i+1) + ")").append("<input type='text' class='form-control' placeholder='請輸入備註...' value='" + response.CHTime.afternoon.week[i].ps + "' />");
				}
				else if(response.CHTime.afternoon.week[i].yesOrNo == "no") {
					$("#ch > tr:eq(1) > td:eq(" + (i+1) + ") > input:first").attr("checked", false);
				}
				//evening
				if(response.CHTime.evening.week[i].yesOrNo == "yes") {
					$("#ch > tr:last > td:eq(" + (i+1) + ") > input:first").attr("checked", true);
					$("#ch > tr:last > td:eq(" + (i+1) + ")").append("<input type='text' class='form-control' placeholder='請輸入備註...' value='" + response.CHTime.evening.week[i].ps + "' />");
				}
				else if(response.CHTime.evening.week[i].yesOrNo == "no") {
					$("#ch > tr:last > td:eq(" + (i+1) + ") > input:first").attr("checked", false);
				}
			}
		},
		error: function() {
			console.log("clinicHours.js getClinicHours error");
		}
	});
	//是否修改檢查
	$("input[type='checkbox']").change(function() {
		changed = true;
	});
	$("input[type='text']").not("#searchPatientID").change(function() {
		changed = true;
	});
});

//新增備註
function newPS() {
	$("#ps").append(
"									<div id='PS" + PSNumber + "' class='input-group' style='margin-top: 1vh; margin-bottom: 1vh;'>" + "\n" + 
"										<span class='input-group-addon'><i class='glyphicon glyphicon-pencil'></i></span>" + "\n" + 
"										<input type='text' class='form-control' style='width: 50vw;' placeholder='請輸入備註...' />" + "\n" + 
"										<button type='button' class='btn btn-default' style='margin-left: 1vw;' onclick='deletePS(" + PSNumber + ")'>刪除</button>" + "\n" + 
"									</div>");
	PSNumber++;

	//是否修改檢查
	$("input[type='text']").not("#searchPatientID").change(function() {
		changed = true;
	});
}

//刪除備註
function deletePS(number) {
	changed = true;
	$("#PS" + number).remove();

	//是否修改檢查
	$("input[type='text']").not("#searchPatientID").change(function() {
		changed = true;
	});
}

//checkbox checked listener
$("input[type=checkbox]").change(function() {
	if(this.checked) {
    	$(this).parent().append("<input type='text' class='form-control' placeholder='請輸入備註...' value='' />");
	}
	else {
		$(this).parent().children().last().remove();
	}

	//是否修改檢查
	$("input[type='text']").not("#searchPatientID").change(function() {
		changed = true;
	});
});

//儲存
function save() {
	changed = false;
	$.ajax({
		type: "POST",
		url: "ClinicHoursServlet",
		data: {
			option: "updateClinicHours",
			PS: turnPSIntoJsonString(),
			time: turnCHIntoJsonString(),
			phone: turnPhoneIntoJsonString()
		},
		dataType: "text",
		success: function(response) {
			location.href = "ClinicHours.html";
		},
		error: function() {
			console.log("editClinicHours.js save error\n");
		}
	});
}

//離開前判斷有沒有修改(沒用到)
function checkIsModified() {
	var PS = JSON.stringify(originalCH.CHPS);
	var CHTime = JSON.stringify(originalCH.CHTime);
	var CHPhone = JSON.stringify(original.CHPhone);
	if(PS == turnPSIntoJsonString() && CHTime == turnCHIntoJsonString() && CHPhone == turnPhoneIntoJsonString()) {		//沒被修改過
		return false;
	}
	else {		//有被修改過
		return true;
	}
}

//儲存前或離開前把備註轉換成字串
function turnPSIntoJsonString() {
	var isFirst = true;
	var PS = "[";
	$("#ps > div").each(function(index) {
		if($(this).find("input").val() == "" && index == 0) {
			return
		}
		else if($(this).find("input").val() == "" && index != 0) {
			return
		}
		else if($(this).find("input").val() != "" && index == 0) {
			PS += "\"" + htmlEscapeCharacter($(this).find("input").val()) + "\"";
			isFirst = false;
		}
		else if($(this).find("input").val() != "" && index != 0 && isFirst) {
			PS += "\"" + htmlEscapeCharacter($(this).find("input").val()) + "\"";
			isFirst = false;
		}
		else if($(this).find("input").val() != "" && index != 0 && !isFirst) {
			PS += ",\"" + htmlEscapeCharacter($(this).find("input").val()) + "\"";
		}
	});
	PS += "]";
	return PS;
}

//儲存前或離開前把門診時間轉換成字串
function turnCHIntoJsonString() {
	var day = ["mon", "tue", "wed", "thu", "fri", "sat", "sun"];
	var yesOrNo = "no";
	var CH = "{";
	var morning = "\"morning\":{\"time\":\"" + htmlEscapeCharacter($("#ch > tr:first > td:first > input").val()) + "\",\"week\":[";
	var afternoon = "\"afternoon\":{\"time\":\"" + htmlEscapeCharacter($("#ch > tr:eq(1) > td:first > input").val()) + "\",\"week\":[";
	var evening = "\"evening\":{\"time\":\"" + htmlEscapeCharacter($("#ch > tr:last > td:first > input").val()) + "\",\"week\":[";
	for(var i=0; i<7; i++) {
		//morning
		if($("#ch > tr:first > td:eq(" + (i+1) + ") > input:first").is(":checked")) yesOrNo = "yes";
		else yesOrNo = "no";
		if(i == 0 && yesOrNo == "yes") {
			morning += "{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"" + htmlEscapeCharacter($("#ch > tr:first > td:eq(" + (i+1) + ") > input:last").val()) + "\"}";
		}
		else if(i == 0 && yesOrNo == "no") {
			morning += "{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"\"}";
		}
		else if(i != 0 && yesOrNo == "yes") {
			morning += ",{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"" + htmlEscapeCharacter($("#ch > tr:first > td:eq(" + (i+1) + ") > input:last").val()) + "\"}";
		}
		else if(i != 0 && yesOrNo == "no") {
			morning += ",{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"\"}";
		}
		//afternoon
		if($("#ch > tr:eq(1) > td:eq(" + (i+1) + ") > input:first").is(":checked")) yesOrNo = "yes";
		else yesOrNo = "no";
		if(i == 0 && yesOrNo == "yes") {
			afternoon += "{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"" + htmlEscapeCharacter($("#ch > tr:eq(1) > td:eq(" + (i+1) + ") > input:last").val()) + "\"}";
		}
		else if(i == 0 && yesOrNo == "no") {
			afternoon += "{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"\"}";
		}
		else if(i != 0 && yesOrNo == "yes") {
			afternoon += ",{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"" + htmlEscapeCharacter($("#ch > tr:eq(1) > td:eq(" + (i+1) + ") > input:last").val()) + "\"}";
		}
		else if(i != 0 && yesOrNo == "no") {
			afternoon += ",{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"\"}";
		}
		//evening
		if($("#ch > tr:last > td:eq(" + (i+1) + ") > input:first").is(":checked")) yesOrNo = "yes";
		else yesOrNo = "no";
		if(i == 0 && yesOrNo == "yes") {
			evening += "{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"" + htmlEscapeCharacter($("#ch > tr:last > td:eq(" + (i+1) + ") > input:last").val()) + "\"}";
		}
		else if(i == 0 && yesOrNo == "no") {
			evening += "{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"\"}";
		}
		else if(i != 0 && yesOrNo == "yes") {
			evening += ",{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"" + htmlEscapeCharacter($("#ch > tr:last > td:eq(" + (i+1) + ") > input:last").val()) + "\"}";
		}
		else if(i != 0 && yesOrNo == "no") {
			evening += ",{\"day\":\"" + day[i] + "\",\"yesOrNo\":\"" + yesOrNo + "\",\"ps\":\"\"}";
		}
	}
	morning += "]},";
	afternoon += "]},";
	evening += "]}";
	CH += morning + afternoon + evening +  "}";
	return CH;
}

//儲存前或離開前把連絡電話轉換成字串
function turnPhoneIntoJsonString() {
	return htmlEscapeCharacter($("#phone").val());
}

//換成escape character code
function htmlEscapeCharacter(str) {
	str = str.replace(/\'/g, "&#39;");
	str = str.replace(/\"/g, "&#34;");
	str = str.replace(/\\/g, "&#92;");
	return str;
}

//換回escape character symbol
function returnEscapeCharacter(str) {
	str = str.replace(/&#39;/g, "\'");
	str = str.replace(/&#34;/g, '\"');
	str = str.replace(/&#92;/g, '\\');
	return str;
}