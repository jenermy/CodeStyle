
function GetAppData(){
	this.dataObj = null;
	this.event = new SHEvent();
	this.changEvent = {
		type: "changEvent",
		changeType: "",
		oldDate: "",
		newDate: ""
	};
}
GetAppData.prototype.getJson = function(jsonStr){
	var self = this;
	self.changEvent.changeType = "getJsonFromApp";
	self.changEvent.oldDate = self.dataObj;
	self.dataObj = JSON.parse(jsonStr);
	self.changEvent.newDate = self.dataObj;
	
	self.event.fire(self.changEvent);
}
GetAppData.prototype.getStr = function(str){
	var self = this;
	
	self.changEvent.changeType = "getStrFromApp";
	self.changEvent.oldDate = self.dataObj;
	this.dataObj = str;
	self.changEvent.newDate = self.dataObj;
	
	self.event.fire(self.changEvent);
}

