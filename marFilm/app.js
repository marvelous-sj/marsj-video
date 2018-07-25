//app.js
App({
  serverUrl:"http://120.79.194.248:8081",
userInfo:null,
 globleData:{
   isSaveRecord:0,
   search:null,
   publisherId:null
 },
getGlobleUserInfo: function () {
  return wx.getStorageSync("userInfo");
},
setGlobleUserInfo: function (user) {
  wx.setStorageSync("userInfo", user)
},
  reportReasonArray: [
  "色情低俗",
  "政治敏感",
  "涉嫌诈骗",
  "辱骂谩骂",
  "广告垃圾",
  "诱导分享",
  "引人不适",
  "过于暴力",
  "违法违纪",
  "其它原因"
]
})


