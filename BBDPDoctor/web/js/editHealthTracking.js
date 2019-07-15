var ajaxURL = "HealthTrackingServlet";

var judgment=0;	//judgement==1表示要輸入新增的分類

//var detailIDLast;	//目前detail新增到幾項了
var detailArray = new Array();	//儲存有哪些detailID

var url = window.location.href;
var urlparts = url.split("?");
var IDparts = urlparts[1].split("=");
var itemID = IDparts[1];

var changed = false;	//判斷是否有修改//用於離開網頁時判斷

var typeName;	//分類名稱
$(document).ready(function() {
	checkItemID(itemID)		//檢查itemID
	
	editDefault();			//取得編輯前所有欄位的值

	checkChanged();			//是否修改檢查
});

//取得編輯前所有欄位的值
function editDefault(){
	$.ajax({
		url : ajaxURL,
		data : {
			state : "editDefault",
			itemID : itemID
		},
		dataType : "json",

		success : function(response) {
			detailArray = response.detailIDList;	//儲存update的時候用
			
			//取得文字敘述
			var selfDescription = response.selfDescription;	
			if(selfDescription == 1){
				document.getElementById("selfDescription").checked = true;
			}
			else if(selfDescription ==0){
				document.getElementById("selfDescription").checked = false;
			}

			$("#modelName").val(returnEscapeCharacter(response.modelName));	//解析
			allItem(returnEscapeCharacter(response.type));	//取得下拉選單的值	//解析
			
			$("#cycle").val(response.cycle);	
			$("#chart").val(response.chart);
			
			appendDetail(response);	//貼上細項的值
			
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
}

//取得下拉選單的值
function allItem(typeName){
	$.ajax({
		url : ajaxURL,
		data : {
			state : "allItem",
		},
		dataType : "json",

		success : function(response) {
			//取得下拉選單的值
			for(var number = 0; number < response.typeList.length; number++){
				var itemType = returnEscapeCharacter(response.typeList[number]);	//項目類別名稱//解析

				if(itemType == typeName){
					$("#healthType2").append("<option selected value='op1'>"+itemType+"</option>");	//資料庫裡的值
				}
				else
					$("#healthType2").append("<option value='op1'>"+itemType+"</option>");	//其他值
			}			
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
}

//貼上細項的值
function appendDetail(response){
	for(var i = 0; i < response.detailIDList.length; i++){
		$("#display").append(
				"<tr id='detail"+response.detailIDList[i]+"_Div'>"+
				"	<td>"+
				"		<form>"+
				"			<div class='form-group'><input type='text' class='form-control editInput' id='detail"+response.detailIDList[i]+"_name' placeholder='項目名稱' /></div>"+
				"			<div class='form-group'><button type='button' class='btn btn-default btn-sm' onClick=deleteDetail('"+response.detailIDList[i]+"')  disabled>刪除</button></div>"+
				"		</form>"+
				"	</td>"+
				"	<td>"+
				"		<form>"+
				"			<div class='form-group'><label class='radio-inline'><input type='radio' name='unitChecked_"+response.detailIDList[i]+"' onClick=yesUnit('"+response.detailIDList[i]+"')>有</label></div>"+
				"			<div class='form-group'><input type='text' class='form-control editInput' id='detail"+response.detailIDList[i]+"_unit' disabled /></div>"+
				"		</form>"+
				"	</td>"+
				"	<td>"+
				"		<form>"+
				"			<div class='form-group'><label class='radio-inline'><input type='radio' name='rangeChecked_"+response.detailIDList[i]+"' onClick=yesRange('"+response.detailIDList[i]+"')>有</label></div>"+
				"			<div class='form-group'><input type='text' class='form-control editInput' id='detail"+response.detailIDList[i]+"_range_1' disabled /></div>"+
				"			<div class='form-group'><h5>到</h5></div>"+
				"			<div class='form-group'><input type='text' class='form-control editInput' id='detail"+response.detailIDList[i]+"_range_2' disabled /></div>"+
				"		</form>"+
				"	</td>"+
				"	<td>"+
				"		<form>"+
				"			<div class='form-group'><label class='radio-inline'><input type='radio' name='warningUpChecked_"+response.detailIDList[i]+"' onClick=yesWarningUp('"+response.detailIDList[i]+"')>有</label></div>"+
				"			<div class='form-group row'>"+
				"				<div class='col-md-7' style='padding-right: 0;'><input type='text editInput' class='form-control' id='detail"+response.detailIDList[i]+"_upperLimit' disabled /></div>"+
				"				<div class='col-md-5' style='padding-right: 0;'><h5>以上</h5></div>"+
				"			</div>"+
				"			<div class='form-group'><label class='radio-inline'><input type='radio' name='warningDownChecked_"+response.detailIDList[i]+"' onClick=yesWarningDown('"+response.detailIDList[i]+"')>有</label></div>"+
				"			<div class='form-group row'>"+
				"				<div class='col-md-7' style='padding-right: 0;'><input type='text editInput' class='form-control' id='detail"+response.detailIDList[i]+"_lowerLimit' disabled /></div>"+
				"				<div class='col-md-5' style='padding-right: 0;'><h5>以下</h5></div>"+
				"			</div>"+
				"		</form>"+
				"	</td>"+
				"</tr>");
	}
	for(var i = 0; i < response.detailIDList.length; i++){
		var nameInput = "#detail"+response.detailIDList[i]+"_name";
		var unitInput = "#detail"+response.detailIDList[i]+"_unit";
		var range_1Input = "#detail"+response.detailIDList[i]+"_range_1";
		var range_2Input = "#detail"+response.detailIDList[i]+"_range_2";
		var upperLimitInput = "#detail"+response.detailIDList[i]+"_upperLimit";
		var lowerLimitInput = "#detail"+response.detailIDList[i]+"_lowerLimit";
		
		var unitChecked = document.getElementsByName("unitChecked_" + response.detailIDList[i]);
		var rangeChecked = document.getElementsByName("rangeChecked_" + response.detailIDList[i]);
		var warningUpChecked = document.getElementsByName("warningUpChecked_" + response.detailIDList[i]);
		var warningDownChecked = document.getElementsByName("warningDownChecked_" + response.detailIDList[i]);
					
		$(nameInput).val(returnEscapeCharacter(response.nameList[i]));	//解析
		if(response.unitList[i] == "NULL")	$(unitInput).val("");
		else{	
			$(unitInput).val(returnEscapeCharacter(response.unitList[i]));	//解析
			document.getElementById("detail"+response.detailIDList[i]+"_unit").disabled=false;	//可輸入單位
			unitChecked[0].checked=true;	//打勾
		}
		
		if(response.range_1_List[i] == "NULL")	$(range_1Input).val("");
		else{
			$(range_1Input).val(response.range_1_List[i]);
			document.getElementById("detail"+response.detailIDList[i]+"_range_1").disabled=false;	//可輸入範圍1
			document.getElementById("detail"+response.detailIDList[i]+"_range_2").disabled=false;	//可輸入範圍2
			rangeChecked[0].checked=true;	//打勾
		}
					
		if(response.range_2_List[i] == "NULL")	$(range_2Input).val("");
		else{
			$(range_2Input).val(response.range_2_List[i]);
			document.getElementById("detail"+response.detailIDList[i]+"_range_1").disabled=false;	//可輸入範圍1
			document.getElementById("detail"+response.detailIDList[i]+"_range_2").disabled=false;	//可輸入範圍2
			rangeChecked[0].checked=true;	//打勾
		}
					
		if(response.upperLimitList[i] == "NULL")	$(upperLimitInput).val("");
		else{
			$(upperLimitInput).val(response.upperLimitList[i]);
			document.getElementById("detail"+response.detailIDList[i]+"_upperLimit").disabled=false;	//可輸入警戒值(以上)
			warningUpChecked[0].checked=true;	//打勾
		}

		if(response.lowerLimitList[i] == "NULL")	$(lowerLimitInput).val("");
		else{
			$(lowerLimitInput).val(response.lowerLimitList[i]);
			document.getElementById("detail"+response.detailIDList[i]+"_lowerLimit").disabled=false;	//可輸入警戒值(以下)
			warningDownChecked[0].checked=true;	//打勾
		}
	}
	
	checkChanged();			//是否修改檢查
}

//修改按下儲存
$(document).ready(function() {
	$("#update").click(function(){
		changed = false;
		modelStorage("update", itemID);
	});
});


//刪除
$(document).ready(function() {
	$("#delete").click(function(){		
		modalGeneratorCancel("刪除", "確定刪除此健康模板嗎？");
		
	});
});
//刪除//modalGeneratorCancel()
function determine(){
	$.ajax({
		url : ajaxURL,
		data : {
			state : "deleteItem",
			itemID : itemID,
			detailArray : detailArray
		},
		dataType : "json",

		success : function(response) {
			console.log("提示 : " + response.result);
			modalGenerator("提示", response.result);
			if(response.result == "刪除成功"){
				changed = false;
				setTimeout(function(){
					window.location.href = 'HealthTracking.html';
				},1500);
			}
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
}


/*檢查*****************************************************************************************************/

//檢查itemID
function checkItemID(itemID){
	$.ajax({
		url : ajaxURL,
		data : {
			state : "checkItemID",
			itemID : itemID
		},
		dataType : "json",
		success : function(response) {
			if(response){
				modalGenerator("警告", "網址錯誤，健康模板不存在");
				setTimeout(function(){
					window.location.href = 'HealthTracking.html';
				},1500);
			}
		},
		error : function() {console.log("錯誤訊息");}
	});
}

//尚未儲存
$(window).on('beforeunload', function() {
	if(changed) return '尚有未儲存的修改。';
});

