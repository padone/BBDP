
function ZmCanvasCrop(opt, saveCallBack){
	this.init(opt);
	this._option.crop_box_width = opt.box_width; //裁剪的最大寬度
	this._option.crop_box_height = opt.box_height;  //裁剪的最大高度
	this._option.crop_min_width = opt.min_width;  //裁剪的最小寬度
	this._option.crop_min_height = opt.min_height;  //裁剪的最小高度
	this._option.crop_scale = opt.min_width / opt.min_height;  //按照比例裁剪	
}

ZmCanvasCrop.prototype = {

	_$box : '',
	_$canvasDown: '',
	_$canvasUp: '',
	_input : '',
	_ctxUp: '',					//裁剪區域canvas
	_img : '',
	_img_show: {
		width: '',
		height: '',
		scale: '', 				//顯示像素:實際像素
		crop_width: '',			//要裁剪部分顯示寬
		crop_height: '',
		min_width: '',			//要裁剪部份顯示最小寬度
		min_height: ''
	},

	_option : {
		crop_box_width: '',		//圖片操作區域寬限制
		crop_box_height: '',	//圖片操作區域高限制
		crop_min_width: '',		//最小像素寬
		crop_min_height: '',	//最小像素高
		crop_scale: '' 			//寬高比
	},
	_save: {
		left: '',
		top: '',
		width: '',
		height: ''
	},
	_resize_point: {
		color: '#6699FF',
		size: 8
	},
	_resize_btn: {},

	init: function(opt){
		var self = this;
		self._input = opt.fileInput;

		self._$box = $('.canvas-box');
		self.readFile();

		opt.saveBtn.addEventListener('click', function(){
			self.save();
		});
	},

	imgTrue: function(){
		if(this._img.width < this._option.crop_min_width || this._img.height < this._option.crop_min_height){
			return false;
		}
		return true;
	},

	readFile: function(){
		var self = this;

		if(typeof FileReader==='undefined'){ 
		    alert("瀏覽器不支援 FileReader"); 
		    input.setAttribute('disabled','disabled'); 
		}else{ 
		    this._input.addEventListener('change', readFile, false);
		} 

		function readFile(){ 
		    var file = this.files[0]; 
		    var reader = new FileReader(); 
		    reader.readAsDataURL(file); 
		    reader.onload = function(e){ 
		        self.drawCavDown(this.result);
		   	    
		    } 
		}
	},

	drawCavDown: function(src){
		var self = this;
		//清除上一次的
		self._$box.html('');
		self._save = {};
		self._img_show = {};

		self._img = new Image();

	    self._img.onload = function(){

	    	if(!self.imgTrue()){
	    		alert('請選擇大一點的圖片');
	    		return;
	    	} 
	    	//將寬高撐滿
	    	self.setShowImg();
	    	self._img_show.scale = self._img_show.width / self._img.width;//縮放比例
	    	//計算裁剪最小寬高
	    	self._img_show.min_width = self._option.crop_min_width * self._img_show.scale;
	    	self._img_show.min_height = self._option.crop_min_height * self._img_show.scale;

	    	//初始化顯示剪裁框寬高，按照寬或者高（更小）的一半顯示，如果一半值小於最小可剪裁值，還是按最小剪裁值顯示
			var size;
            if (self._img.width > self._img.height) {
            	size = self._img.height / 2;
            	if(size<self._option.crop_min_height){
		           self.resizeCrop({
						width: self._option.crop_min_width,
						height: self._option.crop_min_height
					});
            	}else{
            		 self.resizeCrop({
		        		height: size,
		                width: size * self._option.crop_scale 
		            });
            	}
            } else {
            	size = self._img.width / 2;
            	if(size<self._option.crop_min_width){
		           self.resizeCrop({
						width: self._option.crop_min_width,
						height: self._option.crop_min_height
					});
            	}else{
            		 self.resizeCrop({
		        		height: size / self._option.crop_scale ,
		                width: size
		            });
            	}
            }
           
	      
	      	//繪製裁剪區域
	      	drawDown();

	      	//載入上層canvas
	      	self.addUpCanvas();
	      	//鬆開滑屬事件
	      	$(document).on('mouseup', function(){
				$(document).off('mousemove');
			});
	    }

	    self._img.src = src;

	    function drawDown(){
	    	var $canvas = $('<canvas width="' + self._img_show.width  + '" height="' + self._img_show.height + '"></canvas>');
			self._$box.append($canvas);
	      	var $ctx = $canvas[0].getContext('2d');
	      	$ctx.drawImage(self._img, 0, 0, self._img_show.width, self._img_show.height);
			//裁剪區域透明度
			$ctx.beginPath();
			$ctx.fillStyle="rgba(0,0,0,0.6)";
			$ctx.fillRect(0, 0, self._img_show.width, self._img_show.height);
			self._$canvasDown = $canvas;
		}

	},
	setResizePoint: function(direction, left, top){
		return $('<div class="resize-point" style="width:' + this._resize_point.size +'px;height:' + this._resize_point.size + 'px;'+
			'background: ' + this._resize_point.color + ';cursor:'+ direction +';position:absolute;'+
			'left:'+ left +'px;top:'+ top +'px"></div>');
	},

	addUpCanvas: function(){
		
		var self = this;
		self.addResizeBtn();//加入縮放按鈕

		self._ctxUp = self._$canvasUp[0].getContext('2d'); 
		self._ctxUp.drawImage(self._img,  0, 0, self._img_show.crop_width / self._img_show.scale, self._img_show.crop_height / self._img_show.scale,0, 0, self._img_show.crop_width, self._img_show.crop_height);
	
		//初始化儲存
		self._save.left = 0;
		self._save.top = 0;
		self._save.width = self._img_show.crop_width / self._img_show.scale;
		self._save.height = self._img_show.crop_height / self._img_show.scale;

		self.upCanvasEvent();
	},
	//滑鼠點擊事件
	upCanvasEvent: function(){
		var self = this;
		self._$canvasUp.on('mousedown', cavMouseDown);

		function cavMouseDown(e){
			var canv = this;

			//獲取到按下時，鼠標和元素的相對位置，相對偏差
			var relativeOffset = { x: e.clientX - $(canv).offset().left, y: e.clientY - $(canv).offset().top };
			$(document).on('mousemove', function(e){
				//限制移動區域
				if(countPosition().left >= self._img_show.width - self._img_show.crop_width || countPosition().left <= 0) relativeOffset.x = e.clientX - $(canv).offset().left;

				if(countPosition().top >= self._img_show.height - self._img_show.crop_height || countPosition().top<=0) relativeOffset.y = e.clientY - $(canv).offset().top;

				$(canv).css({left: countPosition().left, top: countPosition().top });//移動上層canvas

				//儲存
				self._save.left = countPosition().left / self._img_show.scale;
				self._save.top = countPosition().top / self._img_show.scale;
				self._save.width = self._img_show.crop_width / self._img_show.scale;
				self._save.height = self._img_show.crop_height / self._img_show.scale;

				//重繪剪裁區域
				self._ctxUp.drawImage(self._img, 
					self._save.left, self._save.top, self._save.width, self._save.height,
					0, 0, self._img_show.crop_width, self._img_show.crop_height
				);
				
				//設置縮放按鈕位置
				self.resizePosition();
				function countPosition(){
					var left = (e.clientX - relativeOffset.x) - self._$canvasDown.offset().left;
					var top = (e.clientY - relativeOffset.y) - self._$canvasDown.offset().top;
					return {left: left, top: top}
				}
			});
		}
	},
	addResizeBtn: function(){
		var self = this;
		//載入方向按鈕
		var $seResize =	self.setResizePoint('se-resize', self._img_show.crop_width - self._resize_point.size/2, self._img_show.crop_height - self._resize_point.size/2);
		var $swResize = self.setResizePoint('sw-resize', -self._resize_point.size/2, self._img_show.crop_height - self._resize_point.size/2);
		var $neResize = self.setResizePoint('ne-resize', self._img_show.crop_width - self._resize_point.size/2, -self._resize_point.size/2);
		var $nwResize = self.setResizePoint('nw-resize', -self._resize_point.size/2, -self._resize_point.size/2);

		var $canvas = $('<canvas class="overlay" width="' + self._img_show.crop_width  + '" height="' + self._img_show.crop_height + '"></canvas>');
		
		self._$box.append($canvas);
		self._$canvasUp = $canvas;

		self._$box.append($seResize);
		self._$box.append($swResize);
		self._$box.append($neResize);
		self._$box.append($nwResize);

		self._resize_btn.$se = $seResize;
		self._resize_btn.$sw = $swResize;
		self._resize_btn.$ne = $neResize;
		self._resize_btn.$nw = $nwResize;
	
		self.resizeEvent();
	},
	
	//绑定方向按钮事件
	resizeEvent: function(){
		var self = this;
		$('.resize-point').on('mousedown', function(){

			var pLeft = $(this).position().left + self._resize_point.size/2,
				pTop = $(this).position().top + self._resize_point.size/2;
			var upLeft = self._$canvasUp.position().left,
				upTop = self._$canvasUp.position().top;
			var noChangeX,noChangeY;
			if(upLeft >= pLeft) noChangeX = -(upLeft + self._img_show.crop_width);//負在右
			else noChangeX = upLeft;
			if(upTop >= pTop) noChangeY = -(upTop + self._img_show.crop_height);//負在下
			else noChangeY = upTop;

			$(document).on('mousemove', function(e){
				if(noChangeX >= 0 ){
					self._$canvasUp.css("left", noChangeX)
				}else{
					self._$canvasUp.css("left",  Math.abs(noChangeX) - self._img_show.crop_width);
				}
				if(noChangeY >= 0 ){
					self._$canvasUp.css("top", noChangeY)
				}else{
					self._$canvasUp.css("top",  Math.abs(noChangeY) - self._img_show.crop_height);
				}
				//阻止移動出圖片區域
				self._img_show.crop_width = Math.abs(Math.abs(noChangeX) - countPosition().left);
				self._img_show.crop_height = self._img_show.crop_width / self._option.crop_scale;
				if(noChangeX >= 0 && noChangeX + self._img_show.crop_width > self._img_show.width){
					self._img_show.crop_width = self._img_show.width - noChangeX;
					self._img_show.crop_height = self._img_show.crop_width / self._option.crop_scale;
				}else if(noChangeX < 0 && Math.abs(noChangeX) - self._img_show.crop_width < 0 ){
					self._img_show.crop_width = Math.abs(noChangeX);
					self._img_show.crop_height = self._img_show.crop_width / self._option.crop_scale;
				}
				if(noChangeY >= 0 && noChangeY + self._img_show.crop_height > self._img_show.height) {
					self._img_show.crop_height = self._img_show.height - noChangeY;
					self._img_show.crop_width = self._img_show.crop_height * self._option.crop_scale;
				}else if(noChangeY < 0 && Math.abs(noChangeY) - self._img_show.crop_height < 0){
					self._img_show.crop_height = Math.abs(noChangeY);
					self._img_show.crop_width = self._img_show.crop_height * self._option.crop_scale;
				}
				//IF 寬高小於限制
				if(self._img_show.crop_width < self._img_show.min_width){
					self._img_show.crop_width = self._img_show.min_width;
					self._img_show.crop_height = self._img_show.crop_width / self._option.crop_scale;
				}
				if(self._img_show.crop_height < self._img_show.min_height){
					self._img_show.crop_height = self._img_show.min_height;
					self._img_show.crop_width = self._img_show.crop_height / self._option.crop_scale;
				}

				//儲存
				if(noChangeX>=0){
					self._save.left = noChangeX / self._img_show.scale;
				}else{
					self._save.left = (Math.abs(noChangeX) - self._img_show.crop_width) / self._img_show.scale;
				}
				if(noChangeY>=0){
					self._save.top = noChangeY / self._img_show.scale;
				}else{
					self._save.top = (Math.abs(noChangeY) - self._img_show.crop_height) / self._img_show.scale;
				}
				self._save.width = self._img_show.crop_width / self._img_show.scale;
				self._save.height = self._img_show.crop_height / self._img_show.scale;

				//重繪剪裁區域，修改屬性寬高
				self._$canvasUp.attr("width", self._img_show.crop_width);
				self._$canvasUp.attr("height", self._img_show.crop_height);
				self._ctxUp.drawImage(self._img, 
					self._save.left, self._save.top, self._save.width, self._save.height,
					0, 0, self._img_show.crop_width, self._img_show.crop_height
				);
				self.resizePosition();
	
				function countPosition(){//游標在canvas的相對位置
					var left = e.clientX - self._$canvasDown.offset().left ;
					var top = e.clientY - self._$canvasDown.offset().top ;
					return {left: left, top: top}
				}

			});

		});
		
	},
	resizePosition: function(){
		var self = this;
		//加上寬高，減去本身大小
		self._resize_btn.$se.css({left: self._$canvasUp.position().left + self._img_show.crop_width- self._resize_point.size/2, top: self._$canvasUp.position().top + self._img_show.crop_height - self._resize_point.size/2});
		self._resize_btn.$sw.css({left: self._$canvasUp.position().left - self._resize_point.size/2, top: self._$canvasUp.position().top + self._img_show.crop_height - self._resize_point.size/2});
		self._resize_btn.$ne.css({left: self._$canvasUp.position().left + self._img_show.crop_width - self._resize_point.size/2, top: self._$canvasUp.position().top - self._resize_point.size/2});
		self._resize_btn.$nw.css({left: self._$canvasUp.position().left - self._resize_point.size/2, top: self._$canvasUp.position().top - self._resize_point.size/2});
	},
	parseInt: function(){
		this._save.width = parseInt(this._save.width);
		this._save.height = parseInt(this._save.height);
		this._save.top = parseInt(this._save.top);
		this._save.left = parseInt(this._save.left);
	},
	//保存
	save: function(){
		this.parseInt();
		var self = this;
		var $result = $("<canvas width='596' height='370'></canvas>");
		
		if($("#ipt").val()){
			$result[0].getContext('2d').drawImage(self._img, 
				self._save.left, self._save.top, self._save.width, self._save.height,
				0, 0, 596, 370
			);

			var base64Url = $result[0].toDataURL();
			saveCallBack && saveCallBack(base64Url);
			return base64Url;
		}else {
			saveCallBack && saveCallBack("noSelect");
			return "noSelect";
		}
	},
	
	
	setShowImg: function(){
		if( this._img.width <= this._option.crop_box_width && this._img.height <= this._option.crop_box_height ) {
			this._img_show.width = this._img.width;
			this._img_show.height = this._img.height;
			return;
		}

		var weight = 0;//設置權重
		if( this._img.width > this._option.crop_box_width ) weight+=10;
		if( this._img.height > this._option.crop_box_height ) weight-=10;
		if( this._img.width / this._img.height > this._option.crop_box_width / this._option.crop_box_height) weight+=5;
		else weight-=5;
		if( this._img.width >= this._img.height ) weight++;
		else weight--;

		if(weight > 0){//最滿寬度
			this._img_show.width = this._option.crop_box_width;
			this._img_show.height =  this._option.crop_box_width / ( this._img.width / this._img.height );
		}else{//最滿高度
			this._img_show.height = this._option.crop_box_height;
			this._img_show.width =  this._option.crop_box_height / ( this._img.height / this._img.width );
		}
	},

	resizeCrop: function(real){//裁剪框大小
		this._img_show.crop_width = real.width * this._img_show.scale;
		this._img_show.crop_height = real.height * this._img_show.scale;
	}


}


