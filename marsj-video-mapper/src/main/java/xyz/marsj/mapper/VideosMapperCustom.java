package xyz.marsj.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import xyz.marsj.pojo.Videos;
import xyz.marsj.pojo.vo.VideosVO;

public interface VideosMapperCustom {

    List<VideosVO> selectAllVideo(Videos video);
    int addVideoLikeCount(String id);
    int reduceVideoLikeCount(String id);
    List<VideosVO> queryUserLike(@Param("userId")String userId);
    List<VideosVO> queryUserFollow(@Param("fanId")String fanId);
}