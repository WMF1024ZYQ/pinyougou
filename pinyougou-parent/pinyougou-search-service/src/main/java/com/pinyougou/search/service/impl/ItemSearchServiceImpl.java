package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public Map<String, Object> search(Map serachMap) {
		Map<String, Object> map = new HashMap<>();
		// 1.按关键字查询页面（高亮显示）
		map.putAll(searchList(serachMap));
		// 2.根据关键字查询商品分类选项
		List<String> categoryList = searchCategoryList(serachMap);
		map.put("categoryList", categoryList);
		// 3.查询品牌和规格列表选项
		String categoryName = (String) serachMap.get("category");
		//商品分类条件为空,则默认查询第一个商品分类
		if ("".equals(categoryName)) {
			if (categoryList.size() > 0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}//不为空则使用条件的
		}else {
			map.putAll(searchBrandAndSpecList(categoryName));
		}
		
		return map;
	}
	
	/**
	 * 高亮查询
	 * @param serachMap 查询条件
	 * @return	返回页面显示结果
	 */
	private Map searchList(Map serachMap) {
		Map map = new HashMap<>();
		//空格处理
		String keywords = (String) serachMap.get("keywords");
		serachMap.put("keywords", keywords.replace(" ", ""));
		
		HighlightQuery query = new SimpleHighlightQuery();
		// 设置高亮区域
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		// 设置高亮前缀
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		// 设置高亮后缀
		highlightOptions.setSimplePostfix("</em>");
		// 设置高亮选项
		query.setHighlightOptions(highlightOptions);
		// 关键字查询
		Criteria criteria = new Criteria("item_keywords").is(serachMap.get("keywords"));
		query.addCriteria(criteria);
		//1.1 分类过滤查询
		if (!"".equals(serachMap.get("category"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_category").is(serachMap.get("category"));
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//1.2品牌过滤查询
		if (!"".equals(serachMap.get("brand"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_brand").is(serachMap.get("brand"));
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//1.3规格过滤查询
		if(serachMap.get("spec")!=null) {
			Map<String, String> specMap = (Map<String, String>) serachMap.get("spec");
			for (String key : specMap.keySet()) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		//1.4价格过滤
		if (!"".equals(serachMap.get("price"))) {
			String[] price = ((String)serachMap.get("price")).split("-");
			if (!"0".equals(price[0])) {//价格不等于0(变相大于0)
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
			if (!"*".equals(price[1])) {//价格不等于最大值(变相小于*)
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		//1.5分页查询
		Integer pageNo=(Integer) serachMap.get("pageNo");
		if (pageNo==null) {
			pageNo=1;
		}
		
		Integer pageSize=(Integer) serachMap.get("pageSize");
		if (pageSize==null) {
			pageSize=20;
		}
		query.setOffset(pageNo);
		query.setRows(pageSize);
		
		//1.6排序
		String sortValue =(String) serachMap.get("sort");
		String sortField =(String) serachMap.get("sortField");
		if (sortValue!=null && !sortValue.equals("")) {
			if (sortValue.equals("ASC")) {
				Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
				query.addSort(sort);
//				System.out.println("1111");
			}
			
			if (sortValue.equals("DESC")) {
				Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
				query.addSort(sort);
//				System.out.println("222");
			}
			
		}
		
		//高亮显示处理
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		// 循环高亮入口集合
		for (HighlightEntry<TbItem> h : page.getHighlighted()) {
			// 获取原实体类
			TbItem item = h.getEntity();
			// 如果条件参数不为空
			if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
				// 设置关键字高亮
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
			}
		}
		map.put("rows", page.getContent());
		map.put("totalPages", page.getTotalPages());
		map.put("total", page.getTotalElements());
		return map;

	}
	/**
	 * 分组查询商品分类
	 * @param serachMap 查询条件对象集合
	 * @return
	 */
	private List<String> searchCategoryList(Map serachMap) {
		List<String> list = new ArrayList<>();
		Query query = new SimpleQuery("*:*");
		// 按照关键字查询
		Criteria criteria = new Criteria("item_keywords").is(serachMap.get("keywords"));
		query.addCriteria(criteria);
		// 设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		// 得到分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		// 得到分组页结果
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		// 得到分组页入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		// 得到分组页入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for (GroupEntry<TbItem> groupEntry : content) {
			list.add(groupEntry.getGroupValue());
		}

		return list;
	}

	/**
	 * 从缓存查询品牌和规格列表
	 * 
	 * @param category
	 *            商品分类
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap<>();
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		if (typeId != null) {
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList", brandList);
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList", specList);
		}
		return map;
	}
	
	/**
	 * 添加solr索引
	 * @param list
	 */
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}
	
	/**
	 * 删除solr索引
	 */
	@Override
	public void deleByGoodsIds(List goodsIds) {
		Query query= new SimpleQuery("*:*");
		Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
		query.addCriteria(criteria );
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
}
