package xyz.marsj.mapper;

import java.util.List;
import xyz.marsj.pojo.Videos;

public interface VideosMapper {
    int deleteByPrimaryKey(String id);

    int insert(Videos record);

    Videos selectByPrimaryKey(String id);

    List<Videos> selectAll();

    int updateByPrimaryKey(Videos record);
}