		var url = "PatientInstructionServlet";
		var froalaUrl = "InstructionFroalaServlet";
		
		$(document).ready(function() {
			$("#blueDiv").empty();

			//搜尋所有衛教資訊
			$.ajax({
				url : url,
				data : {
					state : "searchAllInstruction"
				},
				dataType : "json",
				success : function(response) {
					//取得下拉選單的值
					for(var number = 0; number < response.typeList.length; number++){
						$("#type1").append("<option value=''>"+returnEscapeCharacter(response.typeList[number])+"</option>");
					}	
					blueDivAppend(response);	//append藍色方塊項目//取得標題、時間的值
				},
				error : function() {console.log("錯誤訊息");}
			});
		});
		
		//append藍色方塊項目//取得標題、時間的值
		function blueDivAppend(response){
			//response.IDList[number]
			for(number = 0; number < response.IDList.length; number++){
				$("#blueDiv").append(
						"<a href='EditPatientInstruction.html?patientInstructionID="+response.IDList[number]+"' class='list-group-item left-list-item patientInstruction'>"+
						"	<h4 class='list-group-item-heading'>"+returnEscapeCharacter(response.titleList[number])+"</h4>"+
						"	<p class='list-group-item-text text-right'>"+response.dateList[number]+"</p>"+
						"</a>");
			}
		}
		
		//選取分類後的項目
		function changeType1(index){
			$("#blueDiv").empty();
			var select = $("#type1 option:selected").val();
						
			//如果選回衛教資訊分類的話，顯示全部的
			if(select=="all"){
				$.ajax({
					url : url,
					data : {
						state : "searchAllInstruction"
					},
					dataType : "json",
		
					success : function(response) {
						blueDivAppend(response);	//append藍色方塊項目//取得標題、時間的值
					},
					error : function() {console.log("錯誤訊息");}
				});
			}
			//選什麼分類，顯示該分類的項目
			else{
				$.ajax({
					url : url,
					data : {
						state : "typeSelect",
						select: htmlEscapeCharacter($("#type1 :selected").text())
					},
					dataType : "json",
		
					success : function(response) {
						blueDivAppend(response);	//append藍色方塊項目//取得標題、時間的值
					},
					error : function() {console.log("錯誤訊息");}
				});
			}	
		}
		
		/********************************************************************************************/

		//前往管理留言
		function goToComment(){
			window.location.href = 'EditPatientInstructionComment.html?patientInstructionID=' + patientInstructionID;
		}

		//前往編輯
		function goToEdit(){
			window.location.href = 'EditPatientInstructionEdit.html?patientInstructionID=' + patientInstructionID;
		}

		//前往文章
		function goToArticle(){
			window.location.href = 'EditPatientInstruction.html?patientInstructionID=' + patientInstructionID;
		}		
		/********************************************************************************************/
		
		//如果選回衛教資訊分類的話，顯示typeInput//選分類，關掉typeInput
		function changeType2(index){
			var select = $("#type2 option:selected").val();
			if(select=="all"){
				$("#typeInput").val("");		//清空欄位值
				document.getElementById('typeInput').disabled=false;	//可輸入
			}
			else{
				$("#typeInput").val("");		//清空欄位值
				document.getElementById('typeInput').disabled=true;		//不可輸入
			}	
		}

		//如果沒選症狀的話，顯示symptomInput//有選，關掉symptomInput
		function changeSymptom(index){
			var select = $("#symptom option:selected").val();
			if(select=="all"){
				$("#symptomInput").val("");		//清空欄位值
				document.getElementById('symptomInput').disabled=false;	//可輸入
			}
			else{
				$("#symptomInput").val("");		//清空欄位值
				document.getElementById('symptomInput').disabled=true;		//不可輸入
			}	
		}
		
		/********************************************************************************************/

		//手機模擬器
		function changeHtmlEditor(){
			$('#F12Modal').modal('show');
			$("#phoneView").empty();
			$("#phoneView").append($('div#froala-editor').froalaEditor('html.get', true));
			$("#phoneView img").css("height","auto");
			$("#phoneView iframe").css("height","auto");
			changeWord();	//修改文字大小px變成vw
		}
		
		//修改文字大小px變成vw
		function changeVW(px){
			var screenWidth = 360;
			var innerW = $("#F12Frame").css("width").toString();
			var outerW = $(window).width();
			var vw = (px * 100 / screenWidth)*(innerW.substr(0, innerW.length-2)/outerW) ;
			return vw;
		}
		function changeWord() {
			for(var i = 0; i < $('#phoneView >p>span').length; i++){
				var px = $('#phoneView >p>span').eq(i).css("font-size").toString();
				var vw = changeVW(px.substr(0, px.length-2));				
				$('#phoneView >p>span').eq(i).css("font-size", vw+"vw");
			}
			$('#phoneView >p').css("font-size", changeVW(14)+"vw");		
			$('#phoneView >ul').css("font-size", changeVW(14)+"vw");			
			$('#phoneView >li').css("font-size", changeVW(14)+"vw");			
		}

		function changeModalSize(){
			$("#phoneView").empty();
			$("#phoneView").append($('div#froala-editor').froalaEditor('html.get', true));
			$("#phoneView img").css("height","auto");
			$("#phoneView iframe").css("height","auto");
			
			switch($('#modalSize option:selected').val()) {
				case "1":{
					$("#F12Frame").css("width","36vh");
					$("#phoneView").css("height","64vh").css("font-size","calc(36vh * 0.05)");//Galaxy S5
					break;
				}case "2":{
					$("#F12Frame").css("width","38.4vh");
					$("#phoneView").css("height","64vh").css("font-size","calc(38.4vh * 0.05)");//LG Optimus L70
				break;
				}case "3":{
					$("#F12Frame").css("width","32vh");
					$("#phoneView").css("height","56.8vh").css("font-size","calc(32vh * 0.05)");//iPhone 5
				break;
				}case "4":{
					$("#F12Frame").css("width","37.5vh");
					$("#phoneView").css("height","66.7vh").css("font-size","calc(37.5vh * 0.05)");//iPhone 6
				break;
				}case "5":{
					$("#F12Frame").css("width","41.4vh");
					$("#phoneView").css("height","73.6vh").css("font-size","calc(41.4vh * 0.05)");//iPhone 6 Plus
				break;
				}case "6":{
					$("#F12Frame").css("width","53.76vh");
					$("#phoneView").css("height","71.68vh").css("font-size","calc(53.76vh * 0.05)");//ipad 0.8
				break;
				}default: console.log("modalSize error");
			}
			changeWord();	//修改文字大小px變成vw
		}
		
		/********************************************************************************************/

		//印出錯誤
		function errorsJudge(){
			if(!titleJudge()['flag']){
				return titleJudge()['errors'];
			}
			else if(!typeJudge()['flag']){
				return typeJudge()['errors'];
			}
			else if(!symptomJudge()['flag']){
				return symptomJudge()['errors'];
			}
			else if(!htmlJudge()['flag']){
				return htmlJudge()['errors'];
			}
		}

		//判斷是否有輸入文章標題
		function titleJudge(){
			var flag;
			var errors = "";
			var title = $("#title").val();	//取得文章標題
			
			//是否已經輸入文章標題
			if(title == ""){
				errors = "請輸入文章標題";
				flag = false;
			}
			else{flag = true;}

			return {flag:flag, errors:errors, title:htmlEscapeCharacter(title)};
		}

		//判斷是否有輸入衛教資訊分類
		function typeJudge(){
			var flag;
			var errors = "";
			var type = "";
			
			//分類為選擇已有的分類
			if(document.getElementById('typeInput').disabled){
				flag = true;
				type = $("#type2 :selected").text();
			}
			//分類為自行輸入的名稱
			else {
				if($("#typeInput").val() == ""){
					errors = "請輸入要新增的分類名稱";
					flag = false;
				}
				else{
					flag = true;
					type = $("#typeInput").val();
				}
			} 
			return {flag:flag, errors:errors, type:htmlEscapeCharacter(type)};
		}

		//判斷是否有輸入症狀
		function symptomJudge(){
			var flag;
			var errors = "";
			var symptom = "";
			
			//分類為選擇已有的分類
			if(document.getElementById('symptomInput').disabled){
				flag = true;
				symptom = $("#symptom :selected").text();
			}
			//分類為自行輸入的名稱
			else {
				if($("#symptomInput").val() == ""){
					errors = "請輸入要新增的症狀名稱";
					flag = false;
				}
				else{
					flag = true;
					symptom = $("#symptomInput").val();
				}
			} 
			return {flag:flag, errors:errors, symptom:htmlEscapeCharacter(symptom)};
		}

		//判斷是否有輸入文章內容
		function htmlJudge(){
			var flag;
			var errors = "";
			var html = $('div#froala-editor').froalaEditor('html.get', true);	//取得文章內容
			
			//是否已經輸入文章內容
			if(html == ""){
				errors = "請輸入文章內容";
				flag = false;
			}
			else{flag = true;}

			return {flag:flag, errors:errors, html:html};
		}

		/********************************************************************************************/

		//刪除資料夾
		function deleteFolder(patientInstructionID){
			$.ajax({
				url : froalaUrl,
				method: "POST",
				data : {
					state : "deleteFolder",
					patientInstructionID : patientInstructionID
				},
			});	
		}
		
		//取得圖片位置
		function getImgSrc(content, srcImgVideo){
			var pattern = /<img.*?src=[\"'](.+?)[\"'].*?>/g; 
			var matches = content.match(pattern); 
			if(matches){
				for (var i=0; i<matches.length; i++){
					var temp = matches[i].match(pattern);
					var str = RegExp.$1;
					str = str.replace(/\//g, "\\");	//替換
					srcImgVideo[srcImgVideo.length] = str;	//放入陣列最後面
				}
			}
		}
		//取得影片位置
		function getVideoSrc(content, srcImgVideo){
			var pattern = /<video.*?src=[\"'](.+?)[\"'].*?>/g; 
			var matches = content.match(pattern); 
			if(matches){
				for (var i=0; i<matches.length; i++){
					var temp = matches[i].match(pattern);
					var str = RegExp.$1;
					str = str.replace(/\//g, "\\");	//替換
					srcImgVideo[srcImgVideo.length] = str;	//放入陣列最後面
				}
			}
		}
		//刪除不應該在資料夾裡的影片或照片
		function deleteNotInFolder(patientInstructionID, srcImgVideo){
			$.ajax({
				url : froalaUrl,
				method: "POST",
				data : {
					state : "deleteNotInFolder",
					patientInstructionID : patientInstructionID,
					srcImgVideo : srcImgVideo
				},
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
		
		/********************************************************************************************/

		//檢查InstructionID
		function checkInstructionID(patientInstructionID){
			$.ajax({
				url : url,
				data : {
					state : "checkInstructionID",
					patientInstructionID : patientInstructionID
				},
				dataType : "json",
				success : function(response) {
					if(response){
						modalGenerator("警告", "網址錯誤，衛教資訊文章不存在");
						setTimeout(function(){
							window.location.href = 'PatientInstruction.html';
						},1500);
					}
				},
				error : function() {console.log("錯誤訊息");}
			});
		}
		
		//是否修改檢查//froala在各自頁面檢查即可
		function checkChanged(){
			$("input[type='checkbox']").change(function() {				//偵測頁面上任一個checkbox有沒有被修改
				changed = true;
			});
			$("input[type='text'].editInput").change(function() {		//偵測頁面上任一個文字輸入欄位有沒有被修改
				changed = true;
			});
			$(".editSelect").change(function () {						//偵測頁面上任一個下拉式選單有沒有被修改
				changed = true;
			});	
			
		}