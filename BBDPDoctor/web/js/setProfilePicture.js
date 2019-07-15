$(document).ready(function(){
	//設定上傳按鈕
	$("#profilePicturemodalFooter").empty();
	$("#profilePicturemodalFooter").append("<button id='uploadProfilePicture' type='button' class='btn btn-default' data-dismiss=''>上傳</button>");

	var c = new ZmCanvasCrop({
							fileInput: $('#ipt')[0],  
							saveBtn: $('#uploadProfilePicture')[0],
							box_width: 480,  //剪裁容器的最大寬度
							box_height: 300, //剪裁容器的最大高度
							min_width: 200,  //要剪裁图片的最小宽度 596
							min_height: 200  //要剪裁图片的最小高度 370
						}, saveCallBack);

	$("#doctorProfilePicture").empty();

	var doctorID = "";
	//取得doctorID
	$.ajax({
		type: "POST",
		url: "DoctorProfilePictureServlet",	 
		data: { option : "getDoctorID" },
		dataType: "text",
														
		success : function(response){
			doctorID=response;
			if(doctorID.length>0){
				$("#doctorProfilePicture").append("<img class='img-responsive' style='max-height: 20vh; margin-top: 2vh;' src='DoctorProfilePictureServlet?option=getDoctorProfilePicture&doctorID=" + doctorID + "' onerror='failToLoadProfilePicture();'/>");
			}
			else{
				failToLoadProfilePicture();
			}
		},
		error : function(){
			console.log("server沒有回應");
		}
	});
});

	

	//顯示上傳大頭貼的Modal
	function selectProfilePicture() {
		//清空提示
		$("#selecttProfilePictureContent").empty();
		
		//Modal出現
		$("#selectProfilePictureModal").modal("show");
	}

	function failToLoadProfilePicture(){
			$("#doctorProfilePicture").empty();
			$("#doctorProfilePicture").append("<img class='img-responsive' style='max-height: 20vh; margin-top: 2vh;' src='img/accountSetting/doctor.png' onerror='failToLoadProfilePicture();'/>");
	}

	function saveCallBack(base64) {
		if(base64 !="noSelect"){
			//讓上傳按鈕不能按
			$("#profilePicturemodalFooter").empty();
			$("#profilePicturemodalFooter").append("<button id='uploadProfilePicture' type='button' class='btn btn-default disabled' data-dismiss=''>上傳中...</button>");
			
			var data=base64;
			// dataURL 的格式为 “data:image/png;base64,****”,逗号之前都是一些说明性的文字，我们只需要逗号之后的就行了
			data=data.split(',')[1];
					
			data=window.atob(data);

			var ia = new Uint8Array(data.length);
			for (var i = 0; i < data.length; i++) {
				ia[i] = data.charCodeAt(i);
			};

			// canvas.toDataURL 返回的默认格式就是 image/png
			var blob=new Blob([ia], {type:"image/png"});

			var fd=new FormData();
			fd.append('file',blob);

			$.ajax({
				url:"DoctorProfilePictureServlet?option=uploadProfilePicture",
				type:"POST",
			    data:fd, 
			    processData: false, // 告诉jQuery不要去处理发送的数据
				contentType: false, // 告诉jQuery不要去设置Content-Type请求头
			    success : function(){
					//隱藏modal
					$("#selectProfilePictureModal").modal("hide");
					location.reload();
				},
				error : function() {				
					//隱藏modal
					$("#selectProfilePictureModal").modal("hide");
					console.log("server沒有回應");				
				}
			});
		}
		else{
			$("#selecttProfilePictureContent").append("請選擇檔案");
		}						
	}