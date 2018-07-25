function uploadVideo() {
    var that = this;
    wx.chooseVideo({
      sourceType: ['album'],
      success: function (res) {
       var duration = res.duration;
       var tmpheight=res.height;
       var tmpwidth = res.width;
       //var thumbTempFilePath = res.thumbTempFilePath;
       var tempFilePath = res.tempFilePath;
        if(duration>11){
          wx.showToast({
            title: '上传视频不能超过11秒',
            icon: 'none',
            duration:3000
          })
        } else if (duration<2){
          wx.showToast({
            title: '上传视频不能少于2秒',
            icon:'none',
            duration: 3000
          })
        }else{
          //打开bgm页面
          wx.redirectTo({
            url: '../chooseBgm/chooseBgm?duration='+duration+
            '&tmpheight='+ tmpheight +
            '&tmpwidth='+ tmpwidth +
            //'&thumbTempFilePath='+ thumbTempFilePath +
            '&tempFilePath='+ tempFilePath ,
            
          })

        }
      }
    })
  }
  module.exports = {
  uploadVideo: uploadVideo
}