package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

/**
 * 生成静态页面
 * @author q8691
 *
 */
@Component
public class PageListener implements MessageListener {

	@Autowired
	private ItemPageService itemPageService;

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			String text = textMessage.getText();
			System.out.println("监听到消息:" + text);
			boolean b = itemPageService.genItemHtml(Long.parseLong(text));
			System.out.println("创建静态页面:" + b);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
