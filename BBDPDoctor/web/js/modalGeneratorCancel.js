$(document).ready(function() {
	$("body").append(						
			"		<!-- 提示訊息 modal -->"+
			"		<div id='alertModalCancel' class='modal fade' role='dialog'>"+
			"			<div class='modal-dialog modal-sm'>"+
			"				<div class='modal-content'>"+
			"					<div class='modal-header'>"+
			"						<button type='button' class='close' data-dismiss='modal'>&times;</button>"+
			"						<h4 id='alertTitleCancel' class='modal-title'></h4>		<!-- 提示訊息 modal 標題 -->"+
			"					</div>"+
			"					<div class='modal-body'>"+
			"						<p id='alertContentCancel'></p>		<!-- 提示訊息 modal 內容 -->"+
			"					</div>"+
			"					<div class='modal-footer'>				<!-- 按鈕可以只有確定，onclick的function可自行更改 -->"+
			"						<button type='button' class='btn btn-default' data-dismiss='modal' onclick='determine()'>確定</button>"+
			"						<button type='button' class='btn btn-default' data-dismiss='modal' onclick=''>取消</button>"+
			"					</div>"+
			"				</div>"+
			"			</div>"+
			"		</div>");
});

function modalGeneratorCancel(title, body){
	$('#alertTitleCancel').empty(title);
	$('#alertContentCancel').empty(body);
	$('#alertTitleCancel').append(title);
	$('#alertContentCancel').append(body);
	$('#alertModalCancel').modal('show');
}
/*
//寫在所需要的網頁裡
function determine(){
	console.log("determine()");
}
*/
