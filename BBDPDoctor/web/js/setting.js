//修改設定資料
$(document).ready(function() {
	getSetting();	//取得設定資料
	accountSetting(); //修改帳戶設定
	personalSetting();	//修改個人資料
});

/********************************************************************************/
//取得設定資料
function getSetting(){
	$("#account").empty();
	$("#name").empty();
	$("#hospital").empty();
	$("#password").empty();
	$("#passwordCheck").empty();
	$("#department").empty();
	
	$.ajax({
		type : "POST",
		url : "AccountSettingServlet",
		data : {
			state : "Default",
		},
		dataType : "json",

		success : function(response) {
			$("#account").val(response.account);
			$("#name").val(response.name);
			$("#hospital").val(response.hospital);
			$("#password").val(b64_to_utf8(response.password));	//解密
			$("#passwordCheck").val(b64_to_utf8(response.passwordCheck));	//解密
			$("#department").val(response.department);
			$("#doctorQR").attr("src", response.QRCode);
			$("#doctorQRCodeDownload").attr("href", response.QRCode);
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
}

/********************************************************************************/
//修改帳戶設定
function accountSetting(){
	$("#change").click(function() {
		if(checkAccountSetting()){	//檢查帳戶設置
			$.ajax({
				type : "POST",
				url : "AccountSettingServlet",
				data : {
					state : "Change",
					password : utf8_to_b64($("#password").val()),	//加密
					passwordCheck : $("#passwordCheck").val(),
				},
				dataType : "json",
	
				success : function(response) {
					modalGenerator("帳戶設定修改", response.show);	//模組產生器
					
					$("#password").empty();
					$("#passwordCheck").empty();
					
					$("#password").val(response.password);
					$("#passwordCheck").val(response.passwordCheck);
				},
				error : function() {
					console.log("錯誤訊息");
				}
			});
		}
	});
}
//檢查帳戶設置
function checkAccountSetting(){
	if($("#password").val() == ""){
		modalGenerator("帳戶設定修改", "請輸入密碼");
		return false;
	}
	else if($("#passwordCheck").val() == ""){
		modalGenerator("帳戶設定修改", "請輸入確認密碼");
		return false;
	}
	else if($("#password").val().length<6 || $("#password").val().length>15){
		modalGenerator("帳戶設定修改", "密碼長度錯誤");
		return false;
	}
	else if(!checkEnNum($("#password").val())){
		modalGenerator("帳戶設定修改", "密碼請輸入英文或數字");
		return false;
	}
	else if($("#password").val() != $("#passwordCheck").val()){
		modalGenerator("帳戶設定修改", "確認密碼錯誤");
		return false;
	}
	return true;
		
}

/********************************************************************************/
//修改個人資料
function personalSetting(){
	$("#change2").click(function() {
		if(checkPersonalSetting()){	//檢查個人資料
			$.ajax({
				type : "POST",
				url : "AccountSettingServlet",
				data : {
					state : "Change2",
					name : $("#name").val(),
					hospital : $("#hospital").val(),
					department : $("#department").val()
				},
				dataType : "json",
	
				success : function(response) {
					modalGenerator("個人資料修改", response.show);	//模組產生器
					
					$("#name").empty();
					$("#hospital").empty();
					$("#department").empty();
					
					$("#name").val(response.name);
					$("#hospital").val(response.hospital);
					$("#department").val(response.department);
				},
				error : function() {
					console.log("錯誤訊息");
				}
			});
		}
	});
}
//檢查個人資料
function checkPersonalSetting(){
	if($("#name").val()==""){
		modalGenerator("個人資料修改", "請輸入姓名");
		return false;
	}
	else if($("#department").val()==""){
		modalGenerator("個人資料修改", "請輸入科別");
		return false;
	}
	else if(!checkCnEnNum($("#name").val()) || !checkCnEnNum($("#department").val())){
		modalGenerator("個人資料修改", "請輸入中文、英文或數字");
		return false;
	}
	return true;
}
/********************************************************************************/
//系統回報
function sendQuestion(){
	if(checkSendQuestion()){	//系統回報
		$.ajax({
			url : "DoctorSuggestionServlet",
			data : {
				state : "newDoctorSuggestion",
				email : $("#email").val(),
				content : htmlEscapeCharacter($("#question").val())
			},

			success : function(response) {
				modalGenerator("系統回報", "已成功送出，我們將盡快回覆！");	//模組產生器
				$("#email").val("");
				$('#question').val("");
			},
			error : function() {
				console.log("錯誤訊息");
			}
		});
	}
}
//檢查問題輸入
function checkSendQuestion(){
	if($("#email").val()==""){
		modalGenerator("系統回報", "請輸入電子信箱");
		return false;
	}else if($("#question").val()==""){
		modalGenerator("系統回報", "請輸入回報問題");
		return false;
	}
	return true;
}
/********************************************************************************/
//只能輸入英文數字
function checkEnNum(string) {
	var re = /^[a-zA-Z\d]+$/;
	if (!re.test(string))
		return false;
	return true;
}

//只能輸入中文英文數字
function checkCnEnNum(string) {
	var re = /^[a-zA-Z\d\u4E00-\u9FA5]+$/;
	if (!re.test(string))
		return false;
	return true;
}

function htmlEscapeCharacter(str){
	str = str.replace(/\'/g, "&#39;");
	str = str.replace(/\"/g, "&#34;");
	str = str.replace(/\\/g, "&#92;");
	return str;
}
