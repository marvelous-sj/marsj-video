package xyz.marsj.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import xyz.marsj.pojo.UsersLikeVideos;

public interface UsersLikeVideosMapper {
    int deleteById(@Param("userId")String userId,@Param("videoId")String videoId);

    int insert(UsersLikeVideos record);

    UsersLikeVideos selectByPrimaryKey(String id);

    List<UsersLikeVideos> selectHasOrNot(@Param("userId")String userId,@Param("videoId")String videoId);

    int updateByPrimaryKey(UsersLikeVideos record);
}