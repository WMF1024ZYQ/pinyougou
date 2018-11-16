 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.parentId=0;
	$scope.save=function(){				
		var serviceObject;//服务层对象
		
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId=$scope.parentId;
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParentId($scope.entity.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.findByParentId($scope.entity.parentId)//刷新列表
					$scope.selectIds=[];
				}else {
					$scope.selectIds=[];
					alert(response.message);
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	//搜索下级目录
	$scope.findByParentId=function(parentId){
		//记录上级目录Id
		$scope.parentId=parentId;
		itemCatService.findByParentId(parentId).success(
				function(response) {
					$scope.list = response;
		});
	}
	//设置下级文件级别
	$scope.grade=1;
	$scope.setGrade=function(value){
		$scope.grade=value;
	}
	//根据文件级别展示数据
	$scope.selectList = function(entity_0) {
		if($scope.grade==1){
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		
		if($scope.grade==2){
			$scope.entity_1=entity_0;
			$scope.entity_2=null;
		}
		
		if($scope.grade==3){
			$scope.entity_2=entity_0;
		}
		
		$scope.findByParentId(entity_0.id);
	}
	
	
	
	
	
	
	
	
	
	
	
});	
