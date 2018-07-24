package xyz.marsj.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import xyz.marsj.utils.RedisOperator;

@RestController
public class BaseController {
	@Autowired
	public RedisOperator redis;
	public static final String USER_REDIS_SESSION="user-redis-session";
	//本地根路径
	//public static final String FILE_BASE_PATH="D:/temp";   windows
	public static final String FILE_BASE_PATH="/home/marsj/image/video";
	//FFMPEGEXE路径
	//public static final String FFMPEGEXE="D:/Program Files/ffmpeg-win64-static/bin/ffmpeg.exe";  windows
	public static final String FFMPEGEXE="ffmpeg";
	//每页显示视频数
	public static final Integer PAGE_SIZE=6;
	
}
