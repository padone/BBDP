$(document).ready(function() {
	console.log("登出成功");

	// 登出
	$.ajax({
		type : "POST",
		url : "LogoutServlet",
		data : {},
		dataType : "json",

		success : function(response) {
			if (response == true) {
				window.location.href = 'Login.html?state=logout';
			}
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
});
