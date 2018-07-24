package xyz.marsj.controller;


import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import xyz.marsj.pojo.Users;
import xyz.marsj.pojo.vo.UsersVO;
import xyz.marsj.service.IUserService;
import xyz.marsj.utils.JSONResult;
import xyz.marsj.utils.MD5Utils;
@Api(tags="用户注册与登录接口")
@RestController
public class RegisterLoginController extends BaseController{
	@Autowired
	private IUserService userService;
	@PostMapping("/register")
	@ApiOperation(value="用户注册",notes="用户注册接口")
	public JSONResult register(@RequestBody Users user) {
		//判空
		if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())) {
			return JSONResult.errorMsg("用户名和密码不能为空");
		}
		//是否存在
		boolean checkUserName = userService.checkUserName(user);
		//insert
		
			if(checkUserName) {
				try {
				user.setNickname(user.getUsername());
				user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
				user.setFansCounts(0);
				user.setFollowCounts(0);
				user.setReceiveLikeCounts(0);
				userService.insertUser(user);
				} catch (Exception e) {
					return JSONResult.errorMsg("系统内部错误，请稍后再试");
				}
			}else {
				return JSONResult.errorMsg("用户名已经存在，请重新输入");
			}
		user.setPassword("");
		UsersVO	userOv=setUserRedisSessionToken(user);
		return JSONResult.ok(userOv);
	}
	public UsersVO setUserRedisSessionToken(Users userModel) {
		String userToken = UUID.randomUUID().toString();
		//设置30分钟过期时间
		redis.set(USER_REDIS_SESSION+":"+userModel.getId(), userToken, 1000*60*30);
		UsersVO userOv=new UsersVO();
		BeanUtils.copyProperties(userModel, userOv);
		userOv.setUserToken(userToken);
		return userOv;
	}
	@PostMapping("/login")
	@ApiOperation(value="用户登录",notes="用户登录接口")
	public JSONResult login(@RequestBody Users user) {
		//判空
		if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())) {
			return JSONResult.errorMsg("用户名和密码不能为空");
		}
		//验证账号密码
		Users loginUser = userService.checkUserNameAndPwd(user);
		if(loginUser==null) {
			return JSONResult.errorMsg("用户名或密码错误");
		}
		loginUser.setPassword("");
		UsersVO	userOv=setUserRedisSessionToken(loginUser);
		return JSONResult.ok(userOv);
	}
	
	@ApiOperation(value="用户注销",notes="用户注销接口")
	@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query")
	@PostMapping("/logout")
	public JSONResult logout(String userId) {
		redis.del(USER_REDIS_SESSION+":"+userId);
		
		return JSONResult.ok();
	}
}
