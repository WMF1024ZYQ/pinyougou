//文件上传服务层
app.service("uploadService",function($http){
	
	this.uploadFile=function(){
		var formData = new FormData();
		formData.append("file",file.files[0]);
		return $http({
			method: 'post',
			url: '../upload.do',
			data: formData,
			//设置传递参数
			headers: {'content-type':undefined},
			//序列化formData
			transformRequest: angular.identity
		});
	}
});