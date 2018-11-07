app.controller("brandController", function($scope,$controller, brandService) {
	$controller("baseController",{$scope:$scope});
	
	$scope.findAll = function() {
		brandService.findAll().success(function(response) {
			$scope.list = response;
		});
	}

	// 分页
	$scope.findPage = function(page, size) {
		brandService.findPage(page, size).success(function(response) {
			$scope.list = response.rows;
			$scope.paginationConf.totalItems = response.total;
		});
	}

	// 添加
	$scope.save = function() {
		var object = null;
		if ($scope.entity.id != null) {
			object = brandService.update($scope.entity);
		} else {
			object = brandService.add($scope.entity);
		}

		object.success(function(response) {
			if (response.success) {
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		});
	}

	// 修改
	$scope.findOne = function(id) {
		brandService.findOne(id).success(function(response) {
			$scope.entity = response;
		});
	}
	

	// 删除
	$scope.dele = function() {
		brandService.dele($scope.selectIds).success(function(response) {
			if (response.success) {
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		});
	}
	// 定义查询条件对象
	$scope.searchEntity = {};
	// 条件查询
	$scope.search = function(page, size) {
		brandService.search(page, size, $scope.searchEntity).success(
				function(response) {
					// 总记录数添加剂分页框架内
					$scope.paginationConf.totalItems = response.total;
					// 查询的分页数据添加到集合中
					$scope.list = response.rows;
				});
	}
});