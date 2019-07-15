//prepend 框架(上方棕色navbar和左方灰色navbar)
$("div#wrapper").prepend(
"			<nav class='navbar navbar-default navbar-static-top' role='navigation' style='margin-bottom: 0'>" + "\n" + 
"				<div class='navbar-header'>" + "\n" + 
"					<button type='button' class='navbar-toggle' data-toggle='collapse' data-target='.navbar-collapse'>" + "\n" + 
"						<span class='sr-only'>Toggle navigation</span>" + "\n" + 
"						<span class='sr-only'>Toggle navigation</span>" + "\n" + 
"						<span class='icon-bar'></span>" + "\n" + 
"						<span class='icon-bar'></span>" + "\n" + 
"						<span class='icon-bar'></span>" + "\n" + 
"					</button>" + "\n" + 
"					<a class='navbar-brand' href='Homepage.html' style='padding: 0;'><img src='img/frame/logo_white.png' class='img-responsive' style='height: 7vh; padding: 1.2vh; display: inline;' /></a>" + "\n" + 
"					<a class='navbar-brand' href='Homepage.html'>Bridge Between Doctor and Patient</a>" + "\n" + 
"				</div>" + "\n" + 
"				<ul class='nav navbar-top-links navbar-right'>" + "\n" + 
"					<li class='dropdown'>" + "\n" + 
"						<a href='Homepage.html'>" + "\n" + 
"							<i class='fa fa-home fa-fw fa-lg' style='color: white;'></i>" + "\n" + 
"						</a>" + "\n" + 
"					</li>" + "\n" + 
"					<li class='dropdown'>" + "\n" + 
"						<a class='dropdown-toggle' data-toggle='dropdown' href='#' onclick='getAllNotification()'>" + "\n" + 
"							<i class='fa fa-bell fa-fw' style='color: white;'></i><i class='fa fa-caret-down' style='color: white;'></i>" + "\n" + 
"						</a>" + "\n" + 
"						<ul class='dropdown-menu notification-list'>" + "\n" + 
"							<li style='cursor: pointer;' onclick='clearAllNotification()'>" + "\n" + 
"								<a>" + "\n" + 
"									<div class='row'>" + "\n" + 
"										<div class='col-md-12 col-xs-12'>" + "\n" + 
"											<span class='pull-right'>" + "\n" + 
"												<i class='fa fa-trash-o'></i>" + "\n" + 
"												清除所有通知" + "\n" + 
"											</span>" + "\n" + 
"										</div>" + "\n" + 
"									</div>" + "\n" + 
"								</a>" + "\n" + 
"							</li>" + "\n" + 
"							<li class='divider'></li>" + "\n" + 
"							<li>" + "\n" + 
"								<a>" + "\n" + 
"									<div class='row'>" + "\n" + 
"										<div class='col-md-12 col-xs-12'>" + "\n" + 
"											<h5>沒有通知</h5>" + "\n" + 
"										</div>" + "\n" + 
"									</div>" + "\n" + 
"								</a>" + "\n" + 
"							</li>" + "\n" + 
"						</ul>" + "\n" + 
"					</li>" + "\n" + 
"					<li class='dropdown'>" + "\n" + 
"						<a class='dropdown-toggle' data-toggle='dropdown' href='#'>" + "\n" + 
"							<i class='fa fa-user fa-fw' style='color: white;'></i><i class='fa fa-caret-down' style='color: white;'></i>" + "\n" + 
"						</a>" + "\n" + 
"						<ul class='dropdown-menu'>" + "\n" + 
"							<li style='cursor: pointer;'><a href='AccountSetting.html'><i class='fa fa-gear fa-fw'></i>設定</a></li>" + "\n" + 
"							<li class='divider'></li>" + "\n" + 
"							<li style='cursor: pointer;'><a id='logout'><i class='fa fa-sign-out fa-fw'></i>登出</a></li>" + "\n" + 
"						</ul>" + "\n" + 
"					</li>" + "\n" + 
"				</ul>" + "\n" + 
"				<div class='navbar-default2 sidebar' role='navigation'>" + "\n" + 
"					<div class='sidebar-nav navbar-collapse'>" + "\n" + 
"						<ul class='nav' id='side-menu'>" + "\n" + 
"							<li class='sidebar-search'>" + "\n" + 
"								<div style='margin-bottom: 2vh'>" + "\n" + 
"									<b>病患資訊</b>" + "\n" + 
"								</div>" + "\n" + 
"								<div class='input-group custom-search-form'>" + "\n" + 
"									<input type='text' class='form-control' placeholder='身份證後五碼' id='searchPatientID'>" + "\n" + 
"									<span class='input-group-btn'>" + "\n" + 
"										<button class='btn btn-default' type='button' id='pateintSearch' onclick='searchPatientID()'>" + "\n" + 
"											<i class='fa fa-search'></i>" + "\n" + 
"										</button>" + "\n" + 
"									</span>" + "\n" + 
"								</div>" + "\n" + 
"							</li>" + "\n" + 
"							<li>" + "\n" + 
"								<a href='#' style='color: #2e2d4d;'><b>資訊平台</b><span class='fa arrow'></span></a>" + "\n" + 
"								<ul class='nav nav-second-level'>" + "\n" + 
"									<li>" + "\n" + 
"										<a href='PatientInstruction.html' style='color: #2e2d4d;'><b>衛教資訊</b></a>" + "\n" + 
"									</li>" + "\n" + 
"									<li>" + "\n" + 
"										<a href='ClinicHours.html' style='color: #2e2d4d;'><b>門診資訊</b></a>" + "\n" + 
"									</li>" + "\n" + 
"								</ul>" + "\n" + 
"							</li>" + "\n" + 
"							<li>" + "\n" + 
"								<a href='#' style='color: #2e2d4d;'><b>問卷區</b><span class='fa arrow'></span></a>" + "\n" + 
"								<ul class='nav nav-second-level'>" + "\n" + 
"									<li>" + "\n" + 
"										<a href='QuestionnaireModule.html' style='color: #2e2d4d;'><b>問卷模板</b></a>" + "\n" + 
"									</li>" + "\n" + 
"									<li>" + "\n" + 
"										<a href='QuestionnairePool.html' style='color: #2e2d4d;'><b>問卷題庫</b></a>" + "\n" + 
"									</li>" + "\n" + 
"									<li>" + "\n" + 
"										<a href='QuestionnaireTempStorage.html' style='color: #2e2d4d;'><b>題目暫存區</b></a>" + "\n" + 
"									</li>" + "\n" + 
"								</ul>" + "\n" + 
"							</li>" + "\n" + 
"							<li>" + "\n" + 
"								<a href='HealthTracking.html' style='color: #2e2d4d;'><b>健康狀況追蹤模板</b></a>" + "\n" + 
"							</li>" + "\n" + 
"							<li>" + "\n" + 
"								<a href='Notice.html' style='color: #2e2d4d;'><b>注意事項模板</b></a>" + "\n" + 
"							</li>" + "\n" + 
"						</ul>" + "\n" + 
"					</div>" + "\n" + 
"				</div>" + "\n" + 
"			</nav>" + "\n"
);

//可以在這裡引用各種js
$(document).ready(function() {
	// 病患資訊搜尋
	$.getScript("js/patientSearch.js");		//請勿更動此行順序 否則用手機瀏覽網頁時 搜尋的modal會無法顯示
	// 登出
	$("#logout").click(function() {
		$.getScript("js/logout.js");
	});
	//通知欄
	$.getScript("js/notification.js");
	//允許顯示通知
	Notification.requestPermission();
	// 判斷是否登入 js
	$.getScript("js/judgeLogin.js");
	// Chrome notification
	$("head").append("<link rel='stylesheet' type='text/css' href='frame/lobibox/dist/css/lobibox.min.css'>");
	$.getScript("frame/lobibox/dist/js/lobibox.min.js");
	$.getScript("frame/lobibox/dist/js/notifications.min.js");
});