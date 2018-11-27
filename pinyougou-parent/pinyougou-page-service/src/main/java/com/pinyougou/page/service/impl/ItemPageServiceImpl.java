package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.WriteAbortedException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
@Service
public class ItemPageServiceImpl implements ItemPageService {
	
	@Value("${pagedir}")
	private String pagedir;
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbItemMapper itemMapper;
	

	@Override
	public boolean genItemHtml(Long goodsId) {
		try {
			//创建配置类
			Configuration configuration = freeMarkerConfig.getConfiguration();
			//读取模板
			Template template = configuration.getTemplate("item.ftl");
			//查询数据添加进Map集合
			Map map = new HashMap<>();
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			map.put("goods", goods);
			map.put("goodsDesc", goodsDesc);
			//商品分类
			String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			map.put("itemCat1", itemCat1);
			map.put("itemCat2", itemCat2);
			map.put("itemCat3", itemCat3);
			//查询sku表
			TbItemExample example=new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goodsId);
			criteria.andStatusEqualTo("1");
			example.setOrderByClause("is_default desc");
			List<TbItem> itemList = itemMapper.selectByExample(example);
			map.put("itemList", itemList);
			//输出路径
			Writer writer = new FileWriter(pagedir+goodsId+".html");
			//开始合并
			template.process(map, writer);
			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}


	@Override
	public boolean deleteItemHtml(Long[] goodIds) {
		try {
			for (Long goodsId : goodIds) {
				System.out.println(goodsId);
				new File(pagedir + goodsId + ".html").delete();
			} 
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
	}
}
