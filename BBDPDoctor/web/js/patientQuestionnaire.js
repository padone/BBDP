$(document).ready(function(){
	$.ajax({
		url : "PatientQuestionnaireServlet",
		data : {
			state : "searchPatientQuestionnaireType",
		},
		dataType : "json",

		success : function(response) {
			var temp="<option value=''>問卷分類</option>";
			for(var i=0;i<response.length;i++){
				temp+="<option value='"+response[i]+"'>"+response[i]+"</option>";
			}	
			temp+="</select>";
			$("#questionnaireType").empty().append(temp);
			getQuestionnaireList();
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});
	
	$('#searchQuestionnaire').change(function(){
		$.ajax({
			url : "PatientQuestionnaireServlet",
			data : {
				state : "selectQuestionnaire",
				patientSelect : $('#searchQuestionnaire option:selected').val()
			},
			dataType : "json",
			
			success : function(response) {
				var temp="<option value=''>問卷分類</option>";
				for(var i=0;i<response.length;i++){
					temp+="<option value='"+response[i]+"'>"+response[i]+"</option>";
				}	
				temp+="</select>";
				$("#questionnaireType").empty().append(temp);
				$("#questionnaireDateRange").val("");
				$("#questionnaireDate").empty().append("<option value=''>所有時間</option>");
				getQuestionnaireList();
			},
			error : function() {
				console.log("錯誤訊息");
			}
		});
	});
	$('#questionnaireType').change(function(){
		$("#questionnaireDateRange").val("");
		$("#questionnaireDate").empty().append("<option value=''>所有時間</option>");
		getQuestionnaireList();
	});
	$('#questionnaireDateRange').change(function(){
		if($('#questionnaireDateRange option:selected').val()){
			$.ajax({
				url : "PatientQuestionnaireServlet",
				data : {
					state : "selectQuestionnaireDate",
					patientSelect : $('#searchQuestionnaire option:selected').val(),
					type : htmlEscapeCharacter($('#questionnaireType option:selected').val()),
					dateRange : $('#questionnaireDateRange option:selected').val()
					
				},
				dataType : "json",
				
				success : function(response) {
					var temp="<option value=''>所有時間</option>";
					for(var i=0;i<response.length;i++){
						temp+="<option value='"+response[i]+"'>"+response[i]+"</option>";
					}	
					temp+="</select>";
					$("#questionnaireDate").empty().append(temp);
				},
				error : function() {
					console.log("錯誤訊息");
				}
			});
		}else{
			$("#questionnaireDate").empty().append("<option value=''>所有時間</option>");	
		}
	});	
	$('#questionnaireDate').change(function(){
		getQuestionnaireList();
	});
});

function getQuestionnaireList(){
	$.ajax({
		url : "PatientQuestionnaireServlet",
		data : {
			state : "getQuestionnaireList",
			patientSelect : $('#searchQuestionnaire option:selected').val(),
			type : htmlEscapeCharacter($('#questionnaireType option:selected').val()),
			dateRange : $('#questionnaireDateRange option:selected').val(),
			date : $('#questionnaireDate option:selected').val()
			
		},
		dataType : "json",
		
		success : function(response) {
			var temp = "";
			for(var i=0; i<response.length; i+=5){
				temp += '<a href="EditPatientQuestionnaire.html?'+response[i+2]+'" class="list-group-item left-list-item '+response[i]+'">';
				temp += '<h4 class="list-group-item-heading" style="white-space: nowrap;text-overflow: ellipsis;width: '+$("#questionnaireList").width()*0.9+'px;display: block;overflow: hidden;">'+returnEscapeCharacter(response[i+3])+'</h4>';
				if($('#searchQuestionnaire option:selected').val()=="not"){
					temp += '<span class="list-group-item-text">'+response[i+4].substr(0,10);+'</span>';
				}else{
					temp += '<span class="list-group-item-text">'+response[i+4].substr(0,16);+'</span>';
				}
				temp += '<span class="list-group-item-text pull-right">'+response[i+1]+'</span>';
				temp += '</a>';
			}
			$("#questionnaireList").empty().append(temp);	
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});	
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
