package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.GoodsGroup;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(GoodsGroup goodsGroup) {
		TbGoods goods = goodsGroup.getGoods();
		goods.setAuditStatus("0");
		goodsMapper.insert(goods);
		
		TbGoodsDesc goodsDescs = goodsGroup.getGoodsDescs();
		goodsDescs.setGoodsId(goods.getId());
		goodsDescMapper.insert(goodsDescs);
		
		addItem(goodsGroup, goods, goodsDescs);
	}

	public void setItemAttribute(TbItem item, TbGoodsDesc goodsDescs, TbGoods goods) {
		// 商品图片
		List<Map> images = JSON.parseArray(goodsDescs.getItemImages(), Map.class);
		if (images.size() > 0) {
			item.setImage((String) images.get(0).get("url"));
		}

		// 商品类别id
		item.setCategoryid(goods.getCategory3Id());
		// 商品时间
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		// goods的id
		item.setGoodsId(goods.getId());
		// 商家Id
		item.setSellerId(goods.getSellerId());
		// 商品类别
		item.setCategory(itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName());
		// 品牌
		item.setBrand(brandMapper.selectByPrimaryKey(goods.getBrandId()).getName());
		// 商家名称
		item.setSeller(sellerMapper.selectByPrimaryKey(goods.getSellerId()).getNickName());
		itemMapper.insert(item);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(GoodsGroup goodsGroup) {
		goodsMapper.updateByPrimaryKey(goodsGroup.getGoods());
		goodsDescMapper.updateByPrimaryKey(goodsGroup.getGoodsDescs());
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goodsGroup.getGoods().getId());
		itemMapper.deleteByExample(example);
		addItem(goodsGroup, goodsGroup.getGoods(), goodsGroup.getGoodsDescs());;
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public GoodsGroup findOne(Long id) {
		GoodsGroup goodsGroup = new GoodsGroup();
		TbGoods goods = goodsMapper.selectByPrimaryKey(id);
		goodsGroup.setGoods(goods);
		TbGoodsDesc goodsDescs = goodsDescMapper.selectByPrimaryKey(id);
		goodsGroup.setGoodsDescs(goodsDescs);

		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> itemsList = itemMapper.selectByExample(example);
		goodsGroup.setItemsList(itemsList);
		return goodsGroup;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
		}
	}

	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		if (goods != null) {
			if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
				// criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}
			if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
				criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
			}
			if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}
			if (goods.getCaption() != null && goods.getCaption().length() > 0) {
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}
			if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}
			if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}
			criteria.andIsDeleteIsNull();

		}

		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	public void addItem(GoodsGroup goodsGroup, TbGoods goods, TbGoodsDesc goodsDescs) {
		// 开启了规格
		if ("1".equals(goods.getIsEnableSpec())) {
			for (TbItem item : goodsGroup.getItemsList()) {

				// 设置标题
				String titile = goods.getGoodsName();
				Map<String, Object> specMap = JSON.parseObject((item.getSpec()));
				for (String key : specMap.keySet()) {
					titile += " " + specMap.get(key);
				}
				item.setTitle(titile);

				setItemAttribute(item, goodsDescs, goods);
			}
		} else {// 默认规格
			TbItem item = new TbItem();
			item.setTitle(goods.getGoodsName());
			item.setPrice(goods.getPrice());
			item.setNum(99999);
			item.setStatus("1");
			item.setIsDefault("1");
			item.setSpec("{}");
			setItemAttribute(item, goodsDescs, goods);
		}

	}
	/**
	 * 修改审核状态
	 */
	@Override
	public void updateStatus(Long[] ids, String status) {
		for (int i = 0; i < ids.length; i++) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(ids[i]);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}
	}

	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status) {
		TbItemExample example=new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(ids));
		criteria.andStatusEqualTo(status);
		return itemMapper.selectByExample(example);
	}
}
