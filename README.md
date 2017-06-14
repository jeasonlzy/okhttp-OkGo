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
 * QQ群： 489873144 （建议使用QQ群，邮箱使用较少，可能看的不及时）
 * 如果遇到问题欢迎在群里提问，个人能力也有限，希望一起学习一起进步。

## 演示
![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo13.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo8.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo11.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo9.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo10.gif)![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/demo12.gif)

## 使用

Android Studio用户

> 一般来说，只需要添加第一个okgo的核心包即可，其余的三个库根据自己的需要选择添加！！！

```java
//必须使用
compile 'com.lzy.net:okgo:3.0.1'

//以下三个选择添加，okrx和okrx2不能同时使用
compile 'com.lzy.net:okrx:1.0.1'
compile 'com.lzy.net:okrx2:2.0.1'  
compile 'com.lzy.net:okserver:2.0.1'
```

Eclipse的用户(赶紧换AS吧)，可以选择添加本项目根目录中 `/jar` 目录下的jar包:

> 一般来说，至少需要okhttp、okio、okgo三个jar包，其余的三个扩展jar包根据自己的需要选择添加！！！

必须使用
> [okhttp-3.8.0.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okhttp-3.8.0.jar)  
[okio-1.13.0.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okio-1.13.0.jar)   
[okgo-3.0.1.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okgo-3.0.1.jar)   

以下三个选择添加，okrx和okrx2不能同时使用
> [okrx-1.0.1.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okrx-1.0.1.jar)  
[okrx2-2.0.1.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okrx2-2.0.1.jar)   
[okserver-2.0.1.jar](https://raw.githubusercontent.com/jeasonlzy/okhttp-OkGo/master/jar/okserver-2.0.1.jar)  

## 文档
### 该项目的文档全部以Wiki的形式展示，重要的事情说三遍
- [Wiki文档首页请猛戳这里](https://github.com/jeasonlzy/okhttp-OkGo/wiki)
- [Wiki文档首页请猛戳这里](https://github.com/jeasonlzy/okhttp-OkGo/wiki)
- [Wiki文档首页请猛戳这里](https://github.com/jeasonlzy/okhttp-OkGo/wiki)

如果你不想编译项目，提供了apk供直接运行，方便查看效果，[点击下载okgo_v3.0.1.apk](http://7xss53.com1.z0.glb.clouddn.com/file/okgo_v3.0.1.apk)

本项目Demo使用的是我自己的服务器，有时候可能不稳定，网速比较慢时请耐心等待，尴尬呀。。

如果你想查看历史版本信息，[请点击历史版本](https://github.com/jeasonlzy/okhttp-OkGo/releases)。

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

