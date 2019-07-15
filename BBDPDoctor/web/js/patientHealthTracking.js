//改網址用的
var url = "PatientHealthTrackingServlet";

//一進來取得所有項目
$(document).ready(function() {
	$.ajax({
		url : url,
		data : {
			state : "allItem",
		},
		dataType : "json",

		success : function(response) {
			for(var number=0; number<response.itemIDList.length; number++){
				var itemName = returnEscapeCharacter(response.itemNameList[number]);
				var itemRecord = response.itemRecordList[number];
				var itemLastTime = response.itemLastTimeList[number];
				var itemID = response.itemIDList[number];
				var url = "EditPatientHealthTracking.html?itemID=" + response.itemIDList[number] ;
				
				var detailName = response.detailNameList[number];
				var detailValue = response.detailValueList[number];
				var detailUnit = response.detailUnitList[number];

				var appendString = 
					"<div class='panel panel-default healthTrackingTemplate'>"+
					"	<div class='panel-body'>"+
					"		最近一個月的"+itemName+"紀錄："+itemRecord+"筆<br>"+
					"		最後一次"+itemName+"紀錄："+itemLastTime+"<br>";

				if(detailName.length > 2){	//不能顯示超過2項
					detailName.length = 2;
				}
			
				for(var i=0; i<detailName.length; i++){
					if(detailUnit[i] == 'NULL'){	//如果單位為NULL 就不要顯示
						detailUnit[i] = "";
					}
					appendString+=" "+returnEscapeCharacter(detailName[i])+" : "+detailValue[i]+returnEscapeCharacter(detailUnit[i]);
					if(i != 1){	//最多就三項，超過2項要補...，所以第三項的<br>要扣掉
						appendString+="<br>";
					}
				}	
				
				if(detailName.length < 2){	//少於2項要補<br>
					for(var i=0; i<2-detailName.length; i++){
						appendString+= "<br>";
					}
				}
				//超過三項補...//因為前面改了detailName.length = 2;所以用未使用過的detailUnit.length判斷
				if(detailUnit.length > 2){	
					appendString+= "...";
				}
				appendString+=
					"	</div>"+
					"	<a href='EditPatientHealthTracking.html?itemID="+itemID+"'>"+
					"		<div class='panel-footer'>"+
					"			<span class='pull-left'>詳細資料</span>"+
					"			<span class='pull-right'><i class='fa fa-arrow-circle-right'></i></span>"+
					"			<div class='clearfix'></div>"+
					"		</div>"+
					"	</a>"+
					"</div>"
				$("#displayItem").append(appendString);
			}
			
			//設定滑動
			$(function() {
				$(".healthTrackingTemplate").parent().mousewheel(function(event, delta) {
					this.scrollLeft -= (delta * 30);
					event.preventDefault();
				});
			});
			
			//設定滑動
			/*$(document).ready(function(){
				var w = $("#divWidth").width();
				//console.log("width 寬度 : " + w);
				
				$('.bxslider').bxSlider({
					slideWidth: w,
					minSlides: 3,
					maxSlides: 3,
					slideMargin: 10
				});
			});	*/
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
});

/********************************************************************************************/

//檢查itemID
function checkItemID(itemID){
	$.ajax({
		url : url,
		data : {
			state : "checkItemID",
			itemID : itemID
		},
		dataType : "json",
		success : function(response) {
			if(response){
				modalGenerator("警告", "網址錯誤，健康追蹤不存在");
				setTimeout(function(){
					window.location.href = 'PatientHealthTracking.html';
				},1500);
			}
		},
		error : function() {console.log("錯誤訊息");}
	});
}

/********************************************************************************************/
//替換
function htmlEscapeCharacter(str){
	str = str.replace(/\'/g, "&#39;");
	str = str.replace(/\"/g, "&#34;");
	str = str.replace(/\\/g, "&#92;");
	return str;
}
//解析
function returnEscapeCharacter(str){
	str = str.replace(/&#39;/g, "\'");
	str = str.replace(/&#34;/g, '\"');
	str = str.replace(/&#92;/g, '\\');
	return str;
}

//移除html
function removeHTML(strText){
    var regEx = /<[^>]*>/g;
    return strText.replace(regEx, "");
}