package xyz.marsj.service;

import java.util.List;

import xyz.marsj.pojo.Comments;
import xyz.marsj.pojo.Videos;
import xyz.marsj.utils.PagedResult;

public interface IVideoService {
	String saveVideo(Videos video);
	int addVideoCover(Videos video);
	PagedResult selectAllVideo(Videos video,Integer isSaveRecord,Integer page,Integer pageSize);
	List<String> selectAllHotWords();
	void isLikeVideo(String userId,String videoId,String videoCreatorId);
	void isUnLikeVideo(String userId,String videoId,String videoCreatorId);
	PagedResult queryUserLikeVideo(String userId,Integer page,Integer pageSize);
	PagedResult queryUserFollowVideo(String fanId,Integer page,Integer pageSize);
	PagedResult getCommentsList(String videoId,Integer page,Integer pageSize);
	void saveComment(Comments comment);

}
