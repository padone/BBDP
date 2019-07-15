var questionnaireID;	//問卷編號
var questionnaireName;	//問卷名稱
var questionnaireMR;	//問卷病歷
var questionnaireDate;	//填寫問卷日期
var selfDescription;	//病患自述
var questionnaireScoring;	//問卷計分
var answerID;	//答案編號
var answerArray = [];
var partName = [];
var partArray = [];	
var addScoring = 0;
var highest = 0;	
var medicalRecordString = "";
function getPatientMedicalRecord(){
	$.ajax({
		url : "PatientQuestionnaireServlet",
		data : {
			state : "getNewestAnswer",
		},
		dataType : "json",
		async:false,
		success : function(response){
			if(response.length>0){
				questionnaireID = response[0];
				questionnaireName = response[1];
				questionnaireScoring = response[2];
				questionnaireDate = response[3];
				selfDescription = response[4];
				answerID = response[5];
				getOptionAnswer();
				getQuestionnaire();
				cal();
				getMedicalRecord();	
			}else{
				questionnaireDate = "";
				medicalRecordString = "病患尚未填寫問卷";
			}
			
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
	return medicalRecordString;
}

function getQuestionnaireURL(){
	return "EditPatientQuestionnaire.html?num="+answerID;
}

function getDate(){
	return questionnaireDate.substr(0,16);
}

function getQuestionnaire(){
	$.ajax({
		url : "QuestionnaireModuleServlet",
		data : {
			state : "getQuestionList",
			questionnaireID : questionnaireID
		},
		dataType : "json",
		async:false,
		success : function(response) {
			partArray.length = 0;
			partArray[0] = "";
			var tempPart = parseInt(response[0]);
			partArray[tempPart] = response[1];
			for(var i=2; i<response.length; i+=2){
				if(tempPart != parseInt(response[i])){
					partArray[parseInt(response[i])] = response[i+1];
					tempPart = parseInt(response[i]);								
				}else{
					partArray[parseInt(response[i])] += ","+response[i+1];	
				}				
			}
			getPartName();
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});	
}	

function getPartName(){
	$.ajax({
		url : "QuestionnaireModuleServlet",
		data : {
			state : "getPartName",
			questionnaireID : questionnaireID,
		},
		async:false,
		dataType : "json",
		success : function(response) {
			partName.length = 0;
			partName[0] = "";
			for(var i=0; i<response.length; i++){
				partName[i] = response[i];
			}
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});			
}

function getOptionAnswer(){
	$.ajax({
		url : "PatientQuestionnaireServlet",
		data : {
			state : "getOptionAnswer",
			answerID : answerID
		},
		async:false,
		dataType : "json",
		success : function(response){
			answerArray.length = 0;
			answerArray[0] = "";
			for(var i=0; i<response.length; i++){
				answerArray[i] = response[i];
			}					
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
}

function getMedicalRecord(){
	$.ajax({
		url : "PatientQuestionnaireServlet",
		data : {
			state : "getMedicalRecord",
			questionnaireID : questionnaireID
		},
		async:false,
		dataType : "json",
		success : function(response){
			medicalRecordString = response[0]+"，"+questionnaireName + " 如下：<br>";
			var temp = response[1].split("<,>");
			for(var i=0; i<temp.length; i++){
				if(temp[i].indexOf("題目病歷<：>")!= -1){
					medicalRecordString += addQuestionMedicalRecord(temp[i].split("<：>")[1],temp[i].split("<：>")[2]=="<br>");
					if(questionnaireScoring == "1") medicalRecordString += "總分："+addScoring+"/"+highest+"分。<br>";
				}else if(temp[i] == "病患自述"){
					if(selfDescription != "") medicalRecordString += "病患自述："+selfDescription;
				}else{
					if(temp[i]) medicalRecordString += temp[i];
				}
			}
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});	
}

function addQuestionMedicalRecord(str,addBR){
	var returnString = "";
	var tempQuestionMR = "";
	var num = 0;
	for(var i=0; i<partArray.length; i++){
		var questions = partArray[i].split(",");
		for(var j=0; j<questions.length;j++){
			$.ajax({
				url : "QuestionnaireModuleServlet",
				data : {
					state : "searchQuestionMedicalRecord",
					questionID : questions[j]
				},
				async:false,
				dataType : "json",
				success : function(response) {	
					if(response[1]){	//病歷存在
						var obj = new Object();
						obj = JSON.parse(response[1]);
						if(response[0]){	//單選
							var option = eval('(' + response[0] + ')');	
							if(obj.all=="true"){	//全選
								if(obj[0]){
									var temp = obj[0]+"";
									temp = temp.split("<,>");
									for(var k=0; k<temp.length; k++){
										if(temp[k] == "病患選擇答案"){
											tempQuestionMR += option[answerArray[num]].content;		
										}else{
											tempQuestionMR += temp[k];
										}
									}
								}
							}else{
								if(obj[answerArray[num]+1]){
									var temp = obj[answerArray[num]+1]+"";
									temp = temp.split("<,>");
									for(var l=0; l<temp.length; l++){
										tempQuestionMR += temp[l];
									}
								}
							}						
						}else{	//簡答
							if(obj[0]){
								var temp = obj[0]+"";
								temp = temp.split("<,>");
								for(var k=0; k<temp.length; k++){
									if(temp[k] == "病患輸入答案"){
										tempQuestionMR += answerArray[num];		
									}else{
										tempQuestionMR += temp[k];
									}
								}
							}								
						}
						num += 1;
						if(tempQuestionMR){
							returnString += tempQuestionMR+str;
							if(addBR) returnString += "<br>";
							tempQuestionMR = "";
						}
					}							
				},
				error : function() {
				}
			});							
		}
	}
	return returnString;				
}

function cal(){
	var num = 0;
	var tempScore = 0;
	
	for(var i=0; i<partArray.length; i++){
		var questions = partArray[i].split(",");
		for(var j=0; j<questions.length;j++){
			$.ajax({
				url : "QuestionnaireModuleServlet",
				data : {
					state : "searchQuestion",
					questionID : questions[j]
				},
				async:false,
				dataType : "json",
				async:false,
				dataType : "json",
				success : function(response){
					if(response[2]){
						var obj = eval('(' + response[2] + ')');						
						for (var j = 0; j < obj.length; j++) {
							if(obj[j].score > tempScore) tempScore = obj[j].score;										
							if(j == answerArray[num]) addScoring += parseInt(obj[j].score);
						}
						highest += tempScore;
						tempScore = 0;
					}
					num += 1;
				},
				error : function() {									
				}
			});					
		}
	}	
}

//切換至新增病歷頁面
function newMedicalRecord(){
	localStorage.setItem("MRData",  $('#patientBasicMedicalRecordContent').html());
	window.location.href = 'NewPatientMedicalRecord.html';
}