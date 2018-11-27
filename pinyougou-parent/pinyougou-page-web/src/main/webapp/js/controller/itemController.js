app.controller('itemController',function($scope){
	//数量加减
	$scope.addNum=function(x){
		$scope.num+=x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	//规格选中存储
	$scope.specificationItems={};
	$scope.selectSpec=function(key,value){
		$scope.specificationItems[key]=value;
		srarchSku();
	}
	//规格选中判断
	$scope.isSelected=function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}
		return false;
	}
	//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=spuList[0];
		//深克隆
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
	}
	//匹配两个对象
	matchObject=function(map1,map2){
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}
		}
		return true;
	}
	
	//根据选项选择SKU
	srarchSku=function(){
		for(var i = 0;i<spuList.length;i++){
			if(matchObject(spuList[i].spec,$scope.specificationItems)){
				$scope.sku=spuList[i];
				return;
			}
		}
	}
	//添加到购物车
	$scope.addToCart=function(){
		alert("skuid:"+$scope.sku.id);
	}
	
	
	
	
});