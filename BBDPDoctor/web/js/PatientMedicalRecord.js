$(document).ready(function(){
	$.ajax({
		url : "PatientMedicalRecordServlet",
		data : {
			state : "getMedicalRecordList",
			dateRange : $('#medicalRecordDateRange option:selected').val(),
			date : $('#medicalRecordDate option:selected').val()
			
		},
		dataType : "json",
		
		success : function(response) {
			var temp = "";
			for(var i=0; i<response.length; i+=2){
				temp += '<a href="EditPatientMedicalRecord.html?num='+response[i]+'" class="list-group-item left-list-item medicalRecord">';
				temp += '<h4 class="list-group-item-heading">'+response[i+1].substr(0,16)+'</h4></a>';
			}
			$("#medicalRecordList").empty().append(temp);	
		},
		error : function() {
			console.log("錯誤訊息");
		}
	});	
	
	$('#medicalRecordDateRange').change(function(){
		if($('#medicalRecordDateRange option:selected').val()){
			$.ajax({
				url : "PatientMedicalRecordServlet",
				data : {
					state : "selectMedicalRecordDate",
					dateRange : $('#medicalRecordDateRange option:selected').val()
					
				},
				dataType : "json",
				
				success : function(response) {
					var temp="<option value=''>所有時間</option>";
					for(var i=0;i<response.length;i++){
						temp+="<option value='"+response[i]+"'>"+response[i]+"</option>";
					}	
					temp+="</select>";
					$("#medicalRecordDate").empty().append(temp);
				},
				error : function() {
					console.log("錯誤訊息");
				}
			});
		}else{
			$("#medicalRecordDate").empty().append("<option value=''>所有時間</option>");	
		}
	});	
	$('#medicalRecordDate').change(function(){
		getMedicalRecordList();
	});
});

function getMedicalRecordList(){
	$.ajax({
		url : "PatientMedicalRecordServlet",
		data : {
			state : "getMedicalRecordList",
			dateRange : $('#medicalRecordDateRange option:selected').val(),
			date : $('#medicalRecordDate option:selected').val()
			
		},
		dataType : "json",
		
		success : function(response) {
			var temp = "";
			for(var i=0; i<response.length; i+=2){
				temp += '<a href="EditPatientMedicalRecord.html?num='+response[i]+'" class="list-group-item left-list-item medicalRecord">';
				temp += '<h4 class="list-group-item-heading">'+response[i+1].substr(0,16)+'</h4></a>';
			}
			$("#medicalRecordList").empty().append(temp);	
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
