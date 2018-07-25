var videoUtil = require('../../utils/videoUtil.js')
const app = getApp()

Page({
  data: {
    cover: "cover",
    videoId: "",
    src: "",
    videoInfo: {},
    userLikeVideo: false,
    commentsPage: 1,
    commentsTotalPage:1,
    commentsList:[],
    placeholder: "说点什么..."
  },

  showPublisher: function () {
    var that = this;
    var user = app.getGlobleUserInfo();
    var videoInfo = that.data.videoInfo;
    var realUrl = '../mine/mine#publisherId@' + videoInfo.userId;
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../login/login?redirectUrl=' + realUrl,
      })
    } else {
      app.globleData.publisherId = videoInfo.userId;
      app.globleData.isSaveRecord = 0;
      app.globleData.search = null;
      wx.switchTab({
        url: '../mine/mine',
      })
    }

  },
  likeVideoOrNot:function(){
    var that = this;
    var videoInfo = that.data.videoInfo;
    var user = app.getGlobleUserInfo();

    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../login/login',
      })
    } else {

      var userLikeVideo = that.data.userLikeVideo;
      var url = '/video/userlike?userId=' + user.id + '&videoId=' + videoInfo.id + '&videoCreatorId=' + videoInfo.userId;
      if (userLikeVideo) {
        url = '/video/userunlike?userId=' + user.id + '&videoId=' + videoInfo.id + '&videoCreatorId=' + videoInfo.userId;
      }

      var serverUrl = app.serverUrl;
      wx.showLoading({
        title: '...',
      })
      wx.request({
        url: serverUrl + url,
        method: 'POST',
        header: {
          'content-type': 'application/json', // 默认值
          'headerUserId': user.id,
          'headerUserToken': user.userToken
        },
        success: function (res) {
          wx.hideLoading();
          that.setData({
            userLikeVideo: !userLikeVideo
          });
        }
      })


    }
  },
  shareMe: function () {
    var me = this;
    var user = app.getGlobleUserInfo();

    wx.showActionSheet({
      itemList: ['下载到本地', '举报用户', '分享到朋友圈', '分享到QQ空间', '分享到微博'],
      success: function (res) {
        if (res.tapIndex == 0) {
          // 下载
          wx.showLoading({
            title: '下载中...',
          })
          wx.downloadFile({
            url: app.serverUrl + me.data.videoInfo.videoPath,
            success: function (res) {
              // 只要服务器有响应数据，就会把响应内容写入文件并进入 success 回调，业务需要自行判断是否下载到了想要的内容
              if (res.statusCode === 200) {
                wx.saveVideoToPhotosAlbum({
                  filePath: res.tempFilePath,
                  success: function (res) {
                    wx.hideLoading();
                  }
                })
              }
            }
          })
        } else if (res.tapIndex == 1) {
          // 举报
          var videoInfo = JSON.stringify(me.data.videoInfo);
          var realUrl = '../videoinfo/videoinfo#videoInfo@' + videoInfo;

          if (user == null || user == undefined || user == '') {
            wx.navigateTo({
              url: '../login/login?redirectUrl=' + realUrl,
            })
          } else {
            var publishUserId = me.data.videoInfo.userId;
            var videoId = me.data.videoInfo.id;
            var currentUserId = user.id;
            wx.navigateTo({
              url: '../report/report?videoId=' + videoId + "&publishUserId=" + publishUserId
            })
          }
        } else {
          wx.showToast({
            title: '官方暂未开放...',
            icon:'none'
          })
        }
      }
    })
  },

  upload: function () {
    var that = this;
    var user = app.getGlobleUserInfo();
    var videoInfo = JSON.stringify(that.data.videoInfo);
    var realUrl = '../videoinfo/videoinfo#videoInfo@' + videoInfo;
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../login/login?redirectUrl=' + realUrl,
      })
    } else {
      videoUtil.uploadVideo();
    }
  },

  onLoad: function (params) {
    var that = this;
    that.videoCtx = wx.createVideoContext("myVideo", that);

    // 获取上一个页面传入的参数
    var videoInfo = JSON.parse(params.videoInfo);

    var height = videoInfo.videoHeight;
    var width = videoInfo.videoWidth;
    var cover = "cover";
    if (width >= height) {
      cover = "";
    }

    that.setData({
      videoId: videoInfo.id,
      src: app.serverUrl + videoInfo.videoPath,
      videoInfo: videoInfo,
      cover: cover
    });

    var serverUrl = app.serverUrl;
    var user = app.getGlobleUserInfo();
    var loginUserId = "";
    if (user != null && user != undefined && user != '') {
      loginUserId = user.id;
    }
    wx.request({
      url: serverUrl + '/user/querypublisher?userId=' + loginUserId + "&videoId=" + videoInfo.id + "&publishUserId=" + videoInfo.userId,
      method: 'POST',
      header: {
        'content-type': 'application/json',// 默认值
        'headerUserId': user.id,
        'headerUserToken': user.userToken
      },
      success: function (res) {
        var publisher = res.data.data.publisher;
        var userLikeVideo = res.data.data.isLikeVideo;
        that.setData({
          serverUrl: serverUrl,
          publisher: publisher,
          userLikeVideo: userLikeVideo
        });
      }
    })

    that.getCommentsList(1);
  },

  onShow: function () {
    var that = this;
    that.videoCtx.play();
  },

  onHide: function () {
    var that = this;
    that.videoCtx.pause();
  },

 showSearch:function(){
   wx.navigateTo({
     url: '../searchVideo/searchVideo',
  
   })
 },
 showIndex: function () {
   wx.switchTab({
     url: '../index/index',
   })
 },

 showMine: function () {
   var user = app.getGlobleUserInfo();

   if (user == null || user == undefined || user == '') {
     wx.navigateTo({
       url: '../userLogin/login',
     })
   } else {
     wx.switchTab({
       url: '../mine/mine',
     })
   }
 },

 leaveComment:function(){
    this.setData({
      commentFocus:true
    })
 },
 replyFocus:function(e){
                                              //小写 
    var fatherCommentId=e.currentTarget.dataset.fathercommentid;
    var toUserId = e.currentTarget.dataset.touserid;
    var toNickname = e.currentTarget.dataset.tonickname;
    this.setData({
      placeholder: "回复 " + toNickname,
      replyFatherCommentId: fatherCommentId,
      replyToUserId: toUserId,
      commentFocus: true
    })
 },
 saveComment:function(e){
   var that = this;
   var fatherCommentId = e.currentTarget.dataset.replyfathercommentid;
   var toUserId = e.currentTarget.dataset.replytouserid;
   var content=e.detail.value;
   var user = app.getGlobleUserInfo();
   var videoInfo = JSON.stringify(that.data.videoInfo);
   var realUrl = '../videoinfo/videoinfo#videoInfo@' + videoInfo;
   if (user == null || user == undefined || user == '') {
     wx.navigateTo({
       url: '../login/login?redirectUrl=' + realUrl,
     })
   } else {
     wx.request({
       url: app.serverUrl+'/video/savecomment',
       data: {
         videoId: that.data.videoInfo.id,
         fromUserId:user.id,
         comment: content,
         fatherCommentId: fatherCommentId,
         toUserId: toUserId
       },
       header: {
         'content-type': 'application/json',// 默认值
         'headerUserId': user.id,
         'headerUserToken': user.userToken
       },
       method: 'POST',
       success: function(res) {
       
        that.setData({
          contentValue:"",
          commentsList:[]
        })
        that.getCommentsList(1);
       },
      
     })
   }
 },
 getCommentsList:function(page){
   var that=this;
   var videoId= that.data.videoInfo.id;
   wx.request({
     url: app.serverUrl + '/video/getvideocomments?videoId='+videoId+'&page='+page,
     method: 'POST',
     success: function (res) {
       var commentsList=res.data.data.rows;
       var nowCommentsList = that.data.commentsList;
       that.setData({
         commentsList: nowCommentsList.concat(commentsList),
         commentsPage: page,
         commentsTotalPage: res.data.data.total,
       })
     },

   })
 },
 onReachBottom: function () {
   var that = this;
   var currentPage = that.data.commentsPage;
   var totalPage = that.data.commentsTotalPage;
   if (currentPage === totalPage) {
     return;
   }
   var page = currentPage + 1;
   that.getCommentsList(page);
 }
})