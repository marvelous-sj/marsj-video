
//获取应用实例
const app = getApp()
Page({
  data: {
    
  },
  doRegister: function (e) {
 
    var formObject=e.detail.value;
    var username=formObject.username;
    var password = formObject.password;
    if(username.length==0||password.length==0){
      wx.showToast({
        title: '用户名和密码不能为空！',
        icon: 'none',
        duration: 2000
      })
    }else{
    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: app.serverUrl+'/register',
      method:"POST",
      data: {
        username: username,
        password: password
      },
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        wx.hideLoading();
        var status=res.data.status;
        if (status==200){
          wx.showToast({
            title: '注册成功！',
            icon: 'success',
            duration: 2000
          })
          //app.userInfo=res.data.data;
          app.setGlobleUserInfo(res.data.data);
          wx.switchTab({
            url: '../mine/mine',
          })
        }else{
          wx.showToast({
            title: res.data.msg,
            icon: 'none',
            duration: 2000
          })
        }
      }
    })
    }
  },
  backToLogin:function(){
    wx.redirectTo({
      url: '../login/login',
    })
  }
})
