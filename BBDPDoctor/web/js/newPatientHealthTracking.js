//改網址用的
var url = "PatientHealthTrackingServlet";

//一進來就//取得下拉選單的值//取得所有項目的值
$(document).ready(function() {
	//取得存在local storage的doctoeID值並顯示出來
	var doctorID = window.localStorage.getItem('login');
	
	$.ajax({
		url : url,
		data : {
			state : "allType",
			doctorID : doctorID
		},
		dataType : "json",

		success : function(response) {
			//取得下拉選單的值
			for(var number = 0; number < response.typeList.length; number++){
				var itemType = returnEscapeCharacter(response.typeList[number]);	//項目類別名稱
				$("#healthType").append("<option value='op1'>"+itemType+"</option>");
			}			
			//取得所有項目的值
			for(number = 0; number < response.itemIDList.length; number++){
				var itemNumber = "item" + response.itemIDList[number];	//項目數字
				var itemName = returnEscapeCharacter(response.nameList[number]);				//項目名稱
				$("#itemSelect").append("<option value='"+itemNumber+"'>"+itemName+"</option>");
			}
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
});
//選取分類後顯示該分類所有的項目
function changeType(){
	$("#itemSelect").empty();
	document.getElementById('itemSelect').disabled=false;	//可以選取項目
	
	//取得存在local storage的doctoeID值並顯示出來
	var doctorID = window.localStorage.getItem('login');
	
	var select = $("#healthType :selected").text();
	
	//如果選回健康狀況追蹤分類的話，顯示全部的
	if(select=="請選擇分類"){
		$.ajax({
			url : url,
			data : {
				state : "allType",
				doctorID : doctorID
			},
			dataType : "json",

			success : function(response) {
				//取得所有項目的值
				for(number = 0; number < response.itemIDList.length; number++){
					var itemNumber = "item" + response.itemIDList[number];	//項目數字
					var itemName = returnEscapeCharacter(response.nameList[number]);				//項目名稱
					$("#itemSelect").append("<option value='"+itemNumber+"'>"+itemName+"</option>");
				}
			},
			error : function() {
				console.log("錯誤訊息");
			}
		});
	}
	//選什麼分類，顯示該分類的項目
	else{
		$.ajax({
			url : url,
			data : {
				state : "typeSelectItem",
				doctorID : doctorID,
				select: htmlEscapeCharacter(select)	//因為要拿進去資料庫比較，所以要替換特殊符號
			},
			dataType : "json",

			success : function(response) {
				//選取分類後取得該分類項目的值
				for(number = 0; number < response.itemIDList.length; number++){
					var itemNumber = "item" + response.itemIDList[number];	//項目數字
					var itemName = returnEscapeCharacter(response.nameList[number]);				//項目名稱
					$("#itemSelect").append("<option value='"+itemNumber+"'>"+itemName+"</option>");
				}
			},
			error : function() {
				console.log("錯誤訊息");
			}
		});
	}
}

//按下後新增項目給病患
$(document).ready(function() {
	$("#addItemToPatient").click(function(){
		var doctorID = window.localStorage.getItem('login');
		var itemSelect =  $('#itemSelect option:selected').val();
		
		$.ajax({
			url : url,
			data : {
				state : "addItemToPatient",
				doctorID : doctorID,
				itemSelect : itemSelect
			},
			dataType : "json",

			success : function(response) {
				console.log("結果 : " + response.result);
				modalGenerator("結果", response.result);
				if(response.result == "新增成功"){
					setTimeout(function(){
						window.location.href = 'NewPatientHealthTracking.html';
					},1500);
				}
			},
			error : function() {
				console.log("錯誤訊息");
			}
		});
	});
});