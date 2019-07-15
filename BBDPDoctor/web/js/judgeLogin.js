$(document).ready(function() {
		$.ajax({
			type : "POST",
			url : "LoginVerificationServlet",
			data : {
				state : "judgeLogin"
			},
			dataType : "json",

			success : function(response) {	
				if(response)	//true已登入
					console.log("已登入 : " + response);
				else{
					console.log("尚未登入 : " + response);
					window.location.href = 'Login.html?state=hasNotLogin';
				}
			},
			error : function() {
				console.log("錯誤訊息");
			}
		});
	});

