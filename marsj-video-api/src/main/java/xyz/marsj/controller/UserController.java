package xyz.marsj.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import xyz.marsj.pojo.Users;
import xyz.marsj.pojo.UsersReport;
import xyz.marsj.pojo.vo.PublisherVideo;
import xyz.marsj.pojo.vo.UsersVO;
import xyz.marsj.service.IUserService;
import xyz.marsj.utils.JSONResult;
@Api(tags="用户信息相关接口")
@RestController
@RequestMapping("/user")
public class UserController extends BaseController{
	@Autowired
	private IUserService userService;
	@PostMapping("/uploadface")
	@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query")
	@ApiOperation(value="用户头像上传",notes="用户头像上传接口")
	//将用户头像上传到本地
	public JSONResult uploadface(String userId,@RequestParam("file")MultipartFile[] files) throws IOException {
			if(StringUtils.isBlank(userId)) {
				return JSONResult.errorMsg("用户Id不能为空");
			}
		//数据库路径
		String facePathDB="/"+userId+"/face";
		FileOutputStream fileOutPutStream=null;
		InputStream inputStream=null;
		//判断上传文件是否为空
		try {
			if(files!=null&&files.length>0) {
			String filename = files[0].getOriginalFilename();
			if(StringUtils.isNotBlank(filename)) {
				//最终保存路径
				String finalFacePath=FILE_BASE_PATH+facePathDB+"/"+filename;
				//数据库保存路径
				facePathDB+="/"+filename;
				File faceFile=new File(finalFacePath);
				//若路径不存在，则创建
				if(!faceFile.getParentFile().exists()) {
					faceFile.getParentFile().mkdirs();
				}
				fileOutPutStream=new FileOutputStream(faceFile);
				inputStream=files[0].getInputStream();
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
		Users user=new Users();
		user.setId(userId);
		user.setFaceImage(facePathDB);
		int effectNum = userService.updataUserInfo(user);
		if(effectNum!=1) {
			return JSONResult.errorMsg("文件上传出错");
		}
		return JSONResult.ok(facePathDB);
	}
	@PostMapping("/query")
	@ApiImplicitParams({
	@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query"),
	@ApiImplicitParam(name="fanId",value="粉丝Id",required=false,dataType="String",paramType="query")
	})
	@ApiOperation(value="用户信息查询",notes="用户信息查询接口")
	public JSONResult query(String userId,String fanId){
			if(StringUtils.isBlank(userId)) {
				return JSONResult.errorMsg("用户Id不能为空");
			}
		Users user = userService.queryUserInfo(userId);
		UsersVO userOV=new UsersVO();
		BeanUtils.copyProperties(user, userOV);
		boolean userIsFollow = userService.userIsFollow(userId, fanId);
		userOV.setFollow(userIsFollow);
 		return JSONResult.ok(userOV);
	}
	
	@PostMapping("/querypublisher")
	@ApiImplicitParams({
	@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query"),
	@ApiImplicitParam(name="videoId",value="视频Id",required=true,dataType="String",paramType="query"),
	@ApiImplicitParam(name="publishUserId",value="视频作者Id",required=true,dataType="String",paramType="query")
	})
	@ApiOperation(value="视频作者信息查询",notes="视频作者信息查询接口")
	public JSONResult querypublisher(String userId,String videoId,String publishUserId){
			if(StringUtils.isBlank(publishUserId)) {
				return JSONResult.errorMsg("用户Id不能为空");
			}
		Users user = userService.queryUserInfo(publishUserId);
		UsersVO userOV=new UsersVO();
		BeanUtils.copyProperties(user, userOV);
		//查询用户是否点赞
		boolean likeVideo = userService.isLikeVideo(userId, videoId);
		PublisherVideo bean=new PublisherVideo();
		bean.setLikeVideo(likeVideo);
		bean.setPublisher(userOV);
 		return JSONResult.ok(bean);
	}
	@PostMapping("/beyourfan")
	@ApiImplicitParams({
	@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query"),
	@ApiImplicitParam(name="fanId",value="粉丝Id",required=true,dataType="String",paramType="query"),
	})
	@ApiOperation(value="关注用户",notes="关注用户接口")
	public JSONResult beyourfan(String userId,String fanId){
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
			return JSONResult.errorMsg("");
		}
		userService.saveFanRelation(userId, fanId);
 		return JSONResult.ok("关注成功");
	}
	
	@PostMapping("/dontbeyourfan")
	@ApiImplicitParams({
	@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query"),
	@ApiImplicitParam(name="fanId",value="粉丝Id",required=true,dataType="String",paramType="query"),
	})
	@ApiOperation(value="取关用户",notes="取关用户接口")
	public JSONResult dontbeyourfan(String userId,String fanId){
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
			return JSONResult.errorMsg("");
		}
		userService.deleteFanRelation(userId, fanId);
 		return JSONResult.ok("取消关注成功");
	}
	
	@PostMapping("/reportuser")
	@ApiImplicitParam(name="usersReport",value="usersReport对象",required=true,dataType="UsersReport",paramType="form")
	@ApiOperation(value="举报用户",notes="举报用户接口")
	public JSONResult reportuser(@RequestBody UsersReport usersReport){
		// 保存举报信息
		int effectedNum = userService.reportUser(usersReport);
		if(effectedNum!=1){
			return JSONResult.ok("举报失败");
		}
 		return JSONResult.ok("成功举报");
	}
	
}
