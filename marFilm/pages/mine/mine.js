var videoUtil = require('../../utils/videoUtil.js')
const app = getApp()

Page({
  data: {
    faceUrl: "../images/noneface.svg",
    isMe: true,
    isFollow: false,
    videoSelClass: "video-info",
    isSelectedWork: "video-info-selected",
    isSelectedLike: "",
    isSelectedFollow: "",
    myVideoList: [],
    myVideoPage: 1,
    myVideoTotal: 1,

    likeVideoList: [],
    likeVideoPage: 1,
    likeVideoTotal: 1,

    followVideoList: [],
    followVideoPage: 1,
    followVideoTotal: 1,
    x:null,
    myWorkFalg: false,
    myLikesFalg: true,
    myFollowFalg: true
  },
 uploadVideo:function(){
   videoUtil.uploadVideo();
 },
 followMe: function (e) {
   var that = this;

   var user = app.getGlobleUserInfo();
   var userId = user.id;
   var publisherId = that.data.publisherId;

   var followType = e.currentTarget.dataset.followtype;

   // 1：关注 0：取消关注
   var url = '';
   if (followType == '1') {
     url = '/user/beyourfan?userId=' + publisherId + '&fanId=' + userId;
   } else {
     url = '/user/dontbeyourfan?userId=' + publisherId + '&fanId=' + userId;
   }

   wx.showLoading();
   wx.request({
     url: app.serverUrl + url,
     method: 'POST',
     header: {
       'content-type': 'application/json', // 默认值
       'headerUserId': user.id,
       'headerUserToken': user.userToken
     },
     success: function () {
       wx.hideLoading();
       if (followType == '1') {
         that.setData({
           isFollow: true,
           fansCounts: ++that.data.fansCounts
         })
       } else {
         that.setData({
           isFollow: false,
           fansCounts: --that.data.fansCounts
         })
       }
     }
   })
 },


onHide:function(){
  var that=this;
  app.globleData.publisherId = null;
  that.setData({
    isMe: true,
  })
},
  onShow:function(){
    var that = this;
    var user = app.getGlobleUserInfo();
    var userId = user.id;

    var publisherId = app.globleData.publisherId;
    if (publisherId != null && publisherId != '' && publisherId != undefined && publisherId!=userId) {
      userId = publisherId;
      that.setData({
        isMe: false,
        publisherId: publisherId,
        serverUrl: app.serverUrl
      })
    }
    that.setData({
      userId: userId
    })

 // var user=app.userInfo;
    if (userId==null){
   wx.redirectTo({
     url: '../login/login',
   })
   return;
 }
    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: app.serverUrl + '/user/query?userId=' + userId+'&fanId='+user.id,
      method: "POST",
      header: {
        'content-type': 'application/json',// 默认值
         'headerUserId': user.id,
        'headerUserToken': user.userToken
      },
      success: function (res) {
        
        wx.hideLoading();
        var data = res.data;
        var userInfo=data.data;
        var faceUrl = "../images/noneface.svg";
        if (data.status == 200) {
          if (userInfo.faceImage != null && userInfo.faceImage != '' &&             userInfo.faceImage!=undefined){
            var faceUrl = app.serverUrl + userInfo.faceImage;
          }
          that.setData({
          faceUrl: faceUrl,
          fansCounts: userInfo.fansCounts,
          followCounts: userInfo.followCounts,
          receiveLikeCounts: userInfo.receiveLikeCounts,
          nickname: userInfo.nickname,
          isFollow: userInfo.follow
        })
        } 
        else{
          wx.showToast({
            title: res.data.msg,
            duration: 3000,
            icon: "none",
            success: function () {
              wx.redirectTo({
                url: '../login/login',
              })
            }
          })
        }
      }
    })
    that.doSelectWork();
  },
  changeface:function(){ 
    var me=this;
    wx.chooseImage({
      count: 1, 
      sizeType: ['compressed'], // 可以指定是原图还是压缩图，默认二者都有
      sourceType: ['album'], // 可以指定来源是相册还是相机，默认二者都有
      success: function (res) {
        // 返回选定照片的本地文件路径列表，tempFilePath可以作为img标签的src属性显示图片
        var tempFilePaths = res.tempFilePaths;
        var user = app.getGlobleUserInfo();
        // var user=app.userInfo;
        var url=app.serverUrl;
        wx.showLoading({
          title: '上传中...',
        })
        wx.uploadFile({
          url: url+'/user/uploadface?userId='+user.id,
          filePath: tempFilePaths[0],
          name: 'file',
          header: {
            'content-type': 'application/json',// 默认值
            'headerUserId': user.id,
            'headerUserToken': user.userToken
          },
          success: function (res) {
            var data=JSON.parse(res.data);
            wx.hideLoading();
            if (data.status == 200) {
              wx.showToast({
                title: '上传成功！',
                icon: 'success',
                duration: 2000
              })
              var faceUrl = data.data;
              me.setData({
                faceUrl: url+ faceUrl
              })
            } else {
              wx.showToast({
                title: data.msg,
                icon: 'none',
                duration: 2000
              })
            }

          }
        })
      }
    })
  },

  logout:function(){
    wx.showLoading({
      title: '正在注销，请稍后...',
    })
    var user = app.getGlobleUserInfo();
 // var user=app.userInfo;
    wx.request({
      url: app.serverUrl + '/logout?userId=' + user.id,
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        wx.hideLoading();
        var status = res.data.status;
        if (status == 200) {
          wx.showToast({
            title: '注销成功',
            icon: 'success',
            duration: 2000
          })
          //置空
          //app.userInfo = null;
          wx.removeStorageSync("userInfo");
          wx.redirectTo({
            url: '../login/login',
          })
        }
      }
    })
  },
  
  doSelectWork: function () {
    this.setData({
      isSelectedWork: "video-info-selected",
      isSelectedLike: "",
      isSelectedFollow: "",

      myWorkFalg: false,
      myLikesFalg: true,
      myFollowFalg: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyVideoList(1);
  },

  doSelectLike: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "video-info-selected",
      isSelectedFollow: "",

      myWorkFalg: true,
      myLikesFalg: false,
      myFollowFalg: true,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyLikesList(1);
  },
  doSelectFollow: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "",
      isSelectedFollow: "video-info-selected",

      myWorkFalg: true,
      myLikesFalg: true,
      myFollowFalg: false,

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1
    });

    this.getMyFollowList(1)
  },
   getMyVideoList: function (page) {
     var that = this;
     var user = app.getGlobleUserInfo();
     // 查询视频信息
     wx.showLoading();
     // 调用后端
     var serverUrl = app.serverUrl;
     wx.request({
       url: serverUrl + '/video/videolist/?page=' + page,
       method: "POST",
       data: {
         userId: that.data.userId
       },
       header: {
         'content-type': 'application/json' // 默认值
       },
       success: function (res) {
         var myVideoList = res.data.data.rows;
         wx.hideLoading();
         var newVideoList = that.data.myVideoList;
         that.setData({
           myVideoPage: page,
           myVideoList: newVideoList.concat(myVideoList),
           myVideoTotal: res.data.data.total,
           serverUrl: app.serverUrl
         });
       }
     })
   },
   getMyLikesList: function (page) {
     var that = this;
     var userId = that.data.userId;

     // 查询视频信息
     wx.showLoading();
     // 调用后端
     var serverUrl = app.serverUrl;
     wx.request({
       url: serverUrl + '/video/showmylike/?userId=' + userId + '&page=' + page,
       method: "POST",
       header: {
         'content-type': 'application/json' // 默认值
       },
       success: function (res) {
         var likeVideoList = res.data.data.rows;
         wx.hideLoading();
         var newVideoList = that.data.likeVideoList;
         that.setData({
           likeVideoPage: page,
           likeVideoList: newVideoList.concat(likeVideoList),
           likeVideoTotal: res.data.data.total,
           serverUrl: app.serverUrl
         });
       }
     })
   },

   getMyFollowList: function (page) {
     var that = this;
     var userId = that.data.userId;

     // 查询视频信息
     wx.showLoading();
     // 调用后端
     var serverUrl = app.serverUrl;
     wx.request({
       url: serverUrl + '/video/showmyfollow/?fanId=' + userId + '&page=' + page,
       method: "POST",
       header: {
         'content-type': 'application/json' // 默认值
       },
       success: function (res) {
         var followVideoList = res.data.data.rows;
         wx.hideLoading();

         var newVideoList = that.data.followVideoList;
         that.setData({
           followVideoPage: page,
           followVideoList: newVideoList.concat(followVideoList),
           followVideoTotal: res.data.data.total,
           serverUrl: app.serverUrl
         });
       }
     })
   },
   // 点击跳转到视频详情页面
   showVideo: function (e) {
     var myWorkFalg = this.data.myWorkFalg;
     var myLikesFalg = this.data.myLikesFalg;
     var myFollowFalg = this.data.myFollowFalg;

     if (!myWorkFalg) {
       var videoList = this.data.myVideoList;
     } else if (!myLikesFalg) {
       var videoList = this.data.likeVideoList;
     } else if (!myFollowFalg) {
       var videoList = this.data.followVideoList;
     }

     var arrindex = e.target.dataset.arrindex;
     var videoInfo = JSON.stringify(videoList[arrindex]);

     wx.redirectTo({
       url: '../videoinfo/videoinfo?videoInfo=' + videoInfo
     })

   },

   // 到底部后触发加载
   onReachBottom: function () {
     var myWorkFalg = this.data.myWorkFalg;
     var myLikesFalg = this.data.myLikesFalg;
     var myFollowFalg = this.data.myFollowFalg;

     if (!myWorkFalg) {
       var currentPage = this.data.myVideoPage;
       var totalPage = this.data.myVideoTotal;
       // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
       if (currentPage === totalPage) {
         wx.showToast({
           title: '已经没有视频啦...',
           icon: "none"
         });
         return;
       }
       var page = currentPage + 1;
       this.getMyVideoList(page);
     } else if (!myLikesFalg) {
       var currentPage = this.data.likeVideoPage;
       var totalPage = this.data.myLikesTotal;
       // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
       if (currentPage === totalPage) {
         wx.showToast({
           title: '已经没有视频啦...',
           icon: "none"
         });
         return;
       }
       var page = currentPage + 1;
       this.getMyLikesList(page);
     } else if (!myFollowFalg) {
       var currentPage = this.data.followVideoPage;
       var totalPage = this.data.followVideoTotal;
       // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
       if (currentPage === totalPage) {
         wx.showToast({
           title: '已经没有视频啦...',
           icon: "none"
         });
         return;
       }
       var page = currentPage + 1;
       this.getMyFollowList(page);
     }

   }

})
