![](http://7xss53.com1.z0.glb.clouddn.com/markdown/aqyyw.jpg)

## OkGo - 3.0 震撼来袭, 一个基于okhttp的标准RESTful风格的网络框架

> 工程结构全新优化  
支持RxJava  
支持RxJava2  
支持自定义缓存策略  
支持下载管理  
支持上传管理  

该库部分思想借鉴了以下项目：

 * [https://github.com/yanzhenjie/NoHttp](https://github.com/Y0LANDA/NoHttp) 
 * [https://github.com/square/retrofit](https://github.com/square/retrofit)

在此特别感谢上述作者，喜欢原作的可以去使用原项目。同时欢迎大家下载体验本项目，如果使用过程中遇到什么问题，欢迎反馈。

## 友情链接
本项目中使用的图片选择是我的另一个开源项目
> 完全仿微信的图片选择库，自带矩形图片裁剪和圆形图片裁剪功能，有需要的可以去下载使用。  
附上地址：[https://github.com/jeasonlzy/ImagePicker](https://github.com/jeasonlzy/ImagePicker)

本项目中的九宫格控件也是我的开源项目
> 类似QQ空间，微信朋友圈，微博主页等，展示图片的九宫格控件，自动根据图片的数量确定图片大小和控件大小，使用Adapter模式设置图片，对外提供接口回调，使用接口加载图片，支持任意的图片加载框架如：Glide、ImageLoader、xUtils3、Picasso 等，支持点击图片全屏预览大图。  
附上地址：[https://github.com/jeasonlzy/NineGridView](https://github.com/jeasonlzy/NineGridView)

## 联系方式
 * email： liaojeason@126.com
 * QQ群： 489873144 <a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=ba5dbb5115a165866ec77d96cb46685d1ad159ab765b796699d6763011ffe151"><img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="Android 格调小窝" title="Android 格调小窝"></a>（点击图标，可以直接加入，建议使用QQ群，邮箱使用较少，可能看的不及时）
 * 如果遇到问题欢迎在群里提问，个人能力也有限，希望一起学习一起进步。

## 演示
![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo13.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo8.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo11.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo9.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo10.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo12.gif)

## 未来版本
### [v3.1.x]版本
- 计划分离params参数的具体作用，分为paramsPath，paramsQuery和params，支持url路径动态替换
- 计划支持请求优先级，方便有些重要请求优先进行
- 计划支持自定义线程池，使用自己的线程池管理网络请求

### [v3.2.x]版本
- 计划增加扩展库OkAnno，作用是让okgo支持注解方式请求，具体写法与Retrofit相似，但是更简单方便，也更强大，方便Retrofit用户平滑过渡到OkGo

### 其他功能暂时还没想出来，大家有想法的可以积极加群讨论，或者直接在issue里面提出你的想法，我会第一时间回复。

## 使用

[![](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14) [![](https://img.shields.io/badge/platform-android-brightgreen.svg)](https://developer.android.com/index.html) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/5b244560c35c445cbb00b9500b0c5d2a)](https://www.codacy.com/app/jeasonlzy/okhttp-OkGo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jeasonlzy/okhttp-OkGo&amp;utm_campaign=Badge_Grade)  [![](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/jeasonlzy/okhttp-OkGo/blob/master/LICENSE)  [![](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-jeasonlzy-orange.svg)](https://github.com/jeasonlzy)

[![](https://img.shields.io/badge/OkGo-v3.0.4-brightgreen.svg)](https://github.com/jeasonlzy/okhttp-OkGo) [![](https://img.shields.io/badge/OkRx-v1.0.2-brightgreen.svg)](https://github.com/jeasonlzy/okhttp-OkGo) [![](https://img.shields.io/badge/OkRx2-v2.0.2-brightgreen.svg)](https://github.com/jeasonlzy/okhttp-OkGo) [![](https://img.shields.io/badge/OkServer-v2.0.5-brightgreen.svg)](https://github.com/jeasonlzy/okhttp-OkGo)

Android Studio用户

> 一般来说，只需要添加第一个okgo的核心包即可，其余的三个库根据自己的需要选择添加！！！

```java
//必须使用
compile 'com.lzy.net:okgo:3.0.4'

//以下三个选择添加，okrx和okrx2不能同时使用
compile 'com.lzy.net:okrx:1.0.2'
compile 'com.lzy.net:okrx2:2.0.2'  
compile 'com.lzy.net:okserver:2.0.5'
```

Eclipse的用户(赶紧换AS吧)，可以选择添加本项目根目录中 `/jar` 目录下的jar包:

> 一般来说，至少需要okhttp、okio、okgo三个jar包，其余的三个扩展jar包根据自己的需要选择添加！！！

必须使用
> [okhttp-3.8.1.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okhttp-3.8.1.jar)  
[okio-1.13.0.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okio-1.13.0.jar)   
[okgo-3.0.4.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okgo-3.0.4.jar)   

以下三个选择添加，okrx和okrx2不能同时使用
> [okrx-1.0.2.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okrx-1.0.2.jar)  
[okrx2-2.0.2.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okrx2-2.0.2.jar)   
[okserver-2.0.5.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okserver-2.0.5.jar)  

## 文档
### 该项目的文档全部以Wiki的形式展示，wiki文档永远与最新版本的库保持同步，如果你发现文档的说明与你的写法不一样，那么请升级到最新版本，重要的事情说三遍
- [点我，点我，我是3.x文档，Wiki文档首页请猛戳这里](https://github.com/jeasonlzy/okhttp-OkGo/wiki)
- [点我，点我，我是3.x文档，Wiki文档首页请猛戳这里](https://github.com/jeasonlzy/okhttp-OkGo/wiki)
- [点我，点我，我是3.x文档，Wiki文档首页请猛戳这里](https://github.com/jeasonlzy/okhttp-OkGo/wiki)

如果你实在不愿意升级到3.x版本，[这里有2.x版本的文档，点击查看](https://github.com/jeasonlzy/okhttp-OkGo/tree/v2.1.4)，注意：老版本库的问题将不在维护，所有bug会在最新版本修复，所以建议跟随最新版本的库。

[![](http://7xss53.com1.z0.glb.clouddn.com/markdown/w0ujl.jpg)](https://github.com/jeasonlzy/okhttp-OkGo/wiki)

如果遇到使用问题，解决办法如下：
1. 看上述文档中是否有相关描述
2. 看别人提的issues是否有你的问题，这里面有很多人的提问，[点击这里看别人的提问](https://github.com/jeasonlzy/okhttp-OkGo/issues?q=is%3Aissue+is%3Aclosed)。
3. 如果你感觉是bug，或者有疑问，也欢迎在issues里面提问，我每天都会认真解答，[点击这里提问](https://github.com/jeasonlzy/okhttp-OkGo/issues)。
4. 还有疑问，加入联系方式中的QQ群，大家一起讨论。

如果你不想编译项目，提供了apk供直接运行，方便查看效果，点击图标下载：[![](https://img.shields.io/badge/downloads-okgo__v3.0.4.apk-blue.svg)](http://7xss53.com1.z0.glb.clouddn.com/file/okgo_v3.0.4.apk)

本项目Demo使用的是我自己的服务器，有时候可能不稳定，网速比较慢时请耐心等待，尴尬呀。。

如果你想查看历史版本信息，请点击图标：[![](https://img.shields.io/badge/release-tags-ff69b4.svg)](https://github.com/jeasonlzy/okhttp-OkGo/releases)

如果你使用遇到了问题，首先请看控制台log，如果log无法看出问题，无法确定是该库的bug还是服务端的问题，建议抓包查看网络数据，[详细的抓包方法猛戳这里](https://github.com/jeasonlzy/okhttp-OkGo/wiki#网络抓包)

### 如果你觉得好，对你有过帮助，请给我一点打赏鼓励吧，一分也是爱呀！
![](https://ws2.sinaimg.cn/large/006tNbRwly1fgidan2gc9j30jg0a2wg6.jpg)

## 混淆
okgo, okrx, okrx2, okserver 所有代码均可以混淆,但是由于底层使用的是 okhttp,它不能混淆,所以只需要添加以下混淆代码就可以了
```java
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}
```

当然如果你确实不需要混淆okgo的代码,可以继续添加以下代码
```java
#okgo
-dontwarn com.lzy.okgo.**
-keep class com.lzy.okgo.**{*;}

#okrx
-dontwarn com.lzy.okrx.**
-keep class com.lzy.okrx.**{*;}

#okrx2
-dontwarn com.lzy.okrx2.**
-keep class com.lzy.okrx2.**{*;}

#okserver
-dontwarn com.lzy.okserver.**
-keep class com.lzy.okserver.**{*;}
```

## Licenses
```
 Copyright 2016 jeasonlzy(廖子尧)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
```



