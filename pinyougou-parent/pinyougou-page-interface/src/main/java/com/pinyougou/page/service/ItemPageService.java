package com.pinyougou.page.service;

public interface ItemPageService {
	
	/**
	 * 生成商品详细页
	 */
	
	public boolean genItemHtml(Long goodsId);
	
	/**
	 * 删除商品详细页
	 * @param goodIds 商品id数组
	 * @return boolean
	 */
	public boolean deleteItemHtml(Long[] goodsIds);
}
