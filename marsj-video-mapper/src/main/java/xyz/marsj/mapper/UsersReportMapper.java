package xyz.marsj.mapper;

import java.util.List;
import xyz.marsj.pojo.UsersReport;

public interface UsersReportMapper {
    int deleteByPrimaryKey(String id);

    int insert(UsersReport record);

    UsersReport selectByPrimaryKey(String id);

    List<UsersReport> selectAll();

    int updateByPrimaryKey(UsersReport record);
}