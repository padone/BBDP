var url = "PatientInstructionServlet";

var editURL = window.location.href;
var urlparts = editURL.split("?");
var IDparts = urlparts[1].split("=");
var patientInstructionID = IDparts[1];

$(document).ready(function() {
	checkInstructionID(patientInstructionID);	//檢查InstructionID
	
	getInstruction(); 	//取得衛教資訊(標題、時間)
	getComment();		//取得留言資訊
});

//取得衛教資訊(標題、時間)
function getInstruction(){
	$.ajax({
		url : url,
		data : {
			state : "getInstruction",
			patientInstructionID : patientInstructionID
		},
		dataType : "json",
		success : function(response) {
			$("#title").html(returnEscapeCharacter(response.title));
			$("#date").html(response.date);
		},
		error : function() {console.log("錯誤訊息");}
	});
}

//取得留言資訊
function getComment(){
	$.ajax({
		url : url,
		data : {
			state : "getComment",
			patientInstructionID : patientInstructionID
		},
		dataType : "json",
		success : function(response) {
			for(var number=0; number<response.commentIDList.length; number++){
				var insertHtml = "";
				insertHtml = 
					"<li class='list-group-item' id='"+response.commentIDList[number]+"'>"+
					"	<div class='media'>"+
					"		<div class='media-left media-middle'>";
				if(response.hideImgList[number] == "0"){
					insertHtml += 
					"			<img src='http://140.121.197.130:8004/BBDPPatient/ProfilePictureServlet?option=getProfilePicture&patientID="+response.patientIDList[number]+"' onerror=failToLoadProfilePicture('"+response.patientIDList[number]+"'); class='media-object' id='img"+response.patientIDList[number]+"' style='width: 60px; object-fit: cover; margin-right: 1vw;' />";
				}
				else if(response.hideImgList[number] == "1"){
					insertHtml +=
					"			<img src='img/frame/user.png' class='media-object' style='width: 60px; object-fit: cover; margin-right: 1vw;' />";
				}	
				insertHtml += 
					"		</div>"+
					"		<div class='media-body'>"+
					"			<span class='list-group-item-heading' style='font-weight: bold;'>"+returnEscapeCharacter(response.nameList[number])+"</span>"+
					"			<span class='pull-right'>"+response.time_1List[number]+"</span><br>"+
					"			<p class='pull-left' style='margin-top: 1vh;'>"+returnEscapeCharacter(response.comment_1List[number])+"</p><br>"+
					"			<p class='pull-right' id='commentButton"+response.commentIDList[number]+"'>";
				if(response.time_2List[number] == null){
					insertHtml+=
					"				<span id='reply"+response.commentIDList[number]+"' class='commentButton' style='cursor: pointer; margin-right: 1vw;' onclick='replyComment(\""+response.commentIDList[number]+"\",\""+returnEscapeCharacter(response.nameList[number])+"\",\""+response.time_1List[number]+"\",\""+ returnEscapeCharacter(response.comment_1List[number])+"\",\""+ response.patientIDList[number]+"\",\""+ response.hideImgList[number]+"\")' >回覆</span>";
				}
				insertHtml+=
					"				<span id='delete"+response.commentIDList[number]+"' class='commentButton' style='cursor: pointer;' onclick=deleteComment('"+response.commentIDList[number]+"')>刪除</span>"+
					"			</p>"+
					"			<br>";
				if(response.time_2List[number] != null){
					insertHtml+=
					"			<div id='replyComment"+response.commentIDList[number]+"'>" + 
					"				<hr><span class='list-group-item-heading' style='font-weight: bold;'>我的回覆</span>" + 
					"				<span class='pull-right'>"+response.time_2List[number]+"</span><br>" + 	//回覆時間
					"				<p class='pull-left' style='margin-top: 1vh;'>"+returnEscapeCharacter(response.comment_2List[number])+"</p><br>" +		//回覆內容
					"				<p class='pull-right'>" + 
					"				<span class='commentButton' style='cursor: pointer;' onclick='deleteReplyComment(\""+response.commentIDList[number]+"\",\""+returnEscapeCharacter(response.nameList[number])+"\",\""+response.time_1List[number]+"\",\""+returnEscapeCharacter(response.comment_1List[number])+"\",\""+response.patientIDList[number]+"\",\""+response.hideImgList[number]+"\")'>刪除</span>"+
					"			</div>";
				}
				insertHtml+=
					"		</div>"+
					"	</div>"+
					"</li>";
				$("#comment").append(insertHtml);
			}
		},
		error : function() {console.log("錯誤訊息");}
	});
}

