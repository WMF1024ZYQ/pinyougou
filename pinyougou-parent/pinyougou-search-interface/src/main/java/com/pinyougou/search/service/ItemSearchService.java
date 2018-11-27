package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
	
	public Map<String, Object> search(Map serachMap);
	
	public void importList(List itemList);
	
	public void deleByGoodsIds(List goodsIds);
}
