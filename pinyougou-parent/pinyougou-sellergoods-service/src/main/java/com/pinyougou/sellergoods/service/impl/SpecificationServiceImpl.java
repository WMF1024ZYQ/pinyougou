package com.pinyougou.sellergoods.service.impl;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;

import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.SpecificationGroup;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(SpecificationGroup specificationGroup) {
		//获得规格对象
		TbSpecification tbSpecification = specificationGroup.getSpecification();
		//添加到数据库
		specificationMapper.insert(tbSpecification);	
		//循环添加规格表格数据到数据库
		for (TbSpecificationOption tbSpecificationOption : specificationGroup.getSpecificationOptionList()) {
			//设置关联id
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(SpecificationGroup specificationGroup){
		//获得规格对象
				TbSpecification tbSpecification = specificationGroup.getSpecification();
				//添加到数据库
				specificationMapper.updateByPrimaryKey(tbSpecification);	
				
				TbSpecificationOptionExample example = new TbSpecificationOptionExample();
				TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
				//添加查询条件
				criteria.andSpecIdEqualTo(tbSpecification.getId());
				specificationOptionMapper.deleteByExample(example);
				//循环添加规格表格数据到数据库
				for (TbSpecificationOption tbSpecificationOption : specificationGroup.getSpecificationOptionList()) {
					//设置关联id
					tbSpecificationOption.setSpecId(tbSpecification.getId());
					specificationOptionMapper.insert(tbSpecificationOption);
				}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public SpecificationGroup findOne(Long id){
		//查询规格
		SpecificationGroup specificationGroup = new SpecificationGroup();
		TbSpecification specification = specificationMapper.selectByPrimaryKey(id);
		specificationGroup.setSpecification(specification);
		//查询规格选项
		if (specification != null) {
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			//添加查询条件
			criteria.andSpecIdEqualTo(specification.getId());
			List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
			for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
				System.out.println(tbSpecificationOption);
			}
			specificationGroup.setSpecificationOptionList(specificationOptionList);
		}
		
		System.out.println(specificationGroup);
		return specificationGroup;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//删除规格
			specificationMapper.deleteByPrimaryKey(id);
			//删除规格选项
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public List<Map> findSpecList() {
			return specificationMapper.findSpecList();
		}
	
}
