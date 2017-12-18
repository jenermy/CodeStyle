function Select(eles){
	this.center = $("body");
	this.selects = eles;
	this.options = this.selects.attr('data-options').split(",");
	this.selected = this.selects.attr('data-select');
	this.selectedBox = eles.find(".mySelected");
	this.selectedSty = 'color:#2095f2';
	this.init();
}
Select.prototype.init = function(){
	var self = this;
	self.selects.bind("click" , function(e){		
		var that = $(this);
		self.newBg();
		self.newBox();
		for(var i = 0; i < self.options.length; i++){
			console.log();
			if(self.selected == self.options[i]){
				self.newOption(self.options[i] , self.selectedSty);
			}else{
				self.newOption(self.options[i]);
			}			
		}		
	});
}
Select.prototype.newBg = function(){
	var self = this;	
	var boxHtml = '<div class="SH-select-bg" style="width:100%;height:100%;background-color:rgba(0,0,0,.7);position:fixed;left:0;top:0;font-size:40px;"></div>';
	self.center.append(boxHtml);
}
Select.prototype.newBox = function(){
	var self = this;	
	var boxHtml = '<div class="SH-select-box" style="width:65%;max-height:800px;background-color:#fff;overflow:scroll;position:fixed;left:17.5%;top:20%;border-radius:10px;"></div>';
	$(".SH-select-bg").append(boxHtml);
}
Select.prototype.newOption = function(val , selectedSty){
	var self = this;	
	var nowTime = new Date().getTime();
	selectedSty = selectedSty?selectedSty:"" ;
	var optionHtml = '<div class="SH-select-newOption" id="'+ nowTime +'" style="width:90%;height:100px;line-height:100px;border-bottom:1px solid #e7e8e9;margin-left:5%;'+ selectedSty +'">'+ val +'</div>';
	$(".SH-select-box").append(optionHtml);
	$(".SH-select-newOption").bind("click" , function(){
		self.selected = $(this).text();
		self.selects.attr('data-select' , self.selected);
		self.selectedBox.text(self.selected);
		self.hide();
	});
}
Select.prototype.hide = function(val){
	var self = this;	
	$(".SH-select-bg").remove();
}
var s = new Select($(".mySelect"));