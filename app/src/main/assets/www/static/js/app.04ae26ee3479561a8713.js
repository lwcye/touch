webpackJsonp([1],[,,function(e,o,t){"use strict";var i=t(0),a=t(14);i.a.use(a.a);new a.a({routes:[{path:"/",name:"affairs"}]})},function(e,o,t){function i(e){t(8)}var a=t(12)(t(5),t(13),i,null,null);e.exports=a.exports},,function(e,o,t){"use strict";Object.defineProperty(o,"__esModule",{value:!0});var i=t(7);t.n(i);o.default={name:"app",data:function(){return{videoArr:[],video:{},videoNum:0,lastTimeText:"",lastTime:0,videoName:"",submitFlag:!0}},watch:{handler:function(){this.submitFlag=!0}},computed:{submitDate:function(){return(new Date).getDate()}},created:function(){this._init()},methods:{_init:function(){this.$nextTick(function(){var e=this;this.$http.get("./static/index.json").then(function(o){if(1===o.body.status){for(var t=o.body.movietime,i=0;i<o.body.info.length;i++){var a=o.body.info[i].type;"video"!==a&&"movie"!==a||e.videoArr.push(o.body.info[i])}console.log(e.videoArr),localStorage.videoNum?localStorage.videoNum>=e.videoArr.length-1?localStorage.videoNum=0:localStorage.videoNum=Number(localStorage.videoNum)+1:localStorage.videoNum=0,e.videoNum=localStorage.videoNum,console.log(e.videoNum),"video"===e.videoArr[e.videoNum].type&&(e.videoName=e.videoArr[e.videoNum].name),localStorage[e.videoName+"count"]?localStorage[e.videoName+"count"]=Number(localStorage[e.videoName+"count"])+1:localStorage[e.videoName+"count"]=1,localStorage[e.videoName+"submitFlag"]||(localStorage[e.videoName+"submitFlag"]=!0),e.submitFlag=localStorage[e.videoName+"submitFlag"];var n=e.videoName,r=(new Date).getTime();r=Number((r+"").slice(0,10));var l=Number(localStorage[e.videoName+"count"]),d={type:"video",name:n,times:r,nums:l};console.log(d),e.submitFlag&&navigator.onLine&&e.$http.post("http://imeasyshow.com/public/home/hwled/savenumlog",d).then(function(){localStorage.removeItem(e.videoName+"count"),localStorage[e.videoName+"submitFlag"]=!1,console.log("submit"+!0)},function(e){console.log("submit"+!1)}),"video"===e.videoArr[e.videoNum].type?document.getElementById("my-player").play():"movie"===e.videoArr[e.videoNum].type&&((new Date).getHours()<t?location.href="index.html":document.getElementById("my-player").play())}}),setInterval(function(){e.$http.get("./static/index.json",{headers:{"Cache-Control":"no-cache"}}).then(function(e){if(1===e.body.status){for(var o=[],t=0,i=0;i<e.body.info.length;i++){var a=e.body.info[i].type;"video"!==a&&"movie"!==a||o.push(e.body.info[i])}t=localStorage.videoNum,o[t].create_time!=Number(localStorage.createdTime)&&(localStorage.createdTime=o[t].create_time,localStorage.numIndex=-1,location.href="index.html")}})},1e3)})},onPlay:function(){var e=this,o=null;this.video=this.$refs.video,o=setInterval(function(){e.$refs.showTime.style.display="block",e.lastTime=Math.floor(e.video.duration-e.video.currentTime);var t=Math.floor(e.lastTime/60)+"";1===t.length&&(t="0"+t);var i=e.lastTime%60+"";1===i.length&&(i="0"+i),e.lastTimeText="还有"+t+"分"+i+"秒结束",0===e.lastTime&&(clearInterval(o),location.href="index.html")},1e3)},nofind:function(){location.href="index.html"}},components:{}}},function(e,o,t){"use strict";Object.defineProperty(o,"__esModule",{value:!0});var i=t(0),a=t(4),n=t(3),r=t.n(n);t(2);i.a.use(a.a),i.a.config.productionTip=!1,i.a.http.options.emulateHTTP=!0,i.a.http.options.emulateJSON=!0,new i.a({el:"#app",template:"<App/>",components:{App:r.a}})},function(e,o){},function(e,o){},,,,,function(e,o){e.exports={render:function(){var e=this,o=e.$createElement,t=e._self._c||o;return t("div",{attrs:{id:"app"}},[t("div",{staticClass:"main"},[t("video",{ref:"video",staticClass:"video",attrs:{id:"my-player",autoplay:""},on:{canplaythrough:e.onPlay}},[0!==e.videoArr.length&&"video"==e.videoArr[e.videoNum].type?t("source",{attrs:{src:"/www/static/media/"+e.videoArr[e.videoNum].name,type:"video/mp4"},on:{error:e.nofind}}):e._e(),e._v(" "),0!==e.videoArr.length&&"movie"==e.videoArr[e.videoNum].type?t("source",{attrs:{src:e.videoArr[e.videoNum].nameurl,type:"video/mp4"},on:{error:e.nofind}}):e._e()]),e._v(" "),t("div",{ref:"showTime",staticClass:"showTime"},[t("font",{attrs:{id:"hints"}},[e._v(e._s(e.lastTimeText))])],1)])])},staticRenderFns:[]}},,,function(e,o){}],[6]);