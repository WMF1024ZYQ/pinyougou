package com.pinyougou.sellergoods.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;

	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 添加
	 */
	@Override
	public void add(TbBrand tbBrand) {
		brandMapper.insert(tbBrand);
	}
	/**
	 * 查找一个
	 */
	@Override
	public TbBrand findOne(long id) {
		return brandMapper.selectByPrimaryKey(id);
	}
	/**
	 * 修改
	 */
	@Override
	public void update(TbBrand tbBrand) {
		brandMapper.updateByPrimaryKey(tbBrand);
	}

	@Override
	public void delete(long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			brandMapper.deleteByPrimaryKey(ids[i]);
		}
	}

	@Override
	public PageResult findPage(TbBrand brand, int pageNum, int PageSize) {
		PageHelper.startPage(pageNum, PageSize);
		//添加条件
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isBlank(brand.getName())) {
			criteria.andNameLike("%"+brand.getName()+"%");
		}
		if(!StringUtils.isBlank(brand.getFirstChar())) {
			criteria.andFirstCharEqualTo(brand.getFirstChar());
		}
		//查询
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
		//返回封装结果
		return new PageResult(page.getTotal(), page.getResult());
	}
}
