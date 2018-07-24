package xyz.marsj.service;

import xyz.marsj.pojo.Users;
import xyz.marsj.pojo.UsersReport;

public interface IUserService {
	public boolean checkUserName(Users user);
	public void insertUser(Users user);
	public Users checkUserNameAndPwd(Users user);
	public int  updataUserInfo(Users user);
	public Users queryUserInfo(String userId);
	public boolean isLikeVideo(String userId,String videoId);
	public void saveFanRelation(String userId,String fanId);
	public void deleteFanRelation(String userId,String fanId);
	public boolean userIsFollow(String userId,String fanId);
	public int reportUser(UsersReport usersReport);
}
