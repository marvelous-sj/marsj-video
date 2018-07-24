package xyz.marsj.service.impl;

import java.util.Date;
import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import xyz.marsj.mapper.CommentsMapper;
import xyz.marsj.mapper.CommentsMapperCustom;
import xyz.marsj.mapper.SearchRecordsMapper;
import xyz.marsj.mapper.UsersLikeVideosMapper;
import xyz.marsj.mapper.UsersMapper;
import xyz.marsj.mapper.VideosMapper;
import xyz.marsj.mapper.VideosMapperCustom;
import xyz.marsj.pojo.Comments;
import xyz.marsj.pojo.SearchRecords;
import xyz.marsj.pojo.UsersLikeVideos;
import xyz.marsj.pojo.Videos;
import xyz.marsj.pojo.vo.CommentsVO;
import xyz.marsj.pojo.vo.VideosVO;
import xyz.marsj.service.IVideoService;
import xyz.marsj.utils.PagedResult;
import xyz.marsj.utils.TimeAgoUtils;
@Service
public class VideoServiceImpl implements IVideoService {
	@Autowired
	private VideosMapper videosMapper;
	@Autowired
	private UsersMapper usersMapper;
	@Autowired
	private VideosMapperCustom videosMapperCustom;
	@Autowired
	private SearchRecordsMapper searchRecordsMapper;
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	@Autowired
	private CommentsMapper commentsMapper;
	@Autowired
	private CommentsMapperCustom commentsMapperCustom;
	@Autowired
	private Sid sid;
	@Transactional
	@Override
	public String saveVideo(Videos video) {
			String videoId = sid.nextShort();
			video.setId(videoId);
			videosMapper.insert(video);
		return videoId;
	}
	@Transactional
	@Override
	public int addVideoCover(Videos video) {
		
		return videosMapper.updateByPrimaryKey(video);
	}
	@Transactional
	@Override
	public PagedResult selectAllVideo(Videos video,Integer isSaveRecord,Integer page,Integer pageSize) {
		String videoDesc = video.getVideoDesc();
		// 1为查询
		if(isSaveRecord==1) {
			SearchRecords record=new SearchRecords();
			String id = sid.nextShort();
			record.setId(id);
			record.setContent(videoDesc);
			searchRecordsMapper.insert(record);
		}
		
		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosMapperCustom.selectAllVideo(video);
		PageInfo<VideosVO> pageList=new PageInfo<>(list);
		PagedResult result=new PagedResult();
		result.setPage(page);
		result.setRows(list);
		//总记录数
		result.setRecords(pageList.getTotal());
		//总页数
		result.setTotal(pageList.getPages());
		return result;
	}
	@Override
	public List<String> selectAllHotWords() {
		return searchRecordsMapper.selectHotWords();
	}
	@Transactional
	@Override
	public void isLikeVideo(String userId, String videoId, String videoCreatorId) {
		//在users_like_videos插入
		UsersLikeVideos like=new UsersLikeVideos();
		String id = sid.nextShort();
		like.setId(id);
		like.setUserId(userId);
		like.setVideoId(videoId);
		usersLikeVideosMapper.insert(like);
		//users表中 receive_like_counts ++
		usersMapper.addReceiveLikeCount(videoCreatorId);
		//videos表中的 like_counts++
		videosMapperCustom.addVideoLikeCount(videoId);
	}
	@Transactional
	@Override
	public void isUnLikeVideo(String userId, String videoId, String videoCreatorId) {
		//在users_like_videos删除
		usersLikeVideosMapper.deleteById(userId,videoId);
		//users表中 receive_like_counts --
		usersMapper.reduceReceiveLikeCount(videoCreatorId);
		//videos表中的 like_counts++
		videosMapperCustom.reduceVideoLikeCount(videoId);
		
	}
	@Override
	public PagedResult queryUserLikeVideo(String userId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosMapperCustom.queryUserLike(userId);
		PageInfo<VideosVO> pageList=new PageInfo<>(list);
		PagedResult result=new PagedResult();
		result.setPage(page);
		result.setRows(list);
		//总记录数
		result.setRecords(pageList.getTotal());
		//总页数
		result.setTotal(pageList.getPages());
		return result;
	}
	@Override
	public PagedResult queryUserFollowVideo(String fanId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosMapperCustom.queryUserFollow(fanId);
		PageInfo<VideosVO> pageList=new PageInfo<>(list);
		PagedResult result=new PagedResult();
		result.setPage(page);
		result.setRows(list);
		//总记录数
		result.setRecords(pageList.getTotal());
		//总页数
		result.setTotal(pageList.getPages());
		return result;
	}
	@Override
	@Transactional
	public void saveComment(Comments comment) {
		String id = sid.nextShort();
		comment.setId(id);
		comment.setCreateTime(new Date());
		commentsMapper.insert(comment);
	}
	@Override
	public PagedResult getCommentsList(String videoId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<CommentsVO> list = commentsMapperCustom.selectByVideoId(videoId);
		for (CommentsVO comment : list) {
			String time = TimeAgoUtils.format(comment.getCreateTime());
			comment.setTimeAgoStr(time);
		}
		PageInfo<CommentsVO> pageList=new PageInfo<>(list);
		PagedResult result=new PagedResult();
		result.setPage(page);
		result.setRows(list);
		//总记录数
		result.setRecords(pageList.getTotal());
		//总页数
		result.setTotal(pageList.getPages());
		return result;
	}


}
