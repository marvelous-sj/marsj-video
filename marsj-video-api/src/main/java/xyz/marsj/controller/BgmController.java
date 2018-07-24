package xyz.marsj.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xyz.marsj.pojo.Bgm;
import xyz.marsj.service.IBgmService;
import xyz.marsj.utils.JSONResult;
@Api(tags="背景音乐相关接口")
@RestController
@RequestMapping("/bgm")
public class BgmController {
	@Autowired
	private IBgmService bgmService;
	@PostMapping("/list")
	@ApiOperation(value="背景音乐列表",notes="背景音乐列表接口")
	//将用户头像上传到本地
	public JSONResult listBgm()  {
		List<Bgm> bgmList = bgmService.queryBgmList();
		return JSONResult.ok(bgmList);
	}

}
