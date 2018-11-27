app.controller('contentController',function($scope,contentService){
	
	//广告集合
	$scope.contentList=[];
	$scope.findContentById=function(categoryId){
		contentService.findContentById(categoryId).success(function(response) {
			$scope.contentList[categoryId]=response;
		});
	}
	//搜索
	$scope.search=function(){
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
});