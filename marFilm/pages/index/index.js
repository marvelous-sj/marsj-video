const app = getApp()
var bmap = require('../libs/bmap-wx.min.js');
Page({
  data: {
    // 用于分页的属性
    totalPage: 1,
    page:1,
    videoList:[],

    screenWidth: 350,
    serverUrl: "",
    weatherData: '',
    searchContent: ""
  },

  onLoad: function (params) { 
    var that = this;  
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    that.setData({
      screenWidth: screenWidth,
    }) 
    //搜索相关
    var searchContent = app.globleData.search;
    var isSaveRecord = app.globleData.isSaveRecord;
    if (isSaveRecord == null || isSaveRecord == '' || isSaveRecord == undefined) {
      isSaveRecord = 0;
    }
    that.setData({
      searchContent: searchContent
    })

    var page = that.data.page;
    that.getVedioList(page, isSaveRecord) ;
  },

  getVedioList: function (page, isSaveRecord){
    var that = this;
    wx.showLoading({
      title: '加载中，请等待...',
    })
   
    var serverUrl = app.serverUrl;
    var searchContent = that.data.searchContent;
    wx.request({
      url: serverUrl + '/video/videolist?page=' + page + "&isSaveRecord=" + isSaveRecord,
      method: "POST",
      data: { videoDesc: searchContent},
      success: function (res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        if (page == 1) {
          that.setData({
            videoList: []
          })
        }
        var newVideoList = res.data.data.rows;
        var nowVideoList = that.data.videoList;
        that.setData({
          videoList: nowVideoList.concat(newVideoList),
          page: page,
          serverUrl: serverUrl,
          totalPage: res.data.data.total,
        })

      }
    })
    that.getWeather();
  },
  getWeather:function(){
    var that=this;
    // 新建百度地图对象 
    var BMap = new bmap.BMapWX({
      ak: 'vh3iSfAQQZwiGbwC4fGGU45tzyOfuK57'
    });
    var success = function (data) {
      var local = "../images/local.svg"
      var temp = "../images/temp.svg"
      var weatherData = data.currentWeather[0];
      var arry = weatherData.date.split("：");
      var weatherTemp = arry[1].slice(0, arry[1].length - 1);
      that.setData({
        localPoint: weatherData.currentCity,
        weatherTemp: weatherTemp,
        local: local,
        temp: temp
      });
    }
    // 发起weather请求 
    BMap.weather({
      success: success
    });
  },
  onReachBottom:function(){
    var that=this;

    var total=that.totalPage;
    var page= that.page;
    if(total==page){
      wx.showToast({
        title: '已经没有数据了..',
        icon: 'none',
        duration: 2000,
      })
      return;
    }
    page++;
    that.getVedioList(page,0);
  },
  //下拉刷新
  onPullDownRefresh:function(){
  wx.showNavigationBarLoading();
  this.getVedioList(1,0);
  this.getWeather();
  },

  //进入详情页
    showVideoInfo: function (e) {
    var that = this;
    var videoList = that.data.videoList;
    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(videoList[arrindex]);
    wx.redirectTo({
      url: '../videoinfo/videoinfo?videoInfo=' + videoInfo
    })
  }

})
