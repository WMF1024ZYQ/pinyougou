 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location   ,goodsService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	// 查询实体
	$scope.findOne = function (id) {
		var id = $location.search()['id'];// 获取url参数值
// alert(id);
		if(id==null){
			return;
		}
		
		goodsService.findOne(id).success(function (response) {
			$scope.entity = response;
			editor.html($scope.entity.goodsDescs.introduction);// 富文本
			$scope.entity.goodsDescs.itemImages=JSON.parse($scope.entity.goodsDescs.itemImages);//图片
			$scope.entity.goodsDescs.customAttributeItems=JSON.parse($scope.entity.goodsDescs.customAttributeItems);//扩展属性
			$scope.entity.goodsDescs.specificationItems=JSON.parse($scope.entity.goodsDescs.specificationItems);//规格
			
			for (var i = 0; i < $scope.entity.itemsList.length; i++) {
				$scope.entity.itemsList[i].spec=JSON.parse($scope.entity.itemsList[i].spec);
			}
		});
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	// 状态显示
	$scope.status=['未审核','已审核','审核未通过','已驳回'];
	
	// 查询商品分类列表
	$scope.itemCatList=[];
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(function(response){
			for (var i = 0; i < response.length; i++) {
				$scope.itemCatList[response[i].id]=response[i].name;
			}
		});
	}
	
	// 读取一级分类
	$scope.selectItemCat1List = function () {
		itemCatService.findByParentId(0).success(function (response) {
			$scope.ItemCat1List = response;
		});
	}
	// 读取二级分类
	$scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {
		if (newValue != null) {
			itemCatService.findByParentId(newValue).success(function (response) {
				$scope.ItemCat2List = response;
			});
		}
	});

	// 读取三级分类
	$scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {
		if (newValue != null) {
			itemCatService.findByParentId(newValue).success(function (response) {
				$scope.ItemCat3List = response;
			});
		}
	});
	// 读取模板id
	$scope.$watch("entity.goods.category3Id", function (newValue, oldValue) {
		if (newValue != null) {
			itemCatService.findOne(newValue).success(function (response) {
				$scope.entity.goods.typeTemplateId = response.typeId;
			});
		}
	});

	// 读取品牌
	$scope.$watch("entity.goods.typeTemplateId", function (newValue, oldValue) {
		if (newValue != null) {
			typeTemplateService.findOne(newValue).success(function (response) {
				$scope.typeTemplate = response;
				$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
				if($location.search()['id']==null){
					$scope.entity.goodsDescs.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
				}
			});

			typeTemplateService.findSpecList(newValue).success(function (response) {
				$scope.specList = response;
			})
		}
	});
	//规格检查
	$scope.checkAttribute=function(specName,optionName){
		var items=$scope.entity.goodsDescs.specificationItems;
		var object=$scope.serachByObjectKey(items,"attributeName",specName);
		if(object != null) {
			if(object.attributeValue.indexOf(optionName) >= 0){
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
    
	//变更审核状态
	$scope.updateStatus=function(status){
		goodsService.updateStatus($scope.selectIds,status).success(function(response){
			if(response.success){
				$scope.reloadList();
				$scope.selectIds=[];
			}else {
				alert(response.message);
			}
		})
	}
});	
