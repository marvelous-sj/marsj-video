package xyz.marsj.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import xyz.marsj.pojo.UsersFans;

public interface UsersFansMapper {
    int deleteById(@Param("userId")String userId,@Param("fanId")String fanId);

    int insert(UsersFans record);

    UsersFans selectByPrimaryKey(String id);

    List<UsersFans> selectUserIsFollow(@Param("userId")String userId,@Param("fanId")String fanId);

    int updateByPrimaryKey(UsersFans record);
}