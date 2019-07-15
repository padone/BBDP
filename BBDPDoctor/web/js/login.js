	var url = window.location.href;
	var urlparts = url.split("?");
	/*尚未登入及登出*/
	if(url != urlparts){
		var judgeLoginparts = urlparts[1].split("=");
		var judgeLogin = judgeLoginparts[1];
		$(document).ready(function() {
			$("#loginMessage").empty();
			/*登出*/
			if(judgeLogin == "logout")
				$("#loginMessage").append("已登出");
			/*尚未登入*/
			if(judgeLogin == "hasNotLogin")
				$("#loginMessage").append("請登入");
		});
	}
	
	/*判斷輸入帳密是否正確*/
	$(document).ready(function() {
		$("#loginId").click(function(e) {
			e.preventDefault();
			console.log(utf8_to_b64($("#account").val()));
			$.ajax({
				type : "POST",
				url : "LoginVerificationServlet",
				data : {
					state : "login",
					account : $("#account").val(),
					password : utf8_to_b64($("#password").val())	//加密
				},
				dataType : "json",

				success : function(response) {		
					if (response.result == "登入成功") {	//登入成功
						window.location.href = 'Homepage.html';
					}
					else {	//登入失敗
						$("#loginMessage").empty();
						$("#loginMessage").append(response.result);
						console.log(response.result);
					}
				},
				error : function() {
					console.log("錯誤訊息");
				}
			});
		});
	});