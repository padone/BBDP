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
					
					//圖表類型
					var chartType = response.chart;
					$("#chartType").val(response.chart);
					
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
				    console.log("response.dateStart");
				    $("#dateEnd").val(today);

					//時間
					var timeString = ['x'];
					for(var number=0; number<response.itemTimeList.length; number++){
						var itemTime = response.itemTimeList[number].substring(0, 16);
						timeString.push(itemTime);
					}
					//console.log("timeString : "+timeString);
					column.push(timeString);
					
					//細項
					for(var i=0; i<response.detailNameList.length; i++){
						var value = [];
						value.push(returnEscapeCharacter(response.detailNameList[i]));
						for(var j=0; j<response.detailValueList.length; j++){		//valueList 第一個集合
							var tempList = response.detailValueList[j];
							//集合中第幾個
							value.push(tempList[i]);	//要取得跟name一樣的位置，所以要用i，不能用j
						}
						column.push(value);
					}
					
					//console.log("column : "+column);					
					
					//細項name、id
					$("#detailCheckbox").empty();
					for(var number=0; number<response.detailIDList.length; number++){
						var detailID = response.detailIDList[number];		//detailID
						var detailName = returnEscapeCharacter(response.detailNameList[number]);	//detailName
						/*$("#detailCheckbox").append(
											"<label class='checkbox-inline' for='detail"+detailID+"'>"+
											"	<input class='' type='checkbox' value='"+detailID+"' name='detail' id='detail"+detailID+"' style='width:16px;height:16px;display:inline-block;vertical-align:middle;' onClick='changeChart()' checked>"+
											"		<span style='display:inline-block;vertical-align:middle;font-size:16px;font-weight:bold;'>"+detailName+"&nbsp;&nbsp;</span>"+
											"</label>");*/
						$("#detailCheckbox").append(					
											"<div class='checkbox-inline'>"+
											"	<label><input type='checkbox' value='"+detailID+"' name='detail' id='detail"+detailID+"' onClick='changeChart()' checked>"+detailName+"</label>"+
											"</div>");
					}
									
					//////////////////////////////////////////////c3.js//////////////////////////////////
					//計算外框高度(要保留)
					var height = $("#page-wrapper > .row > div > div.row:last > .panel > .panel-body").outerHeight();
					//產生一個空的圖表
					var chart = c3.generate({
						size: {
							height: height		//(要保留)
						},
						bindto: '#chart',
						data: {
					        x: 'x',
					        xFormat: '%Y-%m-%d %H:%M',
					        columns: []
					    },
					    axis: {
					        x: {
					        	label: '日期',
					        	type: 'timeseries',
					            tick: {
					            	format: '%Y-%m-%d %H:%M'
					            }
					        }
					    },
					    zoom: {
							enabled: true
						}
					});
					
					/*換成預設的圖表類型*/
					if(chartType != "timeseries"){
						setTimeout(function () {
						    chart.transform(chartType);
						}, 0000);
					}
					
					//先產生日期、第一筆資料
					setTimeout(function () {
						chart.load({
							columns: [column[0],column[1]]
						});
					}, 1000);
					
					//顯示其他資料
					for(var index=2; index<column.length; index++){
						otherchart(index);
					}
					//顯示其他資料
					function otherchart(index){
						var time = index*1000;
						setTimeout(function () {
							chart.load({
								columns: [column[index]]
							});
						}, time);
					}
					
					//前面特效結束後才會做
					var showChart = 1000*column.length;
					setTimeout(function () {
						chart.unload({});	//卸載
					    chartFlow();		//滑動
					}, showChart);
					
					//滑動
					var duration = 3000;
					function chartFlow(){
						var len = column[0].length - 3;	//要-3才行
						//一筆資料
						if(len < 0){
							chart.unload({});	//卸載
							chart.load({		//載入全部
								columns: column
							});
						}
						//兩筆(含)資料以上讓他可滑動
						else{
							chart.flow({
								columns: column,
								length: len,
								duration: duration,
								done: function () {
									chart.unload({});	//卸載
									chart.load({		//載入全部
										columns: column
									});
								},
							});
						}
					}			
				},
				error : function() {
					console.log("錯誤訊息");
				}
			});		    
		});
		
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
			 changeChart();
		}
		
		//改變圖表///////////////////////////////////////////////////////////////////////////////////////////////////////////
		function changeChart(){
			//取得選取的時間
			 var dateStart = $('#dateStart').val();
			 var dateEnd = $('#dateEnd').val();
			 
			 //取得勾選的detail id
			 var checkArray = new Array();
			 $('input:checkbox:checked[name="detail"]').each(function(i) { checkArray[i] = this.value; });
			 
			 //變化圖表//////////////////////////////////////////////////////////////////////////////////////////////////////
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
						//時間
						var column = [];
						var timeString = ['x'];
						for(var number=0; number<response.itemTimeList.length; number++){
							var itemTime = response.itemTimeList[number].substring(0, 16);
							timeString.push(itemTime);
						}
						//console.log("timeString : "+timeString);
						column.push(timeString);
						
						//細項
						for(var i=0; i<response.detailIDList.length; i++){
							for(var k=0; k<checkArray.length; k++){
								if(checkArray[k] == response.detailIDList[i]){			//如果有勾選此細項的話，放入
									var value = [];
									value.push(returnEscapeCharacter(response.detailNameList[i]));
									for(var j=0; j<response.detailValueList.length; j++){		//valueList 第一個集合
										var tempList = response.detailValueList[j];
										//集合中第幾個
										value.push(tempList[i]);	//要取得跟name一樣的位置，所以要用i，不能用j
									}
									//console.log("value : "+value);
									column.push(value);
								}
							}
						}
						//console.log("column : "+column);
						//c3.js chart
						var chart = c3.generate({
							bindto: '#chart',
							data: {
								x: 'x',
								xFormat: '%Y-%m-%d %H:%M',
								columns: column
							},
							axis: {
								x: {
									label: '日期',
									type: 'timeseries',
									tick: {
										format: '%Y-%m-%d %H:%M'
									}
								}
							},
							zoom: {
								enabled: true
							}
						});
						
						var chartType = $("#chartType :selected").val();	//圖表類型
						chartType = chartType.toString();	//轉成字串才c3 transform可讀
						//變換圖表類型
						if(chartType != "timeseries"){
							setTimeout(function () {
							    chart.transform(chartType);
							}, 0000);
						}
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

		//導向單筆資料///////////////////////////////////////////////////////////////////////////////////////////////////////
		function goToData(){
			window.location.href = "EditPatientHealthTrackingData.html?itemID=" + itemID;
		}