$(document).ready(function() {
	$("body").append(						
			"		<!-- 提示訊息 modal -->"+
			"		<div id='alertModal' class='modal fade' role='dialog'>"+
			"			<div class='modal-dialog modal-sm'>"+
			"				<div class='modal-content'>"+
			"					<div class='modal-header'>"+
			"						<button type='button' class='close' data-dismiss='modal'>&times;</button>"+
			"						<h4 id='alertTitle' class='modal-title'></h4>		<!-- 提示訊息 modal 標題 -->"+
			"					</div>"+
			"					<div class='modal-body'>"+
			"						<p id='alertContent'></p>		<!-- 提示訊息 modal 內容 -->"+
			"					</div>"+
			"					<div class='modal-footer'>				<!-- 按鈕可以只有確定，onclick的function可自行更改 -->"+
			"						<button type='button' class='btn btn-default' data-dismiss='modal' onclick=''>確定</button>"+
			"					</div>"+
			"				</div>"+
			"			</div>"+
			"		</div>");
});

function modalGenerator(title, body){
	$('#alertTitle').empty();
	$('#alertContent').empty();
	$('#alertTitle').append(title);
	$('#alertContent').append(body);
	$('#alertModal').modal('show');
}