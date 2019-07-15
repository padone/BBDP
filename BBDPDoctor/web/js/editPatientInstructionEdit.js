var url = "PatientInstructionServlet";
var froalaUrl = "InstructionFroalaServlet";

var editURL = window.location.href;
var urlparts = editURL.split("?");
var IDparts = urlparts[1].split("=");
var patientInstructionID = IDparts[1];

var firstContent;	//初始文章內容
var srcImgVideo = [];		//存放src

var changed = false;	//判斷是否有修改//用於離開網頁時判斷

/*var newImg = [];
var newImgNum = 0;
var delImg = [];
var delImgNum = 0;*/

$(document).ready(function() {
	checkInstructionID(patientInstructionID);	//檢查InstructionID
	
	froalaEdit();		//HTML編輯器
	getInstruction();  	//取得衛教資訊
	
	checkChanged();		//是否修改檢查//froala檢查比較原始內容跟更改內容即可
});

//取得衛教資訊
function getInstruction(){
	var type2, symptom;
	$.ajax({
		url : url,
		data : {
			state : "getInstruction",
			patientInstructionID : patientInstructionID
		},
		dataType : "json",
		success : function(response) {
			$("#title").val(returnEscapeCharacter(response.title));
			$("#symptom").val(returnEscapeCharacter(response.symptom));
			$('div#froala-editor').froalaEditor('html.insert', response.content, true);	
			getTypeSymptom(response.type, response.symptom);	//取得衛教資訊分類、症狀的值
			
			firstContent = response.content;
			//$("#type2 option[value='"+response.type+"']").attr("selected", true);		//顯示衛教資訊分類//$("#symptom option[value='"+response.symptom+"']").attr("selected", true);	//顯示症狀分類
		},
		error : function() {console.log("錯誤訊息");}
	});
}

//取得衛教資訊分類、症狀的值
function getTypeSymptom(type2, symptom){
	$.ajax({
		url : url,
		data : {
			state : "getTypeSymptom"
		},
		dataType : "json",
		success : function(response) {	
			//取得衛教資訊選單的值
			for(var number = 0; number < response.typeList.length; number++){
				if(response.typeList[number] != type2)
					$("#type2").append("<option value=''>"+returnEscapeCharacter(response.typeList[number])+"</option>");
				else
					$("#type2").append("<option value='' selected>"+returnEscapeCharacter(response.typeList[number])+"</option>");
			}
			//取得症狀選單的值
			for(var number = 0; number < response.symptomList.length; number++){
				if(response.symptomList[number] != symptom)
					$("#symptom").append("<option value=''>"+returnEscapeCharacter(response.symptomList[number])+"</option>");
				else
					$("#symptom").append("<option value='' selected>"+returnEscapeCharacter(response.symptomList[number])+"</option>");
			}
		},
		error : function() {console.log("錯誤訊息");}
	});
}

//儲存編輯
function updateInstruction(){
	if(titleJudge()['flag']&& typeJudge()['flag']&&symptomJudge()['flag']&&htmlJudge()['flag']){
		$.ajax({
			url : url,
			data : {
				state : "updateInstruction",
				title : titleJudge()['title'],
				type : typeJudge()['type'],
				symptom : symptomJudge()['symptom'],
				html : htmlJudge()['html'],
				patientInstructionID : patientInstructionID
			},
			dataType : "json",
			success : function(response) {
				if(response){
					getImgSrc(htmlJudge()['html'], srcImgVideo);					//取得圖片位置
					getVideoSrc(htmlJudge()['html'], srcImgVideo);					//取得影片位置
					deleteNotInFolder(patientInstructionID, srcImgVideo);			//刪除不應該在資料夾裡的影片或照片
					
					modalGenerator("提示", "儲存成功");
					changed = false;
					firstContent = htmlJudge()['html'];	//這樣才能儲存離開不會跳出視窗
					setTimeout(function(){
						window.location.href = 'EditPatientInstruction.html?patientInstructionID=' + patientInstructionID;
					},1500);
				}
				else{
					modalGenerator("提示", "儲存失敗");
				}
			},
			error : function() {console.log("錯誤訊息");}
		});
		
	}
	else{
		modalGenerator("提示", errorsJudge());
	}
}

/********************************************************************************************/

