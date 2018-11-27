app.controller("serachController",function($scope,$location,searchService){
	//增加面包屑
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':'1','pageSize':'20',
			'sort':'','sortField':''}
	$scope.addSearchItem=function(key,value){
		if (key=='category' || key=='brand' || key=='price') {
			$scope.searchMap[key]=value;
		}else {
			$scope.searchMap.spec[key]=value;
		}
		
		$scope.search();//执行搜索
	}
	//移除面包屑
	$scope.removeSearchItem=function(key){
		if (key=='category' || key=='brand' || key=='price') {
			$scope.searchMap[key]='';
		}else {
			delete $scope.searchMap.spec[key];
		}
		$scope.search();//执行搜索
	}
	//搜索
	$scope.search=function(){
		$scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
		$scope.searchMap.pageSize=parseInt($scope.searchMap.pageSize);
		searchService.search($scope.searchMap).success(function(response){
			$scope.resultMap=response;
			buildPageLabel();
		});
	}
	
	//分页栏
	buildPageLabel=function(){
		$scope.pageLabel=[];
		var firstPage=1;
		var lastPage=$scope.resultMap.totalPages;
		$scope.firstDot=true;//前面有点
		$scope.lastDot=true;//后面有点
		
		if (lastPage>5) {
			if ($scope.searchMap.pageNo<=3) {
				lastPage=5;
				$scope.firstDot=false;//前面有点
			}else if ($scope.searchMap.pageNo>=lastPage-2) {
				firstPage=$scope.resultMap.totalPages-4;
				$scope.lastDot=false;
			}else {
				firstPage= $scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
			}
		}else {
			$scope.firstDot=false;//前面无点
			$scope.lastDot=false;//后边无点
		}
		//循环产生页码
		for (var i = firstPage; i <= lastPage; i++) {
			$scope.pageLabel.push(i);
		}
	}
	//页码点击
	$scope.queryByPage=function(pageNo){
		if (pageNo<1 || pageNo >$scope.resultMap.totalPages) {
			return;
		}
		
		$scope.searchMap.pageNo=pageNo;
		$scope.search();
	}
	//判断当前为第一页
	$scope.isTopPage=function(){
		if ($scope.searchMap.pageNo==1) {
			return true;
		}else {
			return false;
		}
	}
	
	//判断当前为最后一页
	$scope.isTEndPage=function(){
		if ($scope.searchMap.pageNo==$scope.resultMap.totalPages) {
			return true;
		}else {
			return false;
		}
	}
	//设置排序规则
	$scope.sortSarch=function(sort,sortField){
		$scope.searchMap.sort=sort;
		$scope.searchMap.sortField=sortField;
		$scope.search();
	}
	
	//判断关键字有没有品牌
	$scope.keywordsIsBrand=function(){
		for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
			if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0) {
				return true;
			}
		}
		return false;
	}
	
	//首页对接
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords=$location.search()['keywords'];
		$scope.search();
	}
});