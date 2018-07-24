package xyz.marsj.mapper;

import java.util.List;
import xyz.marsj.pojo.Comments;

public interface CommentsMapper {
    int deleteByPrimaryKey(String id);

    int insert(Comments record);

    Comments selectByPrimaryKey(String id);

    List<Comments> selectAll();

    int updateByPrimaryKey(Comments record);
}