var ajaxURL = "HealthTrackingServlet";

var judgment=1;	//judgement==1表示要輸入新增的分類

var detailIDLast = 0;	//目前detail新增到幾項了
var detailIDMax = 10; 	//最多只能有10個detail
var detailArray = new Array();	//儲存有多少detail

var changed = false;	//判斷是否有修改//用於離開網頁時判斷

//右上邊//取得下拉選單的值
$(document).ready(function() {
	$.ajax({
		url : ajaxURL,
		data : {
			state : "allItem"
		},
		dataType : "json",
		success : function(response) {
			//取得下拉選單的值
			for(var number = 0; number < response.typeList.length; number++){
				var itemType = response.typeList[number];	//項目類別名稱
				$("#healthType2").append("<option value='op1'>"+itemType+"</option>");
			}			
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
	
	checkChanged();			//是否修改檢查
});

//右下邊//按下刪除項目
function deleteDetail(detail){
	$("#detail"+detail+"_Div").remove();
	detailIDMax++;		//因為刪掉，所以把max增加
	
	var index=0;
	for(var index = 0; index<detailArray.length; index++){
		if(detailArray[index] == detail){
			var remove = detailArray.splice(index, 1);	//移除掉刪除的
			//console.log("remove : " + remove);
			//console.log("刪掉後目前的detailArray : " + detailArray);
			break;
		}
		else{
			console.log("刪除項目有問題");
		}
	}
}
//右下邊//按下新增項目
function addDetail(){
	if(detailIDLast<detailIDMax){
		detailArray.push(detailIDLast);	//把detail存進array裡
		$("#display").append(
				"<tr id='detail"+detailIDLast+"_Div'>"+
				"	<td>"+
				"		<form>"+
				"			<div class='form-group'><input type='text' class='form-control editInput' id='detail"+detailIDLast+"_name' placeholder='項目名稱' /></div>"+
				"			<div class='form-group'><button type='button' class='btn btn-default btn-sm' onClick=deleteDetail('"+detailIDLast+"')>刪除</button></div>"+
				"		</form>"+
				"	</td>"+
				"	<td>"+
				"		<form>"+
				"			<div class='form-group'><label class='radio-inline'><input type='radio' name='unitChecked_"+detailIDLast+"' onClick=yesUnit('"+detailIDLast+"')>有</label></div>"+
				"			<div class='form-group'><input type='text' class='form-control editInput' id='detail"+detailIDLast+"_unit' disabled /></div>"+
				"		</form>"+
				"	</td>"+
				"	<td>"+
				"		<form>"+
				"			<div class='form-group'><label class='radio-inline'><input type='radio' name='rangeChecked_"+detailIDLast+"' onClick=yesRange('"+detailIDLast+"')>有</label></div>"+
				"			<div class='form-group'><input type='text' class='form-control editInput' id='detail"+detailIDLast+"_range_1' disabled /></div>"+
				"			<div class='form-group'><h5>到</h5></div>"+
				"			<div class='form-group'><input type='text' class='form-control editInput' id='detail"+detailIDLast+"_range_2' disabled /></div>"+
				"		</form>"+
				"	</td>"+
				"	<td>"+
				"		<form>"+
				"			<div class='form-group'><label class='radio-inline'><input type='radio' name='warningUpChecked_"+detailIDLast+"' onClick=yesWarningUp('"+detailIDLast+"')>有</label></div>"+
				"			<div class='form-group row'>"+
				"				<div class='col-md-7' style='padding-right: 0;'><input type='text' class='form-control editInput' id='detail"+detailIDLast+"_upperLimit' disabled /></div>"+
				"				<div class='col-md-5' style='padding-right: 0;'><h5>以上</h5></div>"+
				"			</div>"+
				"			<div class='form-group'><label class='radio-inline'><input type='radio' name='warningDownChecked_"+detailIDLast+"' onClick=yesWarningDown('"+detailIDLast+"')>有</label></div>"+
				"			<div class='form-group row'>"+
				"				<div class='col-md-7' style='padding-right: 0;'><input type='text' class='form-control editInput' id='detail"+detailIDLast+"_lowerLimit' disabled /></div>"+
				"				<div class='col-md-5' style='padding-right: 0;'><h5>以下</h5></div>"+
				"			</div>"+
				"		</form>"+
				"	</td>"+
				"</tr>");
		detailIDLast++;
		
		checkChanged();			//是否修改檢查//這樣新增的才會也判斷
	}
	else{
		console.log("最多10個項目");
		//$("modal-1").attr("href", "#modal-container-1");	//這時才給予連結屬性，好讓modal產生
		modalGenerator("提示", "最多10個項目");
	}	
}

//新增按下儲存
$(document).ready(function() {
	$("#storage").click(function(){
		changed = false;
		var itemID = "";	//為了湊itemID用的
		modelStorage("storage", itemID);
	});
});


/*檢查*****************************************************************************************************/

//尚未儲存
$(window).on('beforeunload', function() {
	if(changed) return '尚有未儲存的修改。';
});
