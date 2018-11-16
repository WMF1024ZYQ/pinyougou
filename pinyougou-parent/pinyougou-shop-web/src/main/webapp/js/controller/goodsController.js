//控制层 
app.controller('goodsController', function ($scope, $controller,$location, goodsService,
	uploadService, itemCatService, typeTemplateService) {

	$controller('baseController', {
		$scope: $scope
	});// 继承

	// 读取列表数据绑定到表单中
	$scope.findAll = function () {
		goodsService.findAll().success(function (response) {
			$scope.list = response;
		});
	}

	// 分页
	$scope.findPage = function (page, rows) {
		goodsService.findPage(page, rows).success(function (response) {
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;// 更新总记录数
		});
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

	/*// 保存
	$scope.add = function () {
		// 富本文
		$scope.entity.goodsDescs.introduction = editor.html();
		goodsService.add($scope.entity).success(function (response) {
			if (response.success) {
				alert(response.message);
				$scope.entity = {};
				editor.html('');
			} else {
				alert(response.message);
			}
		});
	}*/
	
	//保存 
	$scope.save=function(){	
		// 富本文
		$scope.entity.goodsDescs.introduction = editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert(response.message);
					location.href="goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}

	// 批量删除
	$scope.dele = function () {
		// 获取选中的复选框
		goodsService.dele($scope.selectIds).success(function (response) {
			if (response.success) {
				$scope.reloadList();// 刷新列表
				$scope.selectIds = [];
			}
		});
	}

	$scope.searchEntity = {};// 定义搜索对象

	// 搜索
	$scope.search = function (page, rows) {
		goodsService.search(page, rows, $scope.searchEntity).success(
			function (response) {
				$scope.list = response.rows;
				$scope.paginationConf.totalItems = response.total;// 更新总记录数
			});
	}
	// 文件上传
	$scope.uploadFile = function () {

		uploadService.uploadFile().success(function (response) {
			if (response.success) {
				$scope.image_entity.url = response.message;
			} else {
				alert(response.message);
			}
		});
	}
	// 添加图片到列表集合
	$scope.entity = { goods: {}, goodsDescs: { itemImages: [], specificationItems: [] } }
	$scope.add_image_entity = function () {
		$scope.entity.goodsDescs.itemImages.push($scope.image_entity);
	}
	// 清空新增属性
	$scope.deleSrc = function () {
		$scope.image_entity = {};
		$scope.image_entity.url = "http://192.168.25.133/group1/M00/00/00/wKgZhVvqdcSAdIt4AACHGowigMc314.jpg";
	}
	// 从列表集合删除图片
	$scope.dele_image_entity = function (index) {
		$scope.entity.goodsDescs.itemImages.splice(index, 1);
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
	// 添加数据进对象
	$scope.updateAttribute = function ($event, name, value) {
		// 查询对象是否存在
		var object = $scope.serachByObjectKey($scope.entity.goodsDescs.specificationItems, "attributeName", name);
		if (object != null) {
			// 存在就给内集合添加值
			if ($event.target.checked) {
				object.attributeValue.push(value);
			} else {
				object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
				if (object.attributeValue.length == 0) {
					$scope.entity.goodsDescs.specificationItems.splice(
						$scope.entity.goodsDescs.specificationItems.indexOf(object), 1);
				}
			}
		} else {// 不存在就给创建内集合并添加值
			$scope.entity.goodsDescs.specificationItems.push({ "attributeName": name, "attributeValue": [value] });
		}
	}
	// 创建初始SPU列表
	$scope.CreateNewList= function() {
		
		$scope.entity.itemsList=[{ spec: {}, price: 0, num: 99999, status: '0', isDefault: '0' }];
		// 创建一个数据集合
		var items = $scope.entity.goodsDescs.specificationItems;
		// 遍历数据集合
		for (var i = 0; i < items.length; i++) {
			// 创建初始SPU列表
			$scope.entity.itemsList=addColumn($scope.entity.itemsList,items[i].attributeName,items[i].attributeValue);
		}
	}
	
	addColumn=function(list,columnName,columnValue){
		// 定义一个集合
		var newList=[];
		for (var i = 0; i < list.length; i++) {
			// 遍历值集合添加进对象
			for (var j = 0; j < columnValue.length; j++) {
				// 克隆行对象
				var newRow =JSON.parse(JSON.stringify(list[i]));
				// 给对象添加键值对属性
				newRow.spec[columnName]=columnValue[j];
				// 对象添加进集合
				newList.push(newRow);
			}
		}
		// 返回更新后的集合
		return newList;
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
	
	//检查规格
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
	
	
	
	
	
	
	
	
	
	
	
});