//刪除病患留言
function deleteComment(commentID){
	$("body").append(						
			"		<!-- 提示訊息 modal -->"+
			"		<div id='deleteCommentModal' class='modal fade' role='dialog'>"+
			"			<div class='modal-dialog modal-sm'>"+
			"				<div class='modal-content'>"+
			"					<div class='modal-header'>"+
			"						<button type='button' class='close' data-dismiss='modal'>&times;</button>"+
			"						<h4 id='alertTitle' class='modal-title'>刪除</h4>		<!-- 提示訊息 modal 標題 -->"+
			"					</div>"+
			"					<div class='modal-body'>"+
			"						<p id='alertContent'>確定刪除此留言嗎?</p>		<!-- 提示訊息 modal 內容 -->"+
			"					</div>"+
			"					<div class='modal-footer'>				<!-- 按鈕可以只有確定，onclick的function可自行更改 -->"+
			"						<button type='button' class='btn btn-default' data-dismiss='modal' onclick='determineComment(\""+commentID+"\")'>確定</button>"+
			"						<button type='button' class='btn btn-default' data-dismiss='modal' onclick=''>取消</button>"+
			"					</div>"+
			"				</div>"+
			"			</div>"+
			"		</div>");
	$('#deleteCommentModal').modal('show');
}
function determineComment(commentID){
	$(document).ready(function() {
		$.ajax({
			url : url,
			data : {
				state : "deleteComment",
				commentID : commentID
			},
			dataType : "json",
			success : function(response) {
				$("#"+commentID).remove();	//清除病患留言UI
				
				//隱藏和清除modal
				$("#deleteCommentModal").hide();
				setTimeout(function(){ $("#deleteCommentModal").remove(); }, 1000);
			},
			error : function() {console.log("錯誤訊息");}
		});
	});
}
//刪除醫生回覆
function deleteReplyComment(commentID, name, time_1, comment_1, patientID, hideImg){
	$("body").append(						
			"		<!-- 提示訊息 modal -->"+
			"		<div id='deleteReplyModal' class='modal fade' role='dialog'>"+
			"			<div class='modal-dialog modal-sm'>"+
			"				<div class='modal-content'>"+
			"					<div class='modal-header'>"+
			"						<button type='button' class='close' data-dismiss='modal'>&times;</button>"+
			"						<h4 id='alertTitle' class='modal-title'>刪除</h4>		<!-- 提示訊息 modal 標題 -->"+
			"					</div>"+
			"					<div class='modal-body'>"+
			"						<p id='alertContent'>確定刪除此回覆嗎?</p>		<!-- 提示訊息 modal 內容 -->"+
			"					</div>"+
			"					<div class='modal-footer'>				<!-- 按鈕可以只有確定，onclick的function可自行更改 -->"+
			"						<button type='button' class='btn btn-default' data-dismiss='modal' onclick='determineReply(\""+commentID+"\",\""+name+"\",\""+time_1+"\",\""+comment_1+"\",\""+patientID+"\",\""+hideImg+"\")'>確定</button>"+
			"						<button type='button' class='btn btn-default' data-dismiss='modal' onclick=''>取消</button>"+
			"					</div>"+
			"				</div>"+
			"			</div>"+
			"		</div>");
	$('#deleteReplyModal').modal('show');
}
function determineReply(commentID, name, time_1, comment_1, patientID, hideImg){
	$(document).ready(function() {
		$.ajax({
			url : url,
			data : {
				state : "deleteReplyComment",
				commentID : commentID
			},
			dataType : "json",
			success : function(response) {
				$("#replyComment"+commentID).remove();	//清除醫生回覆UI
				
				//回覆貼回去，必須也把刪除清除，避免UI不一致
				$("#delete"+commentID).remove();
				$("#commentButton"+commentID).append(
						"				<span id='reply"+commentID+"' class='commentButton' style='cursor: pointer; margin-right: 1vw;' onclick='replyComment(\""+commentID+"\",\""+name+"\",\""+time_1+"\",\""+comment_1+"\",\""+patientID+"\",\""+hideImg+"\")' >回覆</span>"+
						"				<span id='delete"+commentID+"' class='commentButton' style='cursor: pointer;' onclick=deleteComment('"+commentID+"')>刪除</span>");
				//隱藏和清除modal
				$("#deleteReplyModal").hide();
				setTimeout(function(){ $("#deleteReplyModal").remove(); }, 1000);
			},
			error : function() {console.log("錯誤訊息");}
		});
	});
}

