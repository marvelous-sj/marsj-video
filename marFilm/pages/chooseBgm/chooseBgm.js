const app = getApp()

Page({
    data: {
      bgmList: [],
      serverUrl: "",
      videoParams: {}
    },



  onLoad:function(param){
    var that =this;
    that.setData({
      videoParams:param
    })
    var serverUrl=app.serverUrl;
    var user = app.getGlobleUserInfo();
    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: serverUrl + '/bgm/list',
      method: "POST",
      header: {
        'content-type': 'application/json',// 默认值
        'headerUserId': user.id,
        'headerUserToken': user.userToken
      },
      success: function (res) {
        wx.hideLoading();
        var data = res.data;
        console.log(data)
        var faceUrl = "../images/noneface.svg";
        if (data.status == 200) {
          that.setData({
            serverUrl: serverUrl,
            bgmList: data.data
          })
        }
      }
  })
  },

  upload:function(e){
    var that=this;
    var data=e.detail.value;
    var bgmId=data.bgmId;
    var desc=data.desc;
    var duration = that.data.videoParams.duration;
    var tmpheight = that.data.videoParams.tmpheight;
    var tmpwidth = that.data.videoParams.tmpwidth;
    //var tempCoverFilePath = that.data.videoParams.thumbTempFilePath;
    var tempFilePath = that.data.videoParams.tempFilePath;
    var url = app.serverUrl;
    wx.showLoading({
      title: '上传中...',
    })
    
    var user=app.getGlobleUserInfo();
    wx.uploadFile({
      url: url + '/video/upload',
      filePath: tempFilePath,
      name: 'file',
      formData: {
        userId: user.id,
        videoSeconds: duration,
        videoHeight: tmpheight,
        videoWidth: tmpwidth,
        bgmId: bgmId,
        desc: desc
      },
      header: {
        'content-type': 'application/json',// 默认值
        'headerUserId': user.id,
        'headerUserToken': user.userToken
      },
      success: function (res) {
        var data = JSON.parse(res.data);
        if (data.status == 200) {
                wx.showToast({
                  title: '上传成功！',
                  icon: 'success',
                  duration: 2000
                })
                wx.switchTab({
                 url: '../mine/mine',
               })


          //上传视频封面
          // wx.uploadFile({
          //   url: url + '/video/uploadCover',
          //   filePath: tempCoverFilePath,
          //   name: 'file',
          //   formData: {
          //     userId: app.user.id,
          //     videoId: data.data
          //   },
          //   success: function (res) {
          //     var data = JSON.parse(res.data);
          //     wx.hideLoading();
          //     if (data.status == 200) {
          //       wx.showToast({
          //         title: '上传成功！',
          //         icon: 'success',
          //         duration: 2000
          //       })
          //      wx.redirectTo({
          //        url: '../mine/mine',
          //      })
          //     } else {
          //       wx.showToast({
          //         title: data.data.msg,
          //         icon: 'none',
          //         duration: 2000
          //       })
          //     }

          //   }
          // })
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