package com.pinyougou.portal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;

@RestController
@RequestMapping("/content")
public class ContentController {
	
	@Reference
	private ContentService contentService;
	
	@RequestMapping("/findContentById")
	public List<TbContent> findContentById(Long categoryId) {
		return contentService.findContentById(categoryId);
	}
}