//回覆留言 
//點回覆會跳出回覆modal
function replyComment(commentID, name, time_1, comment_1, patientID, hideImg) {
	var insertHtml = "";
	insertHtml += 
		"		<div class='modal fade' id='replyCommentModal"+commentID+"' role='dialog'>" + 
		"			<div class='modal-dialog'>" + 
		"				<div class='modal-content'>" + 
		"					<div class='modal-header'>" + 
		"						<button type='button' class='close' data-dismiss='modal'>&times;</button>" + 
		"						<h4 class='modal-title'>回覆留言</h4>" + 
		"					</div>" + 
		"					<div class='modal-body'>" + 
		"						<li class='list-group-item'>" + 
		"							<div class='media'>" + 
		"								<div class='media-left media-middle'>";
	if(hideImg == "0"){
		insertHtml += 
		"									<img src='http://140.121.197.130:8004/BBDPPatient/ProfilePictureServlet?option=getProfilePicture&patientID="+patientID+"' onerror=failToLoadProfilePictureModal('"+patientID+"'); class='media-object' id='imgModal"+patientID+"' style='width: 60px; object-fit: cover; margin-right: 1vw;' />"; 	//病患大頭貼
	}
	else if(hideImg == "1"){
		insertHtml += 
			"									<img src='img/frame/user.png' class='media-object' style='width: 60px; object-fit: cover; margin-right: 1vw;' />"; 	//病患大頭貼
	}
	insertHtml += 
		"								</div>" + 
		"								<div class='media-body'>" + 
		"									<span class='list-group-item-heading' style='font-weight: bold;'>"+name+"</span>" + 	//病患姓名
		"									<span class='pull-right'>"+time_1+"</span><br>" + 	//病患留言時間
		"									<p class='pull-left' style='margin-top: 1vh;'>"+comment_1+"</p><br>" + 	//病患留言內容
		"								</div>" + 
		"							</div>" + 
		"						</li>" + 
		"						<input type='text' class='form-control' id='comment_2' style='margin-top: 2vh;' placeholder='回覆留言...' />" + 	//回覆輸入框
		"					</div>" + 
		"					<div class='modal-footer'>" + 
		"						<button type='button' class='btn btn-default' onclick='clickReply(\""+commentID+"\",\""+name+"\",\""+time_1+"\",\""+comment_1+"\",\""+patientID+"\",\""+hideImg+"\")' data-dismiss='modal'>送出</button>" + 
		"						<button type='button' class='btn btn-default' data-dismiss='modal'>取消</button>" + 
		"					</div>" + 
		"				</div>" + 
		"			</div>" + 
		"		</div>"
	$("body").append(insertHtml);
	$("#replyCommentModal"+commentID).modal('show');
}
//在回覆modal按下送出執行(暫時先只顯示回覆留言)
function clickReply(commentID, name, time_1, comment_1, patientID, hideImg) {
	console.log("comment_2 val : " + htmlEscapeCharacter($("#comment_2").val()));
	if(htmlEscapeCharacter($("#comment_2").val()) != ""){
		//醫生回覆
		$(document).ready(function() {
			$.ajax({
				url : url,
				data : {
					state : "replyComment",
					commentID : commentID,
					comment_2 : htmlEscapeCharacter($("#comment_2").val())
				},
				dataType : "json",
				success : function(response) {
					$("#reply"+commentID).parent("p.pull-right").parent("div.media-body").append(
							"															<div id='replyComment"+commentID+"'>" + 
							"																<hr><span class='list-group-item-heading' style='font-weight: bold;'>我的回覆</span>" + 
							"																<span class='pull-right'>"+getNowDate().substring(0,16)+"</span><br>" + 	//回覆時間
							"																<p class='pull-left' style='margin-top: 1vh;'>"+$("#comment_2").val()+"</p><br>" +		//回覆內容
							"																<p class='pull-right'>" + 
							"																<span class='commentButton' style='cursor: pointer;' onclick='deleteReplyComment(\""+commentID+"\",\""+name+"\",\""+time_1+"\",\""+comment_1+"\",\""+patientID+"\",\""+hideImg+"\")'>刪除</span>"+
							"															</div>");
					//清除原本回覆UI
					$("#reply"+commentID).remove();				
					
					//隱藏和清除回覆modal
					$("#replyCommentModal"+commentID).hide();
					setTimeout(function(){ $("#replyCommentModal"+commentID).remove(); }, 1000);
				},
				error : function() {console.log("錯誤訊息");}
			});
		});
	}
	else{
		modalGenerator("提示", "請輸入回覆");
	}
}

//取得現在時間
function getNowDate(){
	var timeDate= new Date();
	var tMonth = (timeDate.getMonth()+1) > 9 ? (timeDate.getMonth()+1) : '0'+(timeDate.getMonth()+1);
	var tDate = timeDate.getDate() > 9 ? timeDate.getDate() : '0'+timeDate.getDate();
	var tHours = timeDate.getHours() > 9 ? timeDate.getHours() : '0'+timeDate.getHours();
	var tMinutes = timeDate.getMinutes() > 9 ? timeDate.getMinutes() : '0'+timeDate.getMinutes();
	var tSeconds = timeDate.getSeconds() > 9 ? timeDate.getSeconds() : '0'+timeDate.getSeconds();
	timeDate= timeDate.getFullYear()+'-'+ tMonth +'-'+ tDate +' '+ tHours +':'+ tMinutes +':'+ tSeconds;
	return timeDate
}

//是當使用者沒有設定大頭照時會跑的function
function failToLoadProfilePicture(id){
	$("#img"+id).attr("src", "img/frame/user.png");
}
function failToLoadProfilePictureModal(id){
	$("#imgModal"+id).attr("src", "img/frame/user.png");
}