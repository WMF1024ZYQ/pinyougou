package com.pinyougou.page.service.impl;

import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;
/**
 * 删除静态页面
 * @author q8691
 *
 */
@Component
public class PageDeleteListener implements MessageListener {
	
	@Autowired
	private ItemPageService itemPageService;
	
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage=(ObjectMessage)message;
		try {
			Long[] goodsIds=(Long[]) objectMessage.getObject();
			System.out.println(goodsIds);
			boolean b = itemPageService.deleteItemHtml(goodsIds);
			System.out.println("删除静态页面:"+b);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
