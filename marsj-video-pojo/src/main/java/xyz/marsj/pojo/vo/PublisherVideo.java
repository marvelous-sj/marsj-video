package xyz.marsj.pojo.vo;

public class PublisherVideo {
    
	public UsersVO publisher;
	public boolean isLikeVideo;
	public UsersVO getPublisher() {
		return publisher;
	}
	public void setPublisher(UsersVO publisher) {
		this.publisher = publisher;
	}
	public boolean isLikeVideo() {
		return isLikeVideo;
	}
	public void setLikeVideo(boolean isLikeVideo) {
		this.isLikeVideo = isLikeVideo;
	}

}