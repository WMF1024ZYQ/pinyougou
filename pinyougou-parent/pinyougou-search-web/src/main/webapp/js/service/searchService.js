app.service("searchService",function($http){
	
	this.search=function(serachMap){
		return $http.post("itemsearch/search.do?",serachMap);
	}
});