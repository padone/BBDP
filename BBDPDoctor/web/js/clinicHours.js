//取得門診時間
$(document).ready(function(){
	$.ajax({
		type: "POST",
		url: "ClinicHoursServlet",
		data: {
			option: "getClinicHours"
		},
		dataType: "json",
		success: function(response) {
			if(JSON.stringify(response) == "{}") {
				location.href = "EditClinicHours.html";
			}
			//連絡電話
			$("#phone").html(response.CHPhone);
			//備註
			$("#ps").empty();
			for(var i=0; i<response.CHPS.length; i++) {
				$("#ps").append("<div style='margin-top: 1vh; margin-bottom: 1vh;'><span class='glyphicon glyphicon-pencil'></span>&nbsp;&nbsp;&nbsp;" + response.CHPS[i] + "</div>");
			}
			//門診時間
			$("#ch > tr:first > td:first").html(response.CHTime.morning.time);
			$("#ch > tr:eq(1) > td:first").html(response.CHTime.afternoon.time);
			$("#ch > tr:last > td:first").html(response.CHTime.evening.time);
			for(var i=0; i<7; i++) {
				//morning
				if(response.CHTime.morning.week[i].yesOrNo == "yes" &&　response.CHTime.morning.week[i].ps != "") {
					$("#ch > tr:first > td:eq(" + (i+1) + ")").html("<i class='fa fa-check'></i><div>備註：" + response.CHTime.morning.week[i].ps + "</div>");
				}
				else if(response.CHTime.morning.week[i].yesOrNo == "yes" &&　response.CHTime.morning.week[i].ps == "") {
					$("#ch > tr:first > td:eq(" + (i+1) + ")").html("<i class='fa fa-check'></i>");
				}
				//afternoon
				if(response.CHTime.afternoon.week[i].yesOrNo == "yes" &&　response.CHTime.afternoon.week[i].ps != "") {
					$("#ch > tr:eq(1) > td:eq(" + (i+1) + ")").html("<i class='fa fa-check'></i><div>備註：" + response.CHTime.afternoon.week[i].ps + "</div>");
				}
				else if(response.CHTime.afternoon.week[i].yesOrNo == "yes" &&　response.CHTime.afternoon.week[i].ps == "") {
					$("#ch > tr:eq(1) > td:eq(" + (i+1) + ")").html("<i class='fa fa-check'></i>");
				}
				//evening
				if(response.CHTime.evening.week[i].yesOrNo == "yes" &&　response.CHTime.evening.week[i].ps != "") {
					$("#ch > tr:last > td:eq(" + (i+1) + ")").html("<i class='fa fa-check'></i><div>備註：" + response.CHTime.evening.week[i].ps + "</div>");
				}
				else if(response.CHTime.evening.week[i].yesOrNo == "yes" &&　response.CHTime.evening.week[i].ps == "") {
					$("#ch > tr:last > td:eq(" + (i+1) + ")").html("<i class='fa fa-check'></i>");
				}
			}
		},
		error: function() {
			console.log("clinicHours.js getClinicHours error");
		}
	});
});