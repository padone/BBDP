		var ajaxURL = "HealthTrackingServlet";
		
		//一進來取得所有項目//取得下拉選單的值//取得黃色項目div的值
		$(document).ready(function() {
			$("#itemDiv").empty();
			
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
						$("#healthType1").append("<option value=''>"+itemType+"</option>");
					}
					//取得黃色項目div的值
					for(number = 0; number < response.itemIDList.length; number++){
						var itemNumber = "item" + response.itemIDList[number];	//項目數字
						var itemName = response.nameList[number];				//項目名稱
						itemDivAppend(itemNumber, itemName)	//append黃色方塊項目
					}
				},
				error : function() {
					console.log("錯誤訊息");
				}
			});
		});

		//選取分類後的項目
		function changeType1(index){
			$("#itemDiv").empty();
			var select = $("#healthType1 :selected").text();
			
			//如果選回健康狀況追蹤分類的話，顯示全部的
			if(select=="健康狀況追蹤模板分類"){
				$.ajax({
					url : ajaxURL,
					data : {
						state : "allItem"
					},
					dataType : "json",
		
					success : function(response) {
						//取得黃色項目div的值
						for(number = 0; number < response.itemIDList.length; number++){
							var itemNumber = "item" + response.itemIDList[number];	//項目數字
							var itemName = response.nameList[number];				//項目名稱
							itemDivAppend(itemNumber, itemName)	//append黃色方塊項目
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
					url : ajaxURL,
					data : {
						state : "typeSelect",
						select: htmlEscapeCharacter(select)
					},
					dataType : "json",
		
					success : function(response) {
						//選取分類後取得黃色項目div的值
						for(number = 0; number < response.itemIDList.length; number++){
							var itemNumber = "item" + response.itemIDList[number];	//項目數字
							var itemName = response.nameList[number];				//項目名稱
							itemDivAppend(itemNumber, itemName)	//append黃色方塊項目
						}
					},
					error : function() {
						console.log("錯誤訊息");
					}
				});
			}	
		}
		
		//append黃色方塊項目
		function itemDivAppend(itemNumber, itemName){
			$("#itemDiv").append(
					"<a href='EditHealthTracking.html?itemID="+itemNumber+"' class='list-group-item left-list-item healthTracking'>"+
					"	<h4 class='list-group-item-heading'>"+itemName+"</h4>"+
					"</a>");
		}
		
		
		/*打勾勾*****************************************************************************************************/
		//右上邊//如果有選類別名稱的話，就不給新增類別名稱	//judgement==1表示要輸入新增的分類
		function changeType2(){
			var s =  $('#healthType2 option:selected').val();
			if(s=="all"){
				$("#healthTypeInput").val("");		//清空欄位值
				document.getElementById('healthTypeInput').disabled=false;
				judgment = 1;
			}else{
				judgment = 0;
				$("#healthTypeInput").val("");		//清空欄位值
				document.getElementById('healthTypeInput').disabled=true;
			}
		}
		
		//右下邊//勾有，才可以輸入單位;
		var selectUnit=1;
		function yesUnit(detail){
			var inputName = "detail"+detail+"_unit";
			var radioName = "unitChecked_" + detail;
			var unitChecked = document.getElementsByName(radioName);
			
			if(selectUnit==0){	//我沒有勾選false
				unitChecked[0].checked=false;
				selectUnit=1;
				$("#"+inputName).val("");		//清空欄位值
				document.getElementById(inputName).disabled=true;	//不可輸入單位
			}
			else{	//我有勾選true
				selectUnit=0;
				document.getElementById(inputName).disabled=false;	//可輸入單位
			}
		}

		//右下邊//勾有，才可以輸入合理範圍
		var selectRange=1;
		function yesRange(detail){
			var inputName1 = "detail"+detail+"_range_1";
			var inputName2 = "detail"+detail+"_range_2";
			var radioName = "rangeChecked_" + detail;
			var checked = document.getElementsByName(radioName);
			
			if(selectRange==0){	//我沒有勾選false
				checked[0].checked=false;
				selectRange=1;
				$("#"+inputName1).val("");		//清空欄位值1
				$("#"+inputName2).val("");		//清空欄位值2
				document.getElementById(inputName1).disabled=true;	//不可輸入範圍1
				document.getElementById(inputName2).disabled=true;	//不可輸入範圍2
			}
			else{	//我有勾選true
				selectRange=0;
				document.getElementById(inputName1).disabled=false;	//可輸入範圍1
				document.getElementById(inputName2).disabled=false;	//可輸入範圍2
			}
		}

		//右下邊//勾有，才可以輸入警示值(以上)
		var selectWarningUp=1;
		function yesWarningUp(detail){
			var inputName = "detail"+detail+"_upperLimit";
			var radioName = "warningUpChecked_" + detail;
			var checked = document.getElementsByName(radioName);
			
			if(selectWarningUp==0){	//我沒有勾選false
				checked[0].checked=false;
				selectWarningUp=1;
				$("#"+inputName).val("");		//清空欄位值1
				document.getElementById(inputName).disabled=true;	//不可輸入警示值(以上)
			}
			else{	//我有勾選true
				selectWarningUp=0;
				document.getElementById(inputName).disabled=false;	//可輸入警示值(以上)
			}
		}

		//右下邊//勾有，才可以輸入警示值(以下)
		var selectWarningDown=1;
		function yesWarningDown(detail){
			var inputName = "detail"+detail+"_lowerLimit";
			var radioName = "warningDownChecked_" + detail;
			var checked = document.getElementsByName(radioName);
			
			if(selectWarningDown==0){	//我沒有勾選false
				checked[0].checked=false;
				selectWarningDown=1;
				$("#"+inputName).val("");		//清空欄位值1
				document.getElementById(inputName).disabled=true;	//不可輸入警示值(以下)
			}
			else{	//我有勾選true
				selectWarningDown=0;
				document.getElementById(inputName).disabled=false;	//可輸入警示值(以下)
			}
		}
		/*儲存*****************************************************************************************************/
		//儲存
		function modelStorage(state, itemID){
			var flag = false;
			var errors = "";
			var modelName = $("#modelName").val();	//取得模板名稱
	
			//判斷是否有輸入模板名稱
			if(!modelNameJudge(modelName)['flag']){
				errors = modelNameJudge(modelName)['errors'];
			}
			//判斷是否有輸入分類名稱
			else if(!typeNameJudge()['flag']){
				errors = typeNameJudge()['errors'];
			}
			//判斷是否有輸入項目
			else if(!detailJudge()['flag']){
				errors = detailJudge()['errors'];
			}
			
			//判斷是否有輸入模板名稱//判斷是否有輸入分類名稱//判斷是否有輸入項目
			if(modelNameJudge(modelName)['flag']&&typeNameJudge()['flag']&&detailJudge()['flag']){
				var cycle = $('#cycle option:selected').val();	//取得週期
				var chart = $('#chart option:selected').val();	//取得圖表類型
				var typeName;
				var selfDescription = document.getElementById("selfDescription").checked;	//取得文字敘述
				
				//分類為自行輸入的名稱
				if(judgment == 1){
					typeName = $("#healthTypeInput").val();
				}
				//分類為選擇已有的分類
				else {
					typeName = $("#healthType2 :selected").text();
				}
				
				//文字敘述//1等於有勾選 0等於沒勾選
				if(selfDescription){
					selfDescription = 1;
				}
				else{
					selfDescription = 0;
				}
				
				//取得項目內容
				var nameList = new Array();
				var unitList = new Array();
				var range_1_List = new Array();
				var range_2_List = new Array();
				var upperLimitList = new Array();
				var lowerLimitList = new Array();
				
				for(var index = 0; index < detailArray.length; index++){
					nameList.push(htmlEscapeCharacter($("#detail"+detailArray[index]+"_name").val()));	//替換
					unitList.push(htmlEscapeCharacter($("#detail"+detailArray[index]+"_unit").val()));	//替換
					range_1_List.push($("#detail"+detailArray[index]+"_range_1").val());
					range_2_List.push($("#detail"+detailArray[index]+"_range_2").val());
					upperLimitList.push($("#detail"+detailArray[index]+"_upperLimit").val());
					lowerLimitList.push($("#detail"+detailArray[index]+"_lowerLimit").val());				
				}
				
				$.ajax({
					url : ajaxURL,
					data : {
						state : state,
						itemID : itemID,				//只有edit需要傳入
						detailArray : detailArray,		//只有edit需要傳入
						modelName : htmlEscapeCharacter(modelName),	//替換
						typeName : htmlEscapeCharacter(typeName),	//替換
						nameList : nameList,
						unitList : unitList,
						range_1_List : range_1_List,
						range_2_List : range_2_List,
						upperLimitList : upperLimitList,
						lowerLimitList : lowerLimitList,
						cycle : cycle,
						chart : chart,
						selfDescription : selfDescription
					},
					dataType : "json",
		
					success : function(response) {
						console.log("提示 : " + response.result);
						modalGenerator("提示", response.result);
						//新增的儲存
						if(state == "storage"){
							//$("modal-1").attr("href", "#modal-container-1");	//這時才給予連結屬性，好讓modal產生
							if(response.result == "新增成功"){
								setTimeout(function(){
									window.location.href = 'HealthTracking.html';
								},1500);
							}
						}
						//修改的儲存
						else if(state == "update"){
							setTimeout(function(){
								window.location.href = 'EditHealthTracking.html?itemID='+ itemID;
							},1500);
						}
					},
					error : function() {
						console.log("錯誤訊息");
					}
				});
			}
			else{
				//$("modal-1").attr("href", "#modal-container-1");	//這時才給予連結屬性，好讓modal產生
				console.log("提示 : " + errors);
				modalGenerator("提示", errors);
			}
		}
			
		//判斷是否有輸入模板名稱
		function modelNameJudge(modelName){
			var flag;
			var errors = "";
			
			//是否已經輸入模板名稱
			if(modelName == ""){
				errors = "請輸入模板名稱";
				flag = false;
			}
			else{flag = true;}
			
			return {flag:flag, errors:errors};
		}

		//判斷是否有輸入分類名稱
		function typeNameJudge(){
			var flag;
			var errors = "";
			var typeName;
			
			//分類為自行輸入的名稱
			if(judgment == 1){
				typeName = $("#healthTypeInput").val();
				if(typeName == ""){
					errors = "請輸入要新增的分類名稱";
					flag = false;
				}
				else{flag = true;}
			}
			//分類為選擇已有的分類
			else {flag = true;} 
			
			return {flag:flag, errors:errors};
		}

		//判斷是否有輸入項目
		function detailJudge(){
			var flag = true;
			var errors = "";
			
			//如果detailArray長度=0 代表沒有新增任何項目
			if(detailArray.length == 0){
				errors = "請新增至少一個項目";
				flag = false;
			}
			else{
				for(var index = 0; index < detailArray.length; index++){
					if($("#detail"+detailArray[index]+"_name").val() == ""){
						errors = "請輸入項目名稱";
						flag = false;
						break;
					}		
				}
			}
			
			return {flag:flag, errors:errors};
		}

		/*檢查*****************************************************************************************************/
		
		//是否修改檢查
		function checkChanged(){
			$("input[type='checkbox']").change(function() {				//偵測頁面上任一個checkbox有沒有被修改
				changed = true;
			});
			$("input[type='radio']").change(function() {				//偵測頁面上任一個radio有沒有被修改
				changed = true;
			});
			$("input[type='text'].editInput").change(function() {		//偵測頁面上任一個文字輸入欄位有沒有被修改
				changed = true;
			});
			$(".editSelect").change(function () {						//偵測頁面上任一個下拉式選單有沒有被修改
				changed = true;
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