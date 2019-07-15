$(document).ready(function(){
	$.ajax({
		url : "QuestionnairePoolServlet",
		data : {
			state : "searchType"								
		},
		dataType : "json",

		success : function(response) {
			$("#questionnairePoolType").empty();
			var temp = "<option value='all'>問卷題目分類</option>";
			for(var i=0; i<response.length; i++){
				temp += "<option value='"+response[i]+"'>"+response[i]+"</option>";
			}
			$("#questionnairePoolType").append(temp);	
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
	$.ajax({
		url : "QuestionnairePoolServlet",
		data : {
			state : "searchAllQuestion"							
		},
		dataType : "json",

		success : function(response) {
			$("#questionList").empty();
			var temp ="";		
			for(var i=0; i<response.length; i+=3){
				tempStr = removeHTML(response[i+1]);			
				temp += "<a href='EditQuestionnairePool.html?num="+response[i]+"' class='list-group-item left-list-item questionnaireGreen'><h4 class='list-group-item-heading'style='white-space: nowrap;text-overflow: ellipsis;width: "+$("#questionList").width()*0.9+"px;display: block;overflow: hidden;'>"+tempStr+"</h4><p class='list-group-item-text' style='white-space: nowrap;text-overflow: ellipsis;width: "+$("#questionList").width()*0.9+"px;display: block;overflow: hidden;'>"+response[i+2]+"</p></div><div class='clearfix'></div></a>";
			}
			$("#questionList").append(temp);	
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
	
});

function changeQuestionnairePoolType(){
	$("#changeCheckButton").empty().append('<button type="button" class="btn btn-default" onclick="checkTheBox()">勾選題目</button>');
	var state = "searchQuestion";
	if($('#questionnairePoolType option:selected').val()=="all") state = "searchAllQuestion"
	var type = htmlEscapeCharacter($('#questionnairePoolType option:selected').val());
	$.ajax({
		url : "QuestionnairePoolServlet",
		data : {
			state : state,
			type : type
		},
		dataType : "json",

		success : function(response) {
			$("#questionList").empty();
			var temp ="";		
			for(var i=0; i<response.length; i+=3){
				tempStr = removeHTML(response[i+1]);
				temp += "<a href='EditQuestionnairePool.html?num="+response[i]+"' class='list-group-item left-list-item questionnaireGreen'><h4 class='list-group-item-heading'style='white-space: nowrap;text-overflow: ellipsis;width: "+$("#questionList").width()*0.9+"px;display: block;overflow: hidden;'>"+tempStr+"</h4><p class='list-group-item-text' style='white-space: nowrap;text-overflow: ellipsis;width: "+$("#questionList").width()*0.9+"px;display: block;overflow: hidden;'>"+response[i+2]+"</p></div><div class='clearfix'></div></a>";
			}
			$("#questionList").append(temp);	
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
}

//移除html
function removeHTML(strText){
 
    var regEx = /<[^>]*>/g;
 
    return strText.replace(regEx, "");
 
}

function checkTheBox(){
	var state = "searchQuestion";
	if($('#questionnairePoolType option:selected').val()=="all") state = "searchAllQuestion"
	var type = htmlEscapeCharacter($('#questionnairePoolType option:selected').val());
	$.ajax({
		url : "QuestionnairePoolServlet",
		data : {
			state : state,
			type : type
		},
		dataType : "json",

		success : function(response) {
			$("#questionList").empty();
			var temp ="";		
			for(var i=0; i<response.length; i+=3){
				tempStr = removeHTML(response[i+1]);
				temp += "<a class='list-group-item left-list-item questionnaireGreen' onclick='checkQuestion("+response[i]+")' style='cursor: pointer;'><div class='checkbox pull-left'><label><input type='checkbox' name='questionCheckbox' value='"+response[i]+"' /></label></div><div class='pull-left form-control-inline'><h4 class='list-group-item-heading'style='white-space: nowrap;text-overflow: ellipsis;width: "+$("#questionList").width()*0.8+"px;display: block;overflow: hidden;'>"+tempStr+"</h4><p class='list-group-item-text' style='white-space: nowrap;text-overflow: ellipsis;width: "+$("#questionList").width()*0.8+"px;display: block;overflow: hidden;'>"+response[i+2]+"</p></div><div class='clearfix'></div></a>";
			}
			$("#questionList").append(temp);	
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});	
	$("#changeCheckButton").empty().append('<button type="button" class="btn btn-default" onclick="addTempStorage()">將勾選題目加入暫存區</button>');	
}
function checkQuestion(num){
	if($('input:checkbox[name="questionCheckbox"][value="'+num+'"]:checked').length){
		$('input:checkbox[name="questionCheckbox"][value="'+num+'"]').prop('checked',false);
	}else{ 
		$('input:checkbox[name="questionCheckbox"][value="'+num+'"]').prop("checked", true);
	}
}
function addTempStorage(){
	var num = 0;
	var tempArray = "";
	$('input:checkbox:checked[name="questionCheckbox"]').each(function(i) { num+=1; tempArray += (this.value+",");});
	if(tempArray=="")
		modalGenerator("提示", "請勾選題目");
	else{
		$.ajax({
			url : "QuestionnairePoolServlet",
			data : {
				state : "addTempStorage",
				questionArray : tempArray
			},
			traditional: true,
			success : function(response) {
				if(num == response)
					modalGenerator("提示", "成功加入題目暫存區");
				else
					modalGenerator("提示", "勾選的題目已有"+(num-response)+"道在暫存區內，成功再加入"+parseInt(response)+"道題目");
				$('input[name^="questionCheckbox"]').prop('checked', false);
				changeQuestionnairePoolType();				
			},
			error : function() {
				console.log("錯誤訊息");
			}
		});
	}

}

function changeJson(){
	var tempString="";
	for(var i=1; i<=score; i++){
		j = i+500;
		if($('#'+j).val()){
			tempString+="{";
			tempString+='"score":';
			if($('#'+i).val())
				tempString += htmlEscapeCharacter($('#'+i).val())+',';
			else
				tempString += '0,';
			tempString+='"content":';
			tempString += '"'+htmlEscapeCharacter($('#'+j).val())+'"}';
			tempString +=',';
		}
	}
	if (tempString != ""){
		var reg = /,$/gi;
		tempString = tempString.replace(reg,"");
		tempString = "[" + tempString + "]";
	}
	return tempString;
}
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

function changeAnswer(){
	changeButton();
	$('#optionArea').empty();
	score=0;
	content=500;
	answerNumber = 0;
}
function addYesOrNo(){
	$('#optionArea').empty();
	score=0;
	content=500;
	answerNumber = 0;
	addAnswer(1);
	$("#"+score).val("1");
	$("#"+content).val("是");
	addAnswer(1);
	$("#"+score).val("0");
	$("#"+content).val("否");
}	
function changeButton(){
	if($("#answerType").val() == "單選題"){
		$("#addYesOrNoButton").show();
		
	}else{
		$("#addYesOrNoButton").hide();		
	}		
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
function addAnswer(num){
	if($('#answerType option:selected').val()=="單選題"){
		if(answerNumber<10){
			score+=1;
			content+=1;
			answerNumber+=1;
			if(num){
				$("#optionArea").append('<div class="row" style="margin-top: 1vh;" id = "div'+score+'"><div class="col-md-2 col-sm-2 col-xs-3" style="padding-right: 0;"><input type="number" class="form-control" placeholder="分數" id="'+score+'" /></div><div class="col-md-7 col-sm-7 col-xs-5" style="padding-right: 0;"><input type="text" class="form-control editInput" id="'+content+'" placeholder="請輸入選項..." /></div><div class="col-md-2 col-sm-2 col-xs-2"><button type="button" class="btn btn-default" onclick="deleteAnswer('+score+')">刪除</button></div></div>');
			}else{
				//沒有刪除
				$("#optionArea").append('<div class="row" style="margin-top: 1vh;" id = "div'+score+'"><div class="col-md-2 col-sm-2 col-xs-3" style="padding-right: 0;"><input type="number" class="form-control" placeholder="分數" id="'+score+'" /></div><div class="col-md-7 col-sm-7 col-xs-5" style="padding-right: 0;"><input type="text" class="form-control editInput" id="'+content+'" placeholder="請輸入選項..." /></div><div class="col-md-2 col-sm-2 col-xs-2"></div></div>');				
			} 
		}else
			modalGenerator("提示", "最多10個選項");
		
	}else
		modalGenerator("提示", "請切換題型");
	
	$("input[type='text'].editInput").change(function() {		//偵測頁面上任一個文字輸入欄位有沒有被修改
		changed = true;
	});
	$("input[type='number']").change(function() {		//偵測頁面上任一個文字輸入欄位有沒有被修改
		changed = true;
	});
}
function changeHtmlEditor(){
	$("#phoneView").empty();
	$("#phoneView").append($('div#froala-editor').froalaEditor('html.get', true));
	$("#phoneView img").css("height","auto");
	$("#phoneView iframe").css("height","auto");

}

function changeModalSize(){
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
}
$(document).on('click', '.remove', function(e) { 
//刪除排序Part
	$(this).closest($(this)).parent().parent().remove();
});
