package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

public class GoodsGroup implements Serializable{
	
	private TbGoods goods;	//商品表
	private TbGoodsDesc goodsDescs;	//商品扩展表
	private List<TbItem> itemsList;
	public GoodsGroup() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TbGoods getGoods() {
		return goods;
	}
	public void setGoods(TbGoods goods) {
		this.goods = goods;
	}
	public TbGoodsDesc getGoodsDescs() {
		return goodsDescs;
	}
	public void setGoodsDescs(TbGoodsDesc goodsDescs) {
		this.goodsDescs = goodsDescs;
	}
	public List<TbItem> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<TbItem> itemList) {
		this.itemsList = itemList;
	}
	@Override
	public String toString() {
		return "GoodsGroup [goods=" + goods + ", goodsDescs=" + goodsDescs + ", itemList=" + itemsList + "]";
	}
	
	
}
