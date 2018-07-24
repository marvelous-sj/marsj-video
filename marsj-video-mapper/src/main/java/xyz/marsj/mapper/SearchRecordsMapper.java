package xyz.marsj.mapper;

import java.util.List;
import xyz.marsj.pojo.SearchRecords;

public interface SearchRecordsMapper {
    int deleteByPrimaryKey(String id);

    int insert(SearchRecords record);

    SearchRecords selectByPrimaryKey(String id);

    List<String> selectHotWords();

    int updateByPrimaryKey(SearchRecords record);
}