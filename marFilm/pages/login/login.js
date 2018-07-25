//index.js
//获取应用实例
const app = getApp()
Page({
  data: {
  },
  onLoad: function (params) {
    var me = this;
    var redirectUrl = params.redirectUrl;
    if (redirectUrl != null && redirectUrl != undefined && redirectUrl != '') {
      redirectUrl = redirectUrl.replace(/#/g, "?");
      redirectUrl = redirectUrl.replace(/@/g, "=");
      me.redirectUrl = redirectUrl;
    }
  },

  doLogin: function (e) {
   var that=this;
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
      url: app.serverUrl+'/login',
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
            title: '登录成功！',
            icon: 'success',
            duration: 2000
          })
          //app.userInfo=res.data.data;
          app.setGlobleUserInfo(res.data.data);
          var redirectUrl = that.redirectUrl;
          if (redirectUrl != null && redirectUrl != undefined && redirectUrl != '') {
            wx.redirectTo({
              url: redirectUrl,
            })
          } else {
            wx.switchTab({
              url: '../mine/mine',
            })
          }
          
     
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
  goToRegister:function(){
    wx.redirectTo({
      url: '../register/register',
    })
  }

  
})
