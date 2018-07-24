package xyz.marsj.mapper;

import java.util.List;
import xyz.marsj.pojo.Bgm;

public interface BgmMapper {
    int deleteByPrimaryKey(String id);

    int insert(Bgm record);

    Bgm selectByPrimaryKey(String id);

    List<Bgm> selectAll();

    int updateByPrimaryKey(Bgm record);
   
}