		//改網址用的
		var url = "PatientHealthTrackingServlet";
		
		var urlall = window.location.href;
		var urlparts = urlall.split("?");
		var IDparts = urlparts[1].split("=");
		var itemID = IDparts[1];
			
		var doctorID = window.localStorage.getItem('login');
		
		/*取得上個月的日期*/
        function getBeforeMonth(date) { 
            var arr = date.split('-'); 
            var year = arr[0]; 	//獲取當前日期的年份
            var month = arr[1]; //獲取當前日期的月份 
            var day = arr[2]; //獲取當前日期的日 
            var days = new Date(year, month, 0); 
            days = days.getDate(); //獲取當前日期的月的天數
            var year2 = year; 
            var month2 = parseInt(month) - 1; 
            if (month2 == 0) { 
                year2 = parseInt(year2) - 1; 
                month2 = 12; 
            } 
            var day2 = day; 
            var days2 = new Date(year2, month2, 0); 
            days2 = days2.getDate(); 
            if (day2 > days2) { 
                day2 = days2; 
            } 
            if (month2 < 10) { 
                month2 = '0' + month2; 
            } 
            var t2 = year2 + '-' + month2 + '-' + day2; 
            return t2; 
        } 
		
		//預設輸入為今天日期////////////////////////////////////////////////////////////////////////////////////////////////////
		$(document).ready( function() {
			checkItemID(itemID);	//檢查itemID
		    
		    var column = [];
		    //取得該項目一些基本資料/////////////////////////////////////////////////////////////////////////////////////////////
		    $.ajax({
		    	url : url,
				data : {
					state : "itemAllDetail",
					doctorID : doctorID,
					itemID : itemID
				},
				dataType : "json",

				success : function(response) {		
					//項目名稱
					var itemName = returnEscapeCharacter(response.itemName);
					$("#itemName").empty();
					$("#itemName").append("<b>"+itemName+"</b>");
					
					//日期
					var now = new Date();
				    var month = (now.getMonth() + 1);
				    var day = now.getDate();
				    if(month < 10)
				        month = "0" + month;
				    if(day < 10) 
				        day = "0" + day;
				    var today = now.getFullYear() + '-' + month + '-' + day;
				    //var dateStart = getBeforeMonth(today);
				    $("#dateStart").val(response.dateStart);		//取得前一個月的日期
				    $("#dateEnd").val(today);

					getData(response)	//列印資料
				},
				error : function() {
					console.log("錯誤訊息");
				}
			});		    
		});
		
		//列印資料
		function getData(response){
			$("#data").empty();

			var dataHtml = 
					"<table class='table table-bordered table-hover'>"+
					"	<thead>"+
					"		<tr>"+
					"			<th style='background-color: #F7E56E;'>日期</th>";					
			//第一行
			for(var number=0; number<response.detailNameList.length; number++){
				dataHtml += 
					"			<th style='background-color: #F7E56E;'>"+returnEscapeCharacter(response.detailNameList[number])+"</th>";
			}
			//如果有文字敘述
			if(response.selfDescription==1){
				dataHtml += 
					"			<th style='background-color: #F7E56E;'>文字敘述</th>";
			}
			dataHtml +=
					"		</tr>"+
					"	</thead>"+
					"	<tbody>"+
					"		<!-- 下面灰框右邊的單筆資料 -->";
			//其他資料//要倒過來拿資料
			for(var number=response.itemTimeList.length-1; number>=0; number--){
				dataHtml +=
					"		<tr>"+
					"			<td>"+response.itemTimeList[number]+"</td>";	//日期
				var tempList = response.detailValueList[number];
				for(var i=0; i<tempList.length; i++){
					dataHtml +=
					"			<td>"+tempList[i]+"</td>";	//細項資料值
				}
				//如果有文字敘述
				if(response.selfDescription == 1){
					dataHtml += 
						"		<td>"+returnEscapeCharacter(response.selfDescriptionValueList[number])+"</td>";
				}
				dataHtml +=
					"		</tr>";
			}
			dataHtml +=
					"	</tbody>"+
					"</table>";					
			$("#data").append(dataHtml);
		}
		
		//改變開始日期/////////////////////////////////////////////////////////////////////////////////////////////////////////
		function selectDate(){
			 var dateStart = $('#dateStart').val();
			 var dateEnd = $('#dateEnd').val();
			 
			 //限制選取範圍，開始日期不可大於結束日期，結束日期不可小於開始日期
			 $("#dateStart").attr({	
			    	"max" : dateEnd
			    });
			 $("#dateEnd").attr({
			    	"min" : dateStart
			    });
			 changeData();
		}
		
		function changeData(){
			//取得選取的時間
			 var dateStart = $('#dateStart').val();
			 var dateEnd = $('#dateEnd').val();
			 
			 $.ajax({
					url : url,
					data : {
						state : "changeChart",
						doctorID : doctorID,
						itemID : itemID,
						dateStart : dateStart,
						dateEnd : dateEnd,
					},
					dataType : "json",

					success : function(response) {		
						getData(response)	//列印資料
					},
					error : function() {
						console.log("錯誤訊息");
					}
				});
		}

		//刪除該追蹤項目///////////////////////////////////////////////////////////////////////////////////////////////////////
		function deleteHealthTracking(){
			$.ajax({
				url : url,
				data : {
					state : "deleteHealthTracking",
					doctorID : doctorID,
					itemID : itemID
				},
				dataType : "json",

				success : function(response) {		
					console.log("結果" + response.result);
					modalGenerator("結果", response.result);
					if(response.result =="健康追蹤項目刪除成功"){
						setTimeout(function(){
							window.location.href = 'PatientHealthTracking.html';
						},1500);
					}
				},
				error : function() {
					console.log("錯誤訊息");
				}
			});
		}
		
		
		//導向圖表資料///////////////////////////////////////////////////////////////////////////////////////////////////////
		function goToChart(){
			window.location.href = "EditPatientHealthTracking.html?itemID=" + itemID;
		}