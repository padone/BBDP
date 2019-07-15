var doctorNoticeIDArray = [];
var typeArray = [];
var contentArray = [];
var noticeAmount;

function htmlEscapeCharacter(str){
str = str.replace(/\'/g, "&#39;");
str = str.replace(/\"/g, "&#34;");
str = str.replace(/\\/g, "&#92;");
return str;
}
function returnEscapeCharacter(str){
str = str.replace(/&#39;/g, "\'");
str = str.replace(/&#34;/g, '\"');
str = str.replace(/&#92;/g, '\\');
return str;
}

$(document).ready(function() {
	$.getScript("js/judgeLogin.js");		//判斷登入
	initialNoticeList();
	initialOption();
});

//初始化左邊的列表
function initialNoticeList(){
	$("#noticeList").empty();
	$.ajax({
		type: "GET",
		url: "NoticeServlet",
		data: {option: "getDoctorNitice"},
		dataType: "json",
												
		success : function(response){
			var item = "";
			noticeAmount = response.length;
			for(var i = 0; i<response.length; i++){
				doctorNoticeIDArray[i] = response[i]["doctorNoticeID"];
				typeArray[i] = returnEscapeCharacter(response[i]["type"]);
				contentArray[i] = returnEscapeCharacter(response[i]["content"]);
				item += getItem(i);
			}
			$("#noticeList").append(item);
		},
		 
		error : function(){
			console.log("server沒有回應");
		}
	});
}

//初始化左邊的選單
function initialOption(){
	$.ajax({
		type: "GET",
		url: "NoticeServlet",
		data: {option: "searchNoticeType"},
		dataType: "json",
												
		success : function(response){
			$("#leftSelectType").empty();
			var option = "<option value=''>注意事項分類</option>";

			for(var i = 0; i<response.length; i++){  	
				option += "<option value='"+ response[i]["type"] +"'>"+response[i]["type"]+"</option>";											
			}
			$("#leftSelectType").append(option);
		},
		 
		error : function(){
			console.log("server沒有回應");
		}
	});
}

//右邊選單
function changeType(){
	if($('#selectType option:selected').val().length > 0){
		$('#inputType').val("");
		$('#inputType').attr('disabled', true);		//不可輸入
	}
	else{
		$('#inputType').attr('disabled', false);	//可輸入
	}
}

//左邊選單
function changeLeftType(){
	$("#noticeList").empty();
	var item = "";

	if($('#leftSelectType option:selected').val().length == 0){		//所有類型
		for(var i = 0 ; i < noticeAmount; i ++){
			item += getItem(i);
		}
	}
	else{
		for(var i = 0 ; i < noticeAmount; i ++){
			if(typeArray[i] == $('#leftSelectType option:selected').val()){
				item += getItem(i);
			}
		}
	}
	$("#noticeList").append(item);
}

//列表項目
function getItem(i){
	return "<a href='EditNotice.html?doctorNoticeID="+doctorNoticeIDArray[i]+"' class='list-group-item left-list-item notice'>" +
				"<h4 class='list-group-item-heading' style = 'white-space: nowrap;text-overflow: ellipsis;width: "+$("#noticeList").width()*0.9+"px;display: block;overflow: hidden;'>" + contentArray[i] + "</h4>" +
			"</a>";
}