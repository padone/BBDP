var url = "PatientInstructionServlet";
var froalaUrl = "InstructionFroalaServlet";

var maxInstructionID = 0;	//取得最大id
var srcImgVideo = [];		//存放src

var changed = false;	//判斷是否有修改//用於離開網頁時判斷

//取得衛教資訊分類、症狀的值
$(document).ready(function() {
	getTypeSymptom();		//取得衛教資訊選單的值//取得症狀選單的值
	getMaxInstructionID();	//取得maxpatientInstructionID
	
	checkChanged();			//是否修改檢查
	$(function() {			//froala檢查
		$('div#froala-editor').on('froalaEditor.contentChanged', function (e, editor) {
			changed = true;
		})
	});
});

//取得衛教資訊選單的值//取得症狀選單的值
function getTypeSymptom(){
	$.ajax({
		url : url,
		data : {
			state : "getTypeSymptom"
		},
		dataType : "json",
		success : function(response) {
			//取得衛教資訊選單的值
			for(var number = 0; number < response.typeList.length; number++){
				$("#type2").append("<option value='"+response.typeList[number]+"'>"+returnEscapeCharacter(response.typeList[number])+"</option>");
			}
			//取得症狀選單的值
			for(var number = 0; number < response.symptomList.length; number++){
				$("#symptom").append("<option value='"+response.symptomList[number]+"'>"+returnEscapeCharacter(response.symptomList[number])+"</option>");
			}
		},
		error : function() {console.log("錯誤訊息");}
	});
}

//新增衛教資訊
function addInstruction(){	
	if(titleJudge()['flag']&& typeJudge()['flag']&&symptomJudge()['flag']&&htmlJudge()['flag']){
		$.ajax({
			url : url,
			data : {
				state : "newInstruction",
				title : titleJudge()['title'],
				type : typeJudge()['type'],
				symptom : symptomJudge()['symptom'],
				html : htmlJudge()['html']
			},
			dataType : "json",
			success : function(response) {
				if(response){
					changed = false;
					
					getImgSrc(htmlJudge()['html'], srcImgVideo);		//取得圖片位置
					getVideoSrc(htmlJudge()['html'], srcImgVideo);		//取得影片位置
					deleteNotInFolder(maxInstructionID, srcImgVideo);	//刪除不應該在資料夾裡的影片或照片
					
					modalGenerator("提示", "新增成功");
					setTimeout(function(){
						window.location.href = 'PatientInstruction.html';
					},1500);
				}
				else{
					modalGenerator("提示", "新增失敗");
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

//取得maxpatientInstructionID
function getMaxInstructionID(){
	$.ajax({
		url : "PatientInstructionServlet",
		data : {
			state : "getMaxInstructionID"
		},
		dataType : "json",
		success : function(response) {
			maxInstructionID = response;
			froalaEdit();
		},
		error : function() {console.log("錯誤訊息");}
	});
}

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
			imageUploadParams: {state: "uploadImage",patientInstructionID: maxInstructionID},
			imageUploadMethod:"POST",
			imageMaxSize: 5 * 1024 * 1024,
			imageAllowedTypes: ['jpeg', 'jpg', 'png', 'gif'],
			//imageRoundPercent: true, 		//調整大小時，將圖像百分比逼近整數。
			//imageOutputSize: true,			//圖像將在輸出中將寬度和高度設置為屬性。
			//圖片加上樣式有問題，先刪掉'imageStyle'
			imageEditButtons: ['imageReplace', 'imageAlign', 'imageRemove', '|', 'imageLink', 'linkOpen', 'linkEdit', 'linkRemove', '-', 'imageDisplay', 'imageAlt', 'imageSize'],
			
			//設置視頻上傳參數。
	        videoUploadURL: froalaUrl,
	        videoUploadParams: { state: "uploadVideo",patientInstructionID: maxInstructionID},
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
			/*刪除image
			$.ajax({
		    	  method: 'POST',						
		          url: froalaUrl,		
		          data: {								
		        	  state: "deleteImage",
		        	  src: $img.attr('src')
		          }
		      })
		      .done (function (data) {
		    	  console.log ('Image was deleted');
		      })
		      .fail (function (err) {
		    	  console.log ('Image delete problem: ' + JSON.stringify(err));
		      })*/
		})
		/*********************************vedio********************************
		.on('froalaEditor.video.beforeUpload', function (e, editor, videos) {
			// Return false if you want to stop the video upload.//事件在開始上傳請求之前觸發，可用於更改上傳參數或取消操作
			console.log("beforeUpload");
		})
		.on('froalaEditor.video.uploaded', function (e, editor, response) {
			// Video was uploaded to the server.//事件在成功的視頻上傳請求之後，但在編輯器中插入視頻之前觸發
			console.log("uploaded");
		})
		.on('froalaEditor.video.inserted', function (e, editor, $video) {
			console.log("inserted");
		})
		.on('froalaEditor.video.replaced', function (e, editor, $img, response) {
			// Video was replaced in the editor.//將視頻替換為編輯器後觸發事件
			console.log("replaced");
		})
		.on('froalaEditor.video.beforeRemove', function (e, editor, $video) {
			console.log("beforeRemove");
		})
		.on('froalaEditor.video.removed', function (e, editor, $video) {
			console.log("removed");
			console.log("刪除video");
			console.log("$video.attr('src') : " + $video.attr('src'));
			$.ajax({
				method: 'POST',						
		        url: froalaUrl,		
		        data: {								
		      	  state: "deleteVideo",
		      	  src: $video.attr('src')
		        }
		    })
		    .done (function (data) {
		  	  console.log ('Vedio was deleted');
		    })
		    .fail (function (err) {
		  	  console.log ('Vedio delete problem: ' + JSON.stringify(err));
		    })
		});*/
	})
}

/********************************************************************************************/

//尚未儲存
$(window).on('beforeunload', function() {
	if(changed) return '尚有未儲存的修改。';
});

//網頁離開時檢查是否新增圖片
window.onunload = function(){
	if(changed){
		deleteFolder(maxInstructionID);		//刪除資料夾
	}
};