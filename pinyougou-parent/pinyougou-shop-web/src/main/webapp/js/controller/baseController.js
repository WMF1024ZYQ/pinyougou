app.controller("baseController",function($scope){
	// 分页控件配置
	$scope.paginationConf = {
		// 当前页码
		currentPage : 1,
		// 总条数
		totalItems : 10,
		// 当前页记录数
		itemsPerPage : 10,
		// 页码选择
		perPageOptions : [ 5, 10, 20, 30, 40 ],
		// 更改页面时触发
		onChange : function() {
			$scope.reloadList();// 重新加载
		}
	}
	// 页面加载时调用此方法加载
	$scope.reloadList = function() {
		//切换页码
		$scope.search($scope.paginationConf.currentPage,
				$scope.paginationConf.itemsPerPage);
	}
	
	// 获取复选框集合
	$scope.selectIds = [];
	$scope.updateSelection = function($event, id) {
		if ($event.target.checked) {
			$scope.selectIds.push(id);
		} else {
			// 获取id在集合的索引
			var idx = $scope.selectIds.indexOf(id);
			// 集合中删除此id
			$scope.selectIds.splice(idx, 1);
		}
	}
	// json转字符串
	$scope.jsonToString = function(jsonString,key) {
		var json=JSON.parse(jsonString);
		var value="";
		for(var i = 0;i<json.length;i++){
			if(i > 0){
				value+=",";
			}
			
			value+=json[i][key];
		}
		
		return value;
	}
});