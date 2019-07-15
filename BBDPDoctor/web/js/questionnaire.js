$(document).ready(function(){
	$.ajax({
		url : "QuestionnaireModuleServlet",
		data : {
			state : "searchType",								
		},
		dataType : "json",

		success : function(response) {
			$("#questionnaireType").empty();
			var temp = "<option value='all'>問卷模板分類</option>";
			for(var i=0; i<response.length; i++){
				temp+="<option value='"+response[i]+"'>"+response[i]+"</option>";
			}
			$("#questionnaireType").append(temp);	
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
	
	$.ajax({
		url : "QuestionnaireModuleServlet",
		data : {
			state : "searchAllQuestionnaire",
		},
		dataType : "json",

		success : function(response) {
			$("#questionnaireList").empty();
			var temp ="";		
			for(var i=0; i<response.length; i+=3){
				temp += "<a href='EditQuestionnaireModule.html?num="+response[i]+"'  class='list-group-item left-list-item questionnaireGreen'><h4 class='list-group-item-heading' style='white-space: nowrap;text-overflow: ellipsis;width: "+$("#questionnaireList").width()*0.9+"px;display: block;overflow: hidden;'>"+response[i+1]+"</h4><p class='list-group-item-text' style='white-space: nowrap;text-overflow: ellipsis;width: "+$("#questionnaireList").width()*0.9+"px;display: block;overflow: hidden;'>"+response[i+2]+"</p></a>";
			}
			$("#questionnaireList").append(temp);			
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});	

});

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

function changeQuestionnaireType(){
	var state = "searchQuestionnaire";
	if($('#questionnaireType option:selected').val()=="all") state = "searchAllQuestionnaire";
	var type = htmlEscapeCharacter($('#questionnaireType option:selected').val());
	$.ajax({
		url : "QuestionnaireModuleServlet",
		data : {
			state : state,
			type : type
		},
		dataType : "json",

		success : function(response) {
			$("#questionnaireList").empty();
			var temp ="";		
			for(var i=0; i<response.length; i+=3){
				temp += "<a href='EditQuestionnaireModule.html?num="+response[i]+"'  class='list-group-item left-list-item questionnaireGreen'><h4 class='list-group-item-heading' style='white-space: nowrap;text-overflow: ellipsis;width: "+$("#questionnaireList").width()*0.9+"px;display: block;overflow: hidden;'>"+response[i+1]+"</h4><p class='list-group-item-text' style='white-space: nowrap;text-overflow: ellipsis;width: "+$("#questionnaireList").width()*0.9+"px;display: block;overflow: hidden;'>"+response[i+2]+"</p></a>";
			}
			$("#questionnaireList").append(temp);	
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});	
}
			
function changeType(){
	if($('#selectType option:selected').val()=="other"){
		document.getElementById('inputType').disabled=false;
		nowType = 1;
	}else{
		nowType = 0;
		$("#inputType").prop("value", "");
		document.getElementById('inputType').disabled=true;
	}
}

function changeSymptom(){
	if($('#selectSymptom option:selected').val()=="other"){
		document.getElementById('inputSymptom').disabled=false;
		nowSymptom = 1;
	}else{
		nowSymptom = 0;
		$("#inputSymptom").prop("value", "");
		document.getElementById('inputSymptom').disabled=true;
	}
}

function complete(){
//編輯題目完成
	var questionList = $("#sortable").sortable('serialize');
	var part = 0;
	partArray.length = 0;
	partName.length = 0;
	var tempQuestion = "";
	partArray[part] = "";
	partName[part] = "";
	
	var sameQuestion = [];
	var samePart = 0;
	var one = 1;
	if(questionList){
		var temp = questionList.split("&");
		questionList = "";
		for(var i=0;i<temp.length;i++){
			questionList += temp[i];
		}
		temp = questionList.split("sort[]=");

		for(var i=1; i<temp.length; i++){
			if($('#part'+temp[i]).attr("type")=="text"){
				if(part > 0 && partArray[part] ==""){
					part-= 1;
				}
				part += 1;
				if((one && i>1 )|| i == temp.length-1 ){
					samePart = 1;
				}							
				one = 1;
				partArray[part] = "";
				partName[part] = htmlEscapeCharacter($('#part'+temp[i]).val());
				tempQuestion += "<div class='panel panel-default' style='border: 1px solid #D2F898;'><div class='panel-heading' style='background-color: #D2F898;'>Part "+part+"："+$('#part'+temp[i]).val()+"</div></div>";									
			}else{
				if(!one) {partArray[part] += ",";}
				one = 0;
				sameQuestion.push(sortCountArray[temp[i]]);
				partArray[part] += sortCountArray[temp[i]];
				tempQuestion += "<div class='panel panel-default' style='border: 1px solid #D2F898;'>";
				tempQuestion += "<div class='panel-heading' style='background-color: #D2F898;'>"+$('#question'+temp[i]).html()+"</div>";
				tempQuestion += "<div class='panel-body'>"+$('#option'+temp[i]).html()+"</div></div>";
				
			}
		}
		if(partArray[part] ==""){
			partArray.pop();
			partName.pop();
		}
		if(isRepeat(sameQuestion)){
			partArray.length = 0;
			partName.length = 0;
			$('#completeQuestionnaire').empty();
			modalGenerator("提示", "題目重複");	 
		}else if(samePart){
			partArray.length = 0;
			partName.length = 0;
			$('#completeQuestionnaire').empty();
			modalGenerator("提示", "題目排列格式錯誤");					
		}else{
			$('#completeQuestionnaire').empty();
			$('#completeQuestionnaire').append(tempQuestion);
		}	
	}else{
		$('#completeQuestionnaire').empty();
	}
}
function isRepeat(arr){
	var hash = {};
	for(var i in arr) {
		if(hash[arr[i]])
		return true;
		hash[arr[i]] = true;
	}
	return false;
}
function addTempStorageQuestion(){
	var temp;
	$.ajax({
		url : "QuestionnaireModuleServlet",
		data : {
			state : "addTempStorageQuestion"
		},
		dataType : "json",					
		success : function(response) {
			if(response.length == 0) modalGenerator("提示", "暫存區內沒有題目");
			for(var i=0;i<response.length;i+=3){
				sortCount+=1;
				sortCountArray[sortCount] = response[i];
				temp = "<li id = 'sort_"+sortCount+"' style='margin-left:-3em;'>";
				temp += "<div class='panel panel-default' style='border: 1px solid #D2F898;'>";
				temp += "<button type='button' class='btn btn-default remove'style='background-color:#f0f0f0;float:right'>刪除</button>"
				temp += "<div class='panel-heading can_darg myMOUSE' style='background-color: #D2F898;' id = 'question"+sortCount+"'>"+response[i+1];
				temp += "</div>";		
				temp += "<div class='panel-body' id = 'option"+sortCount+"'>";
				if(response[i+2]){
					temp += "<form>";
					var obj = eval('(' + response[i+2] + ')');						
					for (var j = 0; j < obj.length; j++) {
						temp += "<div class='radio'><label><input type='radio' disabled>"+obj[j].score+" = "+obj[j].content+"</label></div>";
					}
					temp += "</form>";							
				}else{
					temp += "簡答題";
				}
				temp += "</div></div>";
				temp += "</li>";
				 $('.questionSortArea').append(temp);
			
			}
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});	
}
function addPart(){
	sortCount+=1;
	//插入到最後
	$('.questionSortArea').append("<li id = 'sort_"+sortCount+"' style='margin-left:-3em;'><div><i class='fa fa-bars can_darg myMOUSE' style='color:#666666;font-size:3vh;display:inline;vertical-align:middle;'></i>&nbsp;<input type='text' id='part"+sortCount+"' class='form-control' style='width:40%;display:inline;vertical-align:middle;' placeholder='Part名稱'>&nbsp;<button type='button' class='btn btn-default remove'  style='display:inline;vertical-align:middle;'>刪除</button><div><div style='height:1vh'></div></li>"); 					
}
$(document).on('click', '.remove', function(e) { 
//刪除排序
	$(this).closest($(this)).parent().parent().remove();
});
$(document).on('click', '.removeQuestion', function(e) { 
//刪除病歷題目
	document.getElementById('addQuestionButton').disabled=false;
	$(this).closest($(this)).parent().parent().remove();
}); 
$(document).on('click', '.removeSelfDescription', function(e) { 
//刪除病患自述
	document.getElementById('addSelfDescriptionButton').disabled=false;
	$(this).closest($(this)).parent().parent().remove();
}); 	