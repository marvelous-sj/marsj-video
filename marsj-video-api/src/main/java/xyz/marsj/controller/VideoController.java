package xyz.marsj.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import xyz.marsj.enums.VideoStatusEnum;
import xyz.marsj.pojo.Bgm;
import xyz.marsj.pojo.Comments;
import xyz.marsj.pojo.Videos;
import xyz.marsj.service.IBgmService;
import xyz.marsj.service.IVideoService;
import xyz.marsj.utils.FetchVideoCover;
import xyz.marsj.utils.JSONResult;
import xyz.marsj.utils.MergeMP4BGM;
import xyz.marsj.utils.PagedResult;
@Api(tags="短视频相关接口")
@RestController
@RequestMapping("/video")
public class VideoController extends BaseController{
	@Autowired
	private IVideoService videoService;
	@Autowired
	private IBgmService bgmService;
	@PostMapping(value="/upload",headers="content-type=multipart/form-data")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="form"),
		@ApiImplicitParam(name="bgmId",value="BGMId",required=false,dataType="String",paramType="form"),
		@ApiImplicitParam(name="videoSeconds",value="短视频时间",required=true,dataType="double",paramType="form"),
		@ApiImplicitParam(name="videoWidth",value="短视频宽度",required=true,dataType="int",paramType="form"),
		@ApiImplicitParam(name="videoHeight",value="短视频高度",required=true,dataType="int",paramType="form"),
		@ApiImplicitParam(name="desc",value="短视频描述",required=false,dataType="String",paramType="form")
	})
	@ApiOperation(value="用户视频上传",notes="用户视频上传接口")
	public JSONResult upload(String userId,String bgmId,double videoSeconds,int videoWidth,int videoHeight
			,String desc,@ApiParam(value="短视频",required=true)MultipartFile file) throws IOException {
			if(StringUtils.isBlank(userId)) {
				return JSONResult.errorMsg("用户Id不能为空");
			}
		
		//视频数据库路径
		String videoPathDB="/"+userId+"/video";
		//封面数据库路径
		String coverPathDB="/"+userId+"/video/cover";
		FileOutputStream fileOutPutStream=null;
		InputStream inputStream=null;
		String finalVideoPath="";
		String finalCoverPath="";
		//判断上传文件是否为空
		try {
			if(file!=null) {
			String filename = file.getOriginalFilename();
			if(StringUtils.isNotBlank(filename)) {
	
				//视频数据库保存路径
				videoPathDB+="/"+filename;
				//封面数据库保存路径
				coverPathDB+="/"+UUID.randomUUID().toString()+".jpg";
				//最终保存路径
				finalVideoPath=FILE_BASE_PATH+videoPathDB;
				finalCoverPath=FILE_BASE_PATH+coverPathDB;
				File videoFile=new File(finalVideoPath);
				File coverFile=new File(finalCoverPath);
				if(!coverFile.getParentFile().exists()) {
					coverFile.getParentFile().mkdirs();
				}
				fileOutPutStream=new FileOutputStream(videoFile);
				inputStream=file.getInputStream();
				IOUtils.copy(inputStream, fileOutPutStream);
				}else {
					return JSONResult.errorMsg("文件上传出错");
				}
			}else {
				return JSONResult.errorMsg("文件上传出错");
			}
		} catch (Exception e) {
			return JSONResult.errorMsg("文件上传出错");
		} finally {
			if(fileOutPutStream!=null) {
				fileOutPutStream.flush();
				fileOutPutStream.close();
			}
		}
		//判断是否选择了bgm
		if(!StringUtils.isBlank(bgmId)) {
			//不为空的话则合并视频和bgm
			Bgm bgmInfo = bgmService.queryBgmById(bgmId);
			String bgmPath = bgmInfo.getPath();
			bgmPath=FILE_BASE_PATH+bgmPath;
			MergeMP4BGM tool = new MergeMP4BGM(FFMPEGEXE);
			String videoInputPath=finalVideoPath;
			String videoOutputName = UUID.randomUUID().toString() + ".mp4";
			videoPathDB="/"+userId+"/video/"+videoOutputName;
			finalVideoPath=FILE_BASE_PATH+videoPathDB;
			try {
				tool.convertor(videoInputPath, bgmPath, videoSeconds, finalVideoPath);
			} catch (Exception e) {
				return JSONResult.errorMsg("文件上传出错");
			}
		}
		
		FetchVideoCover cover = new FetchVideoCover(FFMPEGEXE);
		try {
			cover.convertor(finalVideoPath,finalCoverPath);
		} catch (Exception e) {
			return JSONResult.errorMsg("文件上传出错");
		}
	
		//保存视频文件到数据库
		Videos video=new Videos();
		video.setUserId(userId);
		video.setVideoDesc(desc);
		video.setVideoHeight(videoHeight);
		video.setVideoWidth(videoWidth);
		video.setVideoSeconds((float) videoSeconds);
		video.setCreateTime(new Date());
		video.setVideoPath(videoPathDB);
		video.setAudioId(bgmId);
		video.setStatus(VideoStatusEnum.SUCCESS.value);
		video.setLikeCounts(0L);
		video.setCoverPath(coverPathDB);
		String VideosId= videoService.saveVideo(video);
		
		return JSONResult.ok(VideosId);
	}
	
	
	@PostMapping(value="/uploadCover",headers="content-type=multipart/form-data")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="form"),
		@ApiImplicitParam(name="videoId",value="视频Id",required=true,dataType="String",paramType="form"),
	})
	@ApiOperation(value="用户视频封面上传",notes="用户视频封面上传接口")
	public JSONResult uploadCover(String userId,String videoId,@ApiParam(value="视频封面",required=true)MultipartFile file) throws IOException {
			if(StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
				return JSONResult.errorMsg("用户Id和视频Id不能为空");
			}
		
		//数据库路径
		String coverPathDB="/"+userId+"/video";
		FileOutputStream fileOutPutStream=null;
		InputStream inputStream=null;
		String finalCoverPath="";
		//判断上传文件是否为空
		try {
			if(file!=null) {
			String filename = file.getOriginalFilename();
			if(StringUtils.isNotBlank(filename)) {
				//最终保存路径
				finalCoverPath=FILE_BASE_PATH+coverPathDB+"/"+filename;
				//数据库保存路径
				coverPathDB+="/"+filename;
				File faceFile=new File(finalCoverPath);
				//若路径不存在，则创建
				if(!faceFile.getParentFile().exists()) {
					faceFile.getParentFile().mkdirs();
				}
				fileOutPutStream=new FileOutputStream(faceFile);
				inputStream=file.getInputStream();
				IOUtils.copy(inputStream, fileOutPutStream);
				}else {
					return JSONResult.errorMsg("文件上传出错");
				}
			}else {
				return JSONResult.errorMsg("文件上传出错");
			}
		} catch (Exception e) {
			return JSONResult.errorMsg("文件上传出错");
		} finally {
			if(fileOutPutStream!=null) {
				fileOutPutStream.flush();
				fileOutPutStream.close();
			}
		}
		Videos video=new Videos();
		video.setId(videoId);
		video.setCoverPath(coverPathDB);
		int effectedNum = videoService.addVideoCover(video);
		if(effectedNum!=1) {
			return JSONResult.errorMsg("文件上传出错!");
		}
		return JSONResult.ok();
	}

	@PostMapping(value="/hotword")
	@ApiOperation(value="热搜词",notes="热搜词接口")
	public JSONResult hotword() {
		List<String> hotWords = videoService.selectAllHotWords();
		return JSONResult.ok(hotWords);
	}

	@PostMapping(value="/videolist")
	@ApiImplicitParams({
		@ApiImplicitParam(name="page",value="查询页数",required=false,dataType="Integer",paramType="query"),
		@ApiImplicitParam(name="video",value="查询视频的条件",required=false,dataType="video",paramType="from"),
		@ApiImplicitParam(name="isSaveRecord",value="是否保存搜索词",required=false,dataType="Integer",paramType="query")
	})
	@ApiOperation(value="查询视频列表",notes="查询视频列表接口")
	public JSONResult videolist(@RequestBody Videos video,Integer isSaveRecord,	Integer page) {
		
		if(page==null) {
			page=1;
		}
		if(isSaveRecord==null) {
			isSaveRecord=0;
		}
		PagedResult result = videoService.selectAllVideo(video,isSaveRecord,page, PAGE_SIZE);
		return JSONResult.ok(result);
	}
	@PostMapping(value="/userlike")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query"),
		@ApiImplicitParam(name="videoId",value="视频Id",required=true,dataType="String",paramType="query"),
		@ApiImplicitParam(name="videoCreatorId",value="视频作者Id",required=true,dataType="String",paramType="query")
	})
	@ApiOperation(value="用户关注视频",notes="用户关注视频接口")
	public JSONResult userlike(String userId,String videoId,String videoCreatorId) {
		if(StringUtils.isBlank(userId)||StringUtils.isBlank(videoId)||StringUtils.isBlank(videoCreatorId)) {
			return JSONResult.errorMsg("Id不能为空");
		}
		videoService.isLikeVideo(userId, videoId, videoCreatorId);
		return JSONResult.ok();
	}
	@PostMapping(value="/userunlike")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query"),
		@ApiImplicitParam(name="videoId",value="视频Id",required=true,dataType="String",paramType="query"),
		@ApiImplicitParam(name="videoCreatorId",value="视频作者Id",required=true,dataType="String",paramType="query")
	})
	@ApiOperation(value="用户取关视频",notes="用户取关视频接口")
	public JSONResult hotwuserunlikeord(String userId,String videoId,String videoCreatorId) {
		if(StringUtils.isBlank(userId)||StringUtils.isBlank(videoId)||StringUtils.isBlank(videoCreatorId)) {
			return JSONResult.errorMsg("Id不能为空");
		}
		videoService.isUnLikeVideo(userId, videoId, videoCreatorId);
		return JSONResult.ok();
	}
	
	@PostMapping(value="/showmylike")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query"),
		@ApiImplicitParam(name="page",value="查询页数",required=false,dataType="Integer",paramType="query"),
		
	})
	@ApiOperation(value="查询用户喜欢视频列表",notes="查询用户喜欢视频列表接口")
	public JSONResult showmylike(String userId,	Integer page) {
		if(StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("Id不能为空");
		}
		if(page==null) {
			page=1;
		}
		PagedResult result = videoService.queryUserLikeVideo(userId, page, PAGE_SIZE);
		return JSONResult.ok(result);
	}
	
	
	@PostMapping(value="/showmyfollow")
	@ApiImplicitParams({
		@ApiImplicitParam(name="fanId",value="粉丝Id",required=true,dataType="String",paramType="query"),
		@ApiImplicitParam(name="page",value="查询页数",required=false,dataType="Integer",paramType="query")
	})
	@ApiOperation(value="查询用户关注视频列表",notes="查询用户关注视频列表接口")
	public JSONResult showmyfollow(String fanId,Integer page) {
		if(StringUtils.isBlank(fanId)) {
			return JSONResult.errorMsg("Id不能为空");
		}
		if(page==null) {
			page=1;
		}
		PagedResult result = videoService.queryUserFollowVideo(fanId, page, PAGE_SIZE);
		return JSONResult.ok(result);
	}
	
	@PostMapping(value="/savecomment")
	@ApiOperation(value="保存留言",notes="保存留言接口")
	public JSONResult savecomment(@RequestBody Comments comments) {
		 videoService.saveComment(comments);
		return JSONResult.ok();
	}
	@PostMapping(value="/getvideocomments")
	@ApiImplicitParams({
		@ApiImplicitParam(name="videoId",value="视频Id",required=true,dataType="String",paramType="query"),
		@ApiImplicitParam(name="page",value="查询页数",required=false,dataType="Integer",paramType="query")
	})
	@ApiOperation(value="获取留言信息",notes="获取留言信息接口")
	public JSONResult getvideocomments(String videoId,Integer page) {
		if(StringUtils.isBlank(videoId)) {
			return JSONResult.errorMsg("Id不能为空");
		}
		if(page==null) {
			page=1;
		}
		PagedResult result = videoService.getCommentsList(videoId, page, PAGE_SIZE);
		return JSONResult.ok(result);
	}
}
