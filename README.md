# OkHttpUtils

### 封装了okhttp的网络框架，支持大文件上传下载，上传进度回调，下载进度回调，表单上传（多文件和多参数一起上传），链式调用，可以自定义返回对象，支持Https和自签名证书，支持cookie自动管理，支持四种缓存模式缓存网络数据，支持301、302重定向，扩展了统一的上传管理和下载管理功能

该项目参考了以下项目：

 * [https://github.com/hongyangAndroid/okhttp-utils](https://github.com/hongyangAndroid/okhttp-utils)  
 * [https://github.com/yanzhenjie/NoHttp](https://github.com/Y0LANDA/NoHttp) 
 * [https://github.com/wyouflf/xUtils](https://github.com/wyouflf/xUtils) 

在此特别感谢上述作者，喜欢原作的可以去使用原项目。同时欢迎大家下载体验本项目，如果使用过程中遇到什么问题，欢迎反馈。

## 联系方式
 * 邮箱地址： liaojeason@126.com
 * QQ群： 489873144 （建议使用QQ群，邮箱使用较少，可能看的不及时）
 * 本群旨在为使用我的github项目的人提供方便，如果遇到问题欢迎在群里提问。个人能力也有限，希望一起学习一起进步。

## 演示
 ![image](http://7xss53.com2.z0.glb.clouddn.com/okhttputils/demo1.png)![image](http://7xss53.com2.z0.glb.clouddn.com/okhttputils/demo2.gif)![image](http://7xss53.com2.z0.glb.clouddn.com/okhttputils/demo3.gif)![image](http://7xss53.com2.z0.glb.clouddn.com/okhttputils/demo4.gif)![image](http://7xss53.com2.z0.glb.clouddn.com/okhttputils/demo5.gif)


## 1.用法

> * 为了方便大家使用，更加通俗的理解http的网络协议，建议做网络请求的时候，对每个请求抓包后查看请求信息和响应信息。
> * 如果是 Windows 操作系统，可以使用 `Fiddler` 对手机的请求进行抓包查看。
> * 如果是 Mac OS  操作系统，可以使用 `Charles` 对手机的请求进行抓包查看。
> * 具体的下载地址和抓包配置方法，我这就不提供了，请自行百度或谷歌。


   对于Eclipse不能运行项目的，提供了apk供直接运行，位于项目根目录 `okhttputils_v1.x.x.apk`。

   本项目Demo的网络请求是我自己的服务器，有时候可能不稳定，网速比较慢时请耐心等待。。

 * 对于Android Studio的用户，可以选择添加:
```java
    compile 'com.lzy.net:okhttputils:1.6.5'  //可以单独使用，不需要依赖下方的扩展包
	compile 'com.lzy.net:okhttpserver:0.1.7' //扩展了下载管理和上传管理，根据需要添加

	compile 'com.lzy.net:okhttputils:+'  //版本号使用 + 可以自动引用最新版
	compile 'com.lzy.net:okhttpserver:+' //版本号使用 + 可以自动引用最新版
```
 * 或者使用
```java
    compile project(':okhttputils')
	compile project(':okhttpserver')
```
* 对于Eclipse的用户，可以选择添加 `/lib` 目录下的:
```java
	okhttputils-1.6.5.jar
	okhttpserver-0.1.7.jar
```

#### 其中的图片选择是我的另一个开源项目，完全仿微信的图片选择库，自带 矩形图片裁剪 和 圆形图片裁剪 功能，有需要的可以去下载使用，附上地址：[https://github.com/jeasonlzy0216/ImagePicker](https://github.com/jeasonlzy0216/ImagePicker)
	
## 2.使用注意事项
 * `okhttputils`使用的`okhttp`的版本是最新的 3.2.0 版本，和以前的 2.x 的版本可能会存在冲突。
 * `okhttpserver`是对`okhttputils`的扩展，统一了下载管理和上传管理，对项目有需要做统一下载的可以考虑使用该扩展，不需要的可以直接使用`okhttputils`即可。
 * 对于缓存模式使用，需要与返回对象相关的所有`javaBean`必须实现`Serializable`接口，否者会报`NotSerializableException`。
 * 使用缓存时，如果不指定`cacheKey`，默认是用url带参数的全路径名为`cacheKey`。
 * 使用该网络框架时，必须要在 Application 中做初始化 `OkHttpUtils.init(this);`。

## 3.目前支持
* 一般的 get,post,put,delete,head,options请求
* 基于Post的大文本数据上传
* 多文件和多参数统一的表单上传
* 支持一个key上传一个文件，也可以一个Key上传多个文件
* 大文件下载和下载进度回调
* 大文件上传和上传进度回调
* 支持cookie的内存存储和持久化存储，支持传递自定义cookie
* 支持304缓存协议，扩展三种本地缓存模式
* 支持301、302重定向
* 支持链式调用
* 支持可信证书和自签名证书的https的访问
* 支持根据Tag取消请求
* 支持自定义泛型Callback，自动根据泛型返回对象

## 4.扩展功能
 * 统一的文件下载管理：默认使用的是 get 请求，同时下载数量为3个，支持断点下载，断点信息使用`ORMLite`数据库框架保存，默认下载路径`/storage/emulated/0/download`，下载路径和下载数量都可以在代码中配置，下载管理使用了服务提高线程优先级，避免后台下载时被系统回收
 * 统一的文件上传管理：默认使用的是 post 上传请求，该上传管理为简单管理，不支持断点续传和分片上传，只是简单的将所有上传任务使用线程池进行了统一管理，默认同时上传数量为1个

## 一、全局配置
一般在 Aplication，或者基类中，只需要调用一次即可，可以配置调试开关，全局的超时时间，公共的请求头和请求参数等信息
```java
    @Override
    public void onCreate() {
        super.onCreate();

        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");    //所有的 header 都 不支持 中文
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");     //所有的 params 都 支持 中文
        params.put("commonParamsKey2", "这里支持中文参数");

        //必须调用初始化
        OkHttpUtils.init(this);
        //以下都不是必须的，根据需要自行选择
        OkHttpUtils.getInstance()//
                .debug("OkHttpUtils")                                              //是否打开调试
                .setConnectTimeout(OkHttpUtils.DEFAULT_MILLISECONDS)               //全局的连接超时时间
                .setReadTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                  //全局的读取超时时间
                .setWriteTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                 //全局的写入超时时间
			  //.setCookieStore(new MemoryCookieStore())                           //cookie使用内存缓存（app退出后，cookie消失）
			  //.setCookieStore(new PersistentCookieStore())                       //cookie持久化存储，如果cookie不过期，则一直有效
                .addCommonHeaders(headers)                                         //设置全局公共头
                .addCommonParams(params);                                          //设置全局公共参数
    }
```

## 二、普通请求
### 1.基本的网络请求
```java
OkHttpUtils.get(Urls.URL_METHOD)     // 请求方式和请求url
	.tag(this)                       // 请求的 tag, 主要用于取消对应的请求
	.cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
	.cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
	.execute(new JsonCallback<RequestInfo>(RequestInfo.class) {
	    @Override
	    public void onResponse(boolean isFromCache, RequestInfo requestInfo, Request request, @Nullable Response response) {
			// requestInfo 对象即为所需要的结果对象
	    }
	});
```
### 2.请求 Bitmap 对象
```java
OkHttpUtils.get(Urls.URL_IMAGE)//
	.tag(this)//
	.execute(new BitmapCallback() {
	    @Override
	    public void onResponse(boolean isFromCache, Bitmap bitmap, Request request, @Nullable Response response) {
		// bitmap 即为返回的图片数据
	    }
	});
```
### 3.请求 文件下载
```java
OkHttpUtils.get(Urls.URL_DOWNLOAD)//
	.tag(this)//
	.execute(new FileCallback("/sdcard/temp/", "file.jpg") {  //文件下载时，需要指定下载的文件目录和文件名
	    @Override
	    public void onResponse(boolean isFromCache, File file, Request request, @Nullable Response response) {
		// file 即为文件数据，文件保存在指定布幕
	    }
	});
```
### 4.普通Post，直接上传String类型的文本
一般此种用法用于与服务器约定的数据格式，当使用该方法时，params中的参数设置是无效的，所有参数均需要通过需要上传的文本中指定，此外，额外指定的header参数仍然保持有效。
```java
OkHttpUtils.post(Urls.URL_TEXT_UPLOAD)//
	.tag(this)//
	.postString("这是要上传的长文本数据！")//
	.execute(new StringCallback() {
	    @Override
	    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
			//上传成功
	    }
	});
```

### 5.普通Post，直接上传Json类型的文本
该方法与postString没有本质区别，只是数据格式是json,一般来说，需要自己创建一个实体bean或者一个map，把需要的参数设置进去，然后通过三方的Gson或者fastjson转换成json字符串，最后直接使用该方法提交到服务器。
```java
HashMap<String, String> params = new HashMap<>();
params.put("key1", "value1");
params.put("key2", "这里是需要提交的json格式数据");
params.put("key3", "也可以使用三方工具将对象转成json字符串");
params.put("key4", "其实你怎么高兴怎么写都行");
JSONObject jsonObject = new JSONObject(params);
        
OkHttpUtils.post(Urls.URL_TEXT_UPLOAD)//
	.tag(this)//
	.postJson(jsonObject.toString())//
	.execute(new StringCallback() {
	    @Override
	    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
			//上传成功
	    }
	});
```

### 6.请求功能的所有配置讲解

以下代码包含了以下内容：

 * 一次普通请求所有能配置的参数，真实使用时不需要配置这么多，按自己的需要选择性的使用即可
 * 多文件和多参数的表单上传，同时支持进度监听
 * 自签名网站https的访问，调用`setCertificates`方法即可
 * 为单个请求设置超时，比如涉及到文件的需要设置读写等待时间多一点。
 * Cookie一般情况下只需要在初始化的时候调用`setCookieStore`即可实现cookie的自动管理，如果特殊业务需要，需要手动额外向服务器传递自定义的cookie，可以在每次请求的时候调用`addCookie`方法，该方法提供了3个重载形式，可以根据自己的需要选择使用。

```java
OkHttpUtils.get(Urls.URL_METHOD) // 请求方式和请求url, get请求不需要拼接参数，支持get，post，put，delete，head，options请求
    .tag(this)               // 请求的 tag, 主要用于取消对应的请求
    .connTimeOut(10000)      // 设置当前请求的连接超时时间
    .readTimeOut(10000)      // 设置当前请求的读取超时时间
    .writeTimeOut(10000)     // 设置当前请求的写入超时时间
    .cacheKey("cacheKey")    // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
    .cacheTime(5000)         // 缓存的过期时间,单位毫秒
    .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST) // 缓存模式，详细请看第四部分，缓存介绍
    .setCertificates(getAssets().open("srca.cer")) // 自签名https的证书，可变参数，可以设置多个
	.addInterceptor(interceptor)            // 添加自定义拦截器
    .headers("header1", "headerValue1")     // 添加请求头参数
    .headers("header2", "headerValue2")     // 支持多请求头参数同时添加
    .params("param1", "paramValue1")        // 添加请求参数
    .params("param2", "paramValue2")        // 支持多请求参数同时添加
    .params("file1", new File("filepath1")) // 可以添加文件上传
    .params("file2", new File("filepath2")) // 支持多文件同时添加上传
	.addUrlParams("key", List<String> values) 									//这里支持一个key传多个参数
	.addFileParams("key", List<File> files)										//这里支持一个key传多个文件
	.addFileWrapperParams("key", List<HttpParams.FileWrapper> fileWrappers)		//这里支持一个key传多个文件
	.addCookie("aaa", "bbb")				// 这里可以传递自己想传的Cookie
    .addCookie(cookie)						// 可以自己构建cookie
    .addCookies(cookies)					// 可以一次传递批量的cookie
     //这里给出的泛型为 RequestInfo，同时传递一个泛型的 class对象，即可自动将数据结果转成对象返回
    .execute(new DialogCallback<RequestInfo>(this, RequestInfo.class) {
		@Override
		public void onBefore(BaseRequest request) {
		    // UI线程 请求网络之前调用
		    // 可以显示对话框，添加/修改/移除 请求参数
		}
	
		@Override
		public RequestInfo parseNetworkResponse(Response response) throws Exception{
		    // 子线程，可以做耗时操作
		    // 根据传递进来的 response 对象，把数据解析成需要的 RequestInfo 类型并返回
			// 可以根据自己的需要，抛出异常，在onError中处理
		    return null;
		}
	
		@Override
		public void onResponse(boolean isFromCache, RequestInfo requestInfo, Request request, @Nullable Response response) {
		    // UI 线程，请求成功后回调
		    // isFromCache 表示当前回调是否来自于缓存
		    // requestInfo 返回泛型约定的实体类型参数
		    // request     本次网络的请求信息，如果需要查看请求头或请求参数可以从此对象获取
		    // response    本次网络访问的结果对象，包含了响应头，响应码等，如果数据来自于缓存，该对象为null
		}
	
		@Override
		public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
		    // UI 线程，请求失败后回调
		    // isFromCache 表示当前回调是否来自于缓存
		    // call        本次网络的请求对象，可以根据该对象拿到 request
		    // response    本次网络访问的结果对象，包含了响应头，响应码等，如果网络异常 或者数据来自于缓存，该对象为null
		    // e           本次网络访问的异常信息，如果服务器内部发生了错误，响应码为 400~599之间，该异常为 null
		}
	
		@Override
		public void onAfter(boolean isFromCache, @Nullable RequestInfo requestInfo, Call call, @Nullable Response response, @Nullable Exception e) {
		    // UI 线程，请求结束后回调，无论网络请求成功还是失败，都会调用，可以用于关闭显示对话框
		    // isFromCache 表示当前回调是否来自于缓存
		    // requestInfo 返回泛型约定的实体类型参数，如果网络请求失败，该对象为　null
		    // call        本次网络的请求对象，可以根据该对象拿到 request
		    // response    本次网络访问的结果对象，包含了响应头，响应码等，如果网络异常 或者数据来自于缓存，该对象为null
		    // e           本次网络访问的异常信息，如果服务器内部发生了错误，响应码为 400~599之间，该异常为 null
		}
	
		@Override
		public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
		    // UI 线程，文件上传过程中回调，只有请求方式包含请求体才回调（GET,HEAD不会回调）
		    // currentSize  当前上传的大小（单位字节）
		    // totalSize 　 需要上传的总大小（单位字节）
		    // progress     当前上传的进度，范围　0.0f ~ 1.0f
		    // networkSpeed 当前上传的网速（单位秒）
		}
	
		@Override
		public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
		    // UI 线程，文件下载过程中回调
		    //参数含义同　上传相同
		}
    });
```
### 7.取消请求
每个请求前都设置了一个参数`tag`，取消则通过` OkHttpUtils.cancel(tag)`执行。
例如：在Activity中，当Activity销毁取消请求，可以在onDestory里面统一取消。
```java
	@Override
	protected void onDestroy() {
	    super.onDestroy();

		//根据 Tag 取消请求
	    OkHttpUtils.getInstance().cancelTag(this);
	}
```
### 8.同步的请求
execute方法不传入callback即为同步的请求，返回`Response`对象，需要自己解析
```java
	Response response = OkHttpUtils.get("http://www.baidu.com")//
					                .tag(this)//
					                .headers("aaa", "111")//
					                .params("bbb", "222")
									.execute();
```

## 三、自定义CallBack使用

目前内部提供的包含`AbsCallback`, `StringCallBack` ,`BitmapCallback` ,`FileCallBack` ,可以根据自己的需求去自定义Callback

 * `AbsCallback`: 所有回调的父类，抽象类
 * `StringCallBack`：如果返回值类型是纯文本数据，即可使用该回调
 * `BitmapCallback`：如果请求的是图片数据，则可以使用该回调
 * `FileCallBack`：如果要做文件下载，则必须使用该回调，内部封装了关于文件下载进度回调的方法

###该网络框架的核心使用方法即为`Callback`的继承使用，详细请看 Demo 源码中`callback`包下的代码。
因为不同的项目需求，可能对数据格式进行了不同的封装，于是在 Demo 中的进行了详细的代码示例，以下是详细介绍：

 * `CommonCallback`:继承自`AbsCallback`,主要作用是做全局共同请求参数的添加，同样也可以在第一步全局配置的时候设置，效果一样。
 * `EncryptCallback`：继承自`CommonCallback`,主要功能是做 Url 参数加密，对每个请求的参数进行编码，防止拦截数据包，篡改数据。
 * `JsonCallback`：继承自`EncryptCallback`,一般来说，服务器返回的响应码都包含 code，msg，data 三部分，在此根据自己的业务需要完成相应的逻辑判断，并对数据进行解析，可以使用 `Gson` 或者 `fastjson`，将解析的对象返回。
 * `DialogCallback`：继承自`JsonCallback`,对需要在网络请求的时候显示对话框，使用该回调。
 * `StringDialogCallback`：继承自`EncryptCallback`,如果网络返回的数据只是纯文本，使用该回调
 * `BitmapDialogCallback` ：继承自`BitmapCallback`,如果网络返回的是Bitmap对象，使用该回调
 * `DownloadFileCallBack` ：继承自`FileCallback`,如果需要做文件下载，使用该回调

以上基本是包含了大部分的业务逻辑，具体情况请参照demo示例，根据业务需求修改！

## 四、缓存的使用

###使用缓存前，必须让缓存的数据`javaBean`对象实现`Serializable`接口，否者会报`NotSerializableException`。
因为缓存的原理是将对象序列化后直接写入 数据库中，如果不实现`Serializable`接口，会导致对象无法序列化，进而无法写入到数据库中，也就达不到缓存的效果。

对于`DEFAULT`缓存模式,超时时间是无效的,因为该模式是完全遵循标准的http协议的,缓存时间是依靠服务端响应头来控制,所以客户端的cacheTime参数无效

目前提供了五种`CacheMode`缓存模式

 * `NO_CACHE`: 不使用缓存,该模式下,`cacheKey`,`cacheTime` 参数均无效
 * `DEFAULT`: 按照HTTP协议的默认缓存规则，例如有304响应头时缓存
 * `REQUEST_FAILED_READ_CACHE`：先请求网络，如果请求网络失败，则读取缓存，如果读取缓存失败，本次请求失败。该缓存模式的使用，会根据实际情况，导致`onResponse`,`onError`,`onAfter`三个方法调用不只一次，具体请在三个方法返回的参数中进行判断。
 * `IF_NONE_CACHE_REQUEST`：如果缓存不存在才请求网络，否则使用缓存。
 * `FIRST_CACHE_THEN_REQUEST`：先使用缓存，不管是否存在，仍然请求网络，如果网络顺利，会导致`onResponse`方法执行两次，第一次`isFromCache`为true，第二次`isFromCache`为false。使用时根据实际情况，对`onResponse`,`onError`,`onAfter`三个方法进行具体判断。

###无论对于哪种缓存模式，都可以指定一个`cacheKey`，建议针对不同需要缓存的页面设置不同的`cacheKey`，如果相同，会导致数据覆盖。





