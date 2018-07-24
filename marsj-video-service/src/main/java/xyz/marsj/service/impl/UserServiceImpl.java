package xyz.marsj.service.impl;

import java.util.Date;
import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import xyz.marsj.mapper.UsersFansMapper;
import xyz.marsj.mapper.UsersLikeVideosMapper;
import xyz.marsj.mapper.UsersMapper;
import xyz.marsj.mapper.UsersReportMapper;
import xyz.marsj.pojo.Users;
import xyz.marsj.pojo.UsersFans;
import xyz.marsj.pojo.UsersLikeVideos;
import xyz.marsj.pojo.UsersReport;
import xyz.marsj.service.IUserService;
import xyz.marsj.utils.MD5Utils;
@Service
public class UserServiceImpl implements IUserService {
	@Autowired
	private UsersMapper userMapper;
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	@Autowired
	private UsersFansMapper usersFansMapper;
	@Autowired
	private UsersReportMapper usersReportMapper;
	@Autowired
	private Sid sid;
	@Override
	@Transactional
	public boolean checkUserName(Users user) {
		boolean flag=false;
		Users users = userMapper.selectByUserName(user.getUsername());
		if(users==null) {
			flag=true;
		}
		return flag;
	}

	@Override
	@Transactional
	public void insertUser(Users user) {
		String userId = sid.nextShort();
		user.setId(userId);
		userMapper.insert(user);
	}

	@Override
	public Users checkUserNameAndPwd(Users user) {
		String username=user.getUsername();
		String password=user.getPassword();
		Users loginUser =null;
		try {
			 loginUser = userMapper.selectByUserNameAndPwd(username, MD5Utils.getMD5Str(password));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return loginUser;
	}

	@Override
	@Transactional
	public int updataUserInfo(Users user) {
		return userMapper.updateByPrimaryKey(user);
	}

	@Override
	public Users queryUserInfo(String userId) {
		Users users = userMapper.selectByPrimaryKey(userId);
		return users;
	}

	@Override
	public boolean isLikeVideo(String userId, String videoId) {
		List<UsersLikeVideos> list = usersLikeVideosMapper.selectHasOrNot(userId, videoId);
		if(list!=null&&list.size()==1) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public void saveFanRelation(String userId, String fanId) {
		//保存用户粉丝关系
		UsersFans record =new UsersFans();
		String id = sid.nextShort();
		record.setId(id);
		record.setFanId(fanId);
		record.setUserId(userId);
		usersFansMapper.insert(record);
		//粉丝数++
		userMapper.addFansCounts(userId);
		//关注数++
		userMapper.addFollowCounts(fanId);
	}

	@Override
	@Transactional
	public void deleteFanRelation(String userId, String fanId) {
		//删除用户粉丝关系
		usersFansMapper.deleteById(userId, fanId);
		//粉丝数--
		userMapper.reduceFansCounts(userId);
		//关注数--
		userMapper.reduceFollowCounts(fanId);
	}

	@Override
	public boolean userIsFollow(String userId, String fanId) {
		List<UsersFans> list = usersFansMapper.selectUserIsFollow(userId, fanId);
		if(list!=null&&list.size()==1) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public int reportUser(UsersReport usersReport) {
		String id = sid.nextShort();
		usersReport.setId(id);
		usersReport.setCreateDate(new Date());
		return usersReportMapper.insert(usersReport);	
	}
	
	

}
