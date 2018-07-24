package xyz.marsj.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import xyz.marsj.pojo.Users;

public interface UsersMapper {
    int deleteByPrimaryKey(String id);

    int insert(Users record);

    Users selectByPrimaryKey(String id);
    Users selectByUserName(String username);
    Users selectByUserNameAndPwd(@Param("username")String username,@Param("password")String password);
    List<Users> selectAll();
    int updateByPrimaryKey(Users record);
    
    int addReceiveLikeCount(String id);
    int reduceReceiveLikeCount(String id);
    
    int addFansCounts(String userId);
    int reduceFansCounts(String userId);	
    
    int addFollowCounts(String fanId);
    int reduceFollowCounts(String fanId);
}