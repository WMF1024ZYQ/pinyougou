package com.pinyougou.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
public class UploadController {

	@Value("${FILE_SERVER_URL}")
	private String file_server_uer;

	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {
		// 获取上传的文件名
		String filename = file.getOriginalFilename();
		// 获取文件扩展名
		String extName = filename.substring(filename.lastIndexOf(".") + 1);
		try {
			// 调用上传工具类
			FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
			//得到文件的path
			String path = client.uploadFile(file.getBytes(), extName);
			//得到文件的url
			String url = file_server_uer + path;
			System.out.println(url);
			//返回url
			return new Result(true,url);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"上传失败");
		}
	}
}