//HTML編輯器
function froalaEdit(){
	var height = $("div.right-content").height() - $(".right-content > .panel-heading").height() - 120 - 50;
	$(function() {
		$('div#froala-editor').froalaEditor({
			language: 'zh_tw',
			placeholderText: '請輸入文章內容...',
			height: height,
			//>= 1200px
			toolbarButtons: ['undo', 'redo', '|', 'fontFamily', '|', 'fontSize',  'color', '|', 'bold', 'italic', 'underline', 'strikeThrough', 'subscript', 'superscript', '-', '|', 'paragraphFormat', 'align', 'formatOL', 'formatUL', 'outdent', 'indent', '|', 'specialCharacters', 'insertHR', 'quote', 'insertLink', 'insertTable', 'insertImage', 'insertVideo', '|', 'selectAll', 'clearFormatting', '|', 'fullscreen', 'help'],
			//>= 992px
			toolbarButtonsMD: ['undo', 'redo', '|', 'fontFamily', '|', 'fontSize',  'color', '|', 'bold', 'italic', 'underline', 'strikeThrough', 'subscript', 'superscript', '|', 'paragraphFormat', 'align', 'formatOL', 'formatUL', 'outdent', 'indent', '|', 'specialCharacters', 'insertHR', 'quote', 'insertLink', 'insertTable', 'insertImage', 'insertVideo', '|', 'selectAll', 'clearFormatting', '|', 'fullscreen', 'help'],
			//>= 768px
			toolbarButtonsSM: ['undo', 'redo', '|', 'fontFamily', '|', 'fontSize',  'color', '|', 'bold', 'italic', 'underline', 'strikeThrough', 'subscript', 'superscript', '|', 'paragraphFormat', 'align', 'formatOL', 'formatUL', 'outdent', 'indent', '|', 'specialCharacters', 'insertHR', 'quote', 'insertLink', 'insertTable', 'insertImage', 'insertVideo', '|', 'selectAll', 'clearFormatting', '|', 'fullscreen', 'help'],
			//< 768px
			toolbarButtonsXS: ['undo', 'redo', '|', 'fontFamily', '|', 'fontSize',  'color', '|', 'bold', 'italic', 'underline', 'strikeThrough', 'subscript', 'superscript', '|', 'paragraphFormat', 'align', 'formatOL', 'formatUL', 'outdent', 'indent', '|', 'specialCharacters', 'insertHR', 'quote', 'insertLink', 'insertTable', 'insertImage', 'insertVideo', '|', 'selectAll', 'clearFormatting', '|', 'fullscreen', 'help'],
			fontFamilySelection: true,
			fontSizeSelection: true,
			fontSizeDefaultSelection: '30',
			fontSize: ['30', '36', '40', '48', '50', '55', '60', '72', '80', '84', '96', '100', '108', '120'],
			linkList: [{
				text: 'Google',
				href: 'http://google.com',
				target: '_blank',
				rel: 'nofollow'
			}],
			paragraphFormat: {
				N: 'Normal',
				H1: 'Heading 1',
				H2: 'Heading 2',
				H3: 'Heading 3',
				H4: 'Heading 4'
			},
			//圖片和影片上傳位置需要改
			imageUploadURL: froalaUrl,
			imageUploadParams: {state: "uploadImage",patientInstructionID: patientInstructionID},
			imageUploadMethod:"POST",
			imageMaxSize: 5 * 1024 * 1024,
			imageAllowedTypes: ['jpeg', 'jpg', 'png', 'gif'],
			imageEditButtons: ['imageReplace', 'imageAlign', 'imageRemove', '|', 'imageLink', 'linkOpen', 'linkEdit', 'linkRemove', '-', 'imageDisplay', 'imageAlt', 'imageSize'],
			
			//設置視頻上傳參數。
	        videoUploadURL: froalaUrl,
	        videoUploadParams: { state: "uploadVideo",patientInstructionID: patientInstructionID},
	        videoUploadMethod: 'POST',
	        videoMaxSize: 50 * 1024 * 1024,
	        videoAllowedTypes: [ 'webm' ,'mp4' ,'ogg' ], 	 //允許上傳MP4，WEBM和OGG
	        videoInsertButtons: [ 'videoBack' ,'|','videoByURL' ,'videoUpload' ],	//插入影片方式:url，本地端上傳
		})
		/*********************************image*********************************/
		.on('froalaEditor.image.beforeUpload', function (e, editor, images) {
			// Return false if you want to stop the image upload.//事件在開始上傳請求之前觸發，可用於更改上傳參數或取消操作
		})
		.on('froalaEditor.image.uploaded', function (e, editor, response) {
			// Image was uploaded to the server.//在成功的圖像上傳請求後，但在編輯器中插入圖像之前觸發事件
		})
		.on('froalaEditor.image.inserted', function (e, editor, $img, response) {
			// Image was inserted in the editor.//將圖像插入編輯器後觸發事件
			//newImg[newImgNum] = $img.attr('src');
			//newImgNum += 1;
		})
		.on('froalaEditor.image.replaced', function (e, editor, $img, response) {
			// Image was replaced in the editor.//將圖像替換為編輯器後觸發事件
		})
		.on('froalaEditor.image.error', function (e, editor, error, response) {
			if (error.code == 1) { console.log("Bad link."); }
       		else if (error.code == 2) { console.log("No link in upload response."); }
			else if (error.code == 3) { console.log("Error during image upload."); }
			else if (error.code == 4) { console.log("Parsing response failed."); }
			else if (error.code == 5) { console.log("Image too text-large."); }
			else if (error.code == 6) { console.log("Invalid image type."); }
			else if (error.code == 7) { console.log("Image can be uploaded only to same domain in IE 8 and IE 9."); }
			// Response contains the original server response to the request if available.
		})
		.on('froalaEditor.image.removed', function (e, editor, $img) {
			//deleteImage(e, editor, $img);	//刪除image
			//delImg[delImgNum] = $img.attr('src');
			//delImgNum += 1;
		})
	})
}
/*暫時用不到
//刪除delImg陣列
function delImgArrayDelete(){
	for(var i=0;i<delImgNum;i++){
		deleteImage(delImg[i]);
	}
}

//刪除newImg陣列
function newImgArrayDelete(){
	for(var i=0;i<newImgNum;i++){
		deleteImage(newImg[i]);
	}
}
*/
/********************************************************************************************/

//尚未儲存
$(window).on('beforeunload', function() {
	console.log("changed : " + changed);
	if(firstContent!=$('div#froala-editor').froalaEditor('html.get', true)){
		console.log("跟原本不一樣喔!!!!!!!!!!!!!!!!");
	}
	if(changed || (firstContent!=$('div#froala-editor').froalaEditor('html.get', true)) ) {	//froala檢查比較原始內容跟更改內容即可
		getImgSrc(firstContent, srcImgVideo);					//取得圖片位置
		getVideoSrc(firstContent, srcImgVideo);					//取得影片位置
		deleteNotInFolder(patientInstructionID, srcImgVideo);	//刪除不應該在資料夾裡的影片或照片
		return '尚有未儲存的修改。'
	}
});