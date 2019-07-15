$(document).ready(function(){
	$.getScript("js/webtoolkit.base64.js");
	$("body").append(
		"<div id='searchModal' class='modal fade' role='dialog'>" +
			"<div class='modal-dialog modal-sm'>" +
				"<div class='modal-content'>" +
					"<div class='modal-header'>" +
						"<button type='button' class='close' data-dismiss='modal'>&times;</button>"+
						"<h4 class='modal-title' id='searchModalLabel'>搜尋結果</h4>" +
					"</div>" +
					"<div class='modal-body'>" +
						"<p id='patientSearchContent'></p>"+
					"</div>" +
				"</div>" +
			"</div>" +
		"</div>"
	);
});


function searchPatientID(){
	$("#searchModal").modal("show");
}

var selecrPatientID = [];

$(document).ready(function() {
	//modal出現
	$("#searchModal").on("show.bs.modal", function() {
		$("#patientSearchContent").empty();

		if ($('#searchPatientID').val().length != 5|| isNaN($('#searchPatientID').val())){
			$("#patientSearchContent").append("請輸入病患身分證字號後5碼");
		}
		else{
			$.ajax({
				type: "POST",
				url: "PatientSearchServlet",
				data: {option : "search", account: $('#searchPatientID').val()},
				dataType: "text",
						
				success : function(response){
                    
                    if(response == "fail"){
                        $("#patientSearchContent").append("查無病患資料");
                    }
                    else if(response == "SQLException"){
                        $("#patientSearchContent").append("Server沒有回應");
                    }
					else{
                        var decodeString = Base64.decode(response);     //解碼 decode the string                   
                        var json = JSON.parse(decodeString);
                        
						for(var i = 0; i<json.length; i++){	
							selecrPatientID[i] = json[i]["patientID"]
							item = "<a class='list-group-item' onclick='searchConfirm("+i+")'>" + 
			        				"<h4 class='list-group-item-heading'>" + 
			        					json[i]["name"] + 
			        				"</h4>" + 
			        				"<p class='list-group-item-text'>" +
			        					json[i]["account"] + 
			        				"</p>" + 
			        				"</a>";
							$("#patientSearchContent").append(item);
						}
					}			

				},

				error : function(){
					//alert("Server沒有回應");
					console.log("Server沒有回應");
				}
			}); 
		}
	  	  
	  });
	
});

function searchConfirm(i) {
	//alert(selecrPatientID[i]);
	
	$.ajax({
		type: "POST",	
		url: "PatientSearchServlet",
		data: {option : "select", selectPatient : selecrPatientID[i]},
		success : function(response){
			window.location.href='PatientBasicInformation.html';
		},

		error : function(){
			//alert("server沒有回應");
		}
	}); 
	
}
