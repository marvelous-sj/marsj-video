package xyz.marsj.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import xyz.marsj.pojo.vo.CommentsVO;

public interface CommentsMapperCustom {
    List<CommentsVO> selectByVideoId(@Param("videoId")String videoId);
}