# OkHttpUtils

### 封装了okhttp的网络框架，支持大文件上传下载，上传进度回调，下载进度回调，表单上传（多文件和多参数一起上传），链式调用，可以自定义返回对象，支持Https和自签名证书，支持cookie自动管理，支持四种缓存模式缓存网络数据，扩展了统一的上传管理和下载管理功能

该项目参考了以下项目：

 * [https://github.com/hongyangAndroid/okhttp-utils](https://github.com/hongyangAndroid/okhttp-utils)  
 * [https://github.com/Y0LANDA/NoHttp](https://github.com/Y0LANDA/NoHttp) 
 * [https://github.com/wyouflf/xUtils](https://github.com/wyouflf/xUtils) 

在此特别感谢上述作者，喜欢原作的可以去使用原项目。同时欢迎大家下载体验本项目，如果使用过程中遇到什么问题，欢迎反馈。

## 联系方式
 * 邮箱地址： liaojeason@126.com
 * QQ群： 489873144 （建议使用QQ群，邮箱使用较少，可能看的不及时）
 * 本群刚建立，旨在为使用我的github项目的人提供方便，如果遇到问题欢迎在群里提问。个人能力也有限，希望一起学习一起进步。

## 演示
 ![image](http://7xss53.com2.z0.glb.clouddn.com/okhttputils/demo1.png)![image](http://7xss53.com2.z0.glb.clouddn.com/okhttputils/demo2.gif)![image](http://7xss53.com2.z0.glb.clouddn.com/okhttputils/demo3.gif)![image](http://7xss53.com2.z0.glb.clouddn.com/okhttputils/demo4.gif)![image](http://7xss53.com2.z0.glb.clouddn.com/okhttputils/demo5.gif)


## 1.用法

 * 对于Android Studio的用户，可以选择添加:
```java
    compile 'com.lzy.net:okhttputils:1.0.0'  //可以单独使用，不需要依赖下方的扩展包
	compile 'com.lzy.net:okhttpserver:0.1.0' //扩展了下载管理和上传管理，根据需要添加
```
 * 或者使用
```java
    compile project(':okhttputils')
	compile project(':okhttpserver')
```
* 对于Eclipse的用户，可以选择添加 `/lib` 目录下的:
```java
	okhttputils-1.0.0.jar
	okhttpserver-0.1.0.jar
```


其中的图片选择是我的另一个开源项目，完全仿微信的图片选择库，自带 矩形图片裁剪 和 圆形图片裁剪 功能，有需要的可以去下载使用，附上地址：[https://github.com/jeasonlzy0216/ImagePicker](https://github.com/jeasonlzy0216/ImagePicker)
	
## 2.使用注意事项

 * `okhttputils`使用的`okhttp`的版本是最新的 3.2.0 版本，和以前的 2.x 的版本可能会存在冲突。
 * `okhttpserver`是对`okhttputils`的扩展，统一了下载管理和上传管理，对项目有需要做统一下载的可以考虑使用该扩展，不需要的可以直接使用`okhttputils`即可。
 * 对于缓存模式使用，所有`javaBean`必须实现`Serializable`接口

## 3.目前支持
* 一般的 get,post,put,delete,head,options请求
* 基于Post的大文本数据上传
* 多文件和多参数统一的表单上传
* 大文件下载和下载进度回调
* 大文件上传和上传进度回调
* 支持session的保持
* 支持四种本地缓存模式
* 支持链式调用
* 支持可信证书和自签名证书的https的访问
* 支持根据Tag取消请求
* 支持自定义泛型Callback，自动根据泛型返回对象

## 4.扩展功能
 * 统一的文件下载管理：默认使用的是 get 请求，同时下载数量为3个，支持断点下载，断点信息使用`ORMLite`数据库框架保存，默认下载路径`/storage/emulated/0/download`，下载路径和下载数量都可以在代码中配置，下载管理使用了服务提高线程优先级，避免后台下载时被系统回收
 * 统一的文件上传管理：默认使用的是 post 上传请求，该上传管理为简单管理，不支持断点续传和分片上传，只是简单的将所有上传任务使用线程池进行了统一管理，默认同时上传数量为1个

## 5.用法示例

### 6.1 全局配置
一般在 Aplication，或者基类中，只需要调用一次即可，可以配置调试开关，全局的超时时间，公共的请求头和请求参数等信息
```java
    public void onCreate() {
        super.onCreate();

        System.setProperty("http.proxyHost", "192.168.1.108");
        System.setProperty("http.proxyPort", "8888");

        OkHttpUtils.debug(true, "MyOkHttp");    //是否打开调试
        try {
            OkHttpUtils.getInstance()//
                    .setConnectTimeout(OkHttpUtils.DEFAULT_MILLISECONDS)//全局的连接超时时间
                    .setReadTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)//全局的读取超时时间
                    .setWriteTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)//全局的写入超时时间
                     //.setCertificates(getAssets().open("srca.cer"), getAssets().open("zhy_server.cer"))//
                    .setCertificates(new Buffer().writeUtf8(CER_12306).inputStream());//设置自签名网站的证书

            RequestHeaders headers = new RequestHeaders();
            headers.put("aaa", "111");
            headers.put("bbb", "222");
            OkHttpUtils.getInstance().addCommonHeader(headers); //全局公共头

            RequestParams params = new RequestParams();
            params.put("ccc", "333");
            params.put("ddd", "444");
            OkHttpUtils.getInstance().addCommonParams(params);  //全局公共参数
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

### 6.2 普通的GET请求，根据泛型Bean返回值也是Bean

```java
	private void getJson() {
        OkHttpUtils.get("http://192.168.1.111:8080/UploadServer/ResponseJson")//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .execute(new MyBeanCallBack<Bean>() {
                    @Override
                    public void onResponse(Bean bean) {
                        System.out.println("onResponse:" + bean);
                    }
                });
    }
```

### 6.3 普通的POST请求，根据泛型Bean返回值也是Bean
```java
	private void responseJson() {
        OkHttpUtils.post("http://192.168.1.111:8080/UploadServer/ResponseJson")//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .execute(new MyBeanCallBack<Bean>() {
                    @Override
                    public void onResponse(Bean bean) {
                        System.out.println("onResponse:" + bean);
                    }
                });
    }
```
### 6.4 普通Post，直接上传String类型的文本
不建议这么用，该方法上传字符串会清空实体中其他所有的参数，但头信息不清除，例如本例中的 params 参数不会上传
```java
	private void postString() {
        OkHttpUtils.post("http://192.168.1.111:8080/UploadServer/UploadString")//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .content("asdfasdfad这是文本这是文本aasfesr")//
                .mediaType(PostRequest.MEDIA_TYPE_PLAIN)//
                .execute(new MyBeanCallBack<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("onResponse:" + s);
                    }
                });
    }
```
如果要上传Json，把上面的 mediaType 改为  `.mediaType(PostRequest.MEDIA_TYPE_JSON)`

### 6.5 表单Post，同时上传多文件和多参数（推荐使用）
```java
	private void uploadFile() {
        OkHttpUtils.post("http://192.168.1.111:8080/UploadServer/UploadFile")//
                .tag(this)//
                .headers("aaa", "111")
                .headers("bbb", "222")
                .params("ccc", "333")
                .params("ddd", "444")
                .params("file1", new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20151225_155549.jpg"))//
                .params("file2", new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20160109_010308.jpg"))//
                .params("file3", new File(Environment.getExternalStorageDirectory() + "/video/splash.avi"))//
                .execute(new MyBeanCallBack<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("onResponse:" + s);
                    }
                });
    }
```
其中文件的`key`，相当于表单中`<input type="file" name="File1"/>`的name属性。

### 6.6 下载文件，get和post都可以，这里使用get演示
```java
	OkHttpUtils.get("http://192.168.1.111:8080/UploadServer/DownloadFile")//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .execute(new MyFileCallBack(Environment.getExternalStorageDirectory() + "/video", "bbb.avi") {
                    @Override
                    public void onResponse(File response) {
                        System.out.println("onResponse:" + response);
                    }
                });
```
具体`FileCallBack`内部实现的下载进度监听，自行看代码

### 6.7 根据tag取消请求

目前对于支持的方法都添加了最后一个参数`Object tag`，取消则通过` OkHttpUtils.cancel(tag)`执行。

例如：在Activity中，当Activity销毁取消请求，可以在onDestory里面统一取消。
```java
	  @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }
```


### 6.8 自定义CallBack

目前内部提供的包含`JsonCallBack`, `StringCallBack`, `FileCallBack`, `BitmapCallback`，可以根据自己的需求去自定义Callback

#### 其中`JsonCallBack`使用比较多，它支持传递一个泛型，将返回的`Response`对象解析成需要的类型并且返回，目前支持：

* 一般的 JavaBean
* 字符串 String 
* 集合泛型 `List<Bean>`
* 集合泛型 `Map<Bean>`


以下是实现代码
```java
	public abstract class JsonCallBack<T> extends AbsCallback<T> {

    @Override
    public T parseNetworkResponse(Response response) throws Exception {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            //如果用户写了泛型，就会进入这里，否者不会执行
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type beanType = parameterizedType.getActualTypeArguments()[0];
            if (beanType == String.class) {
                //如果是String类型，直接返回字符串
                return (T) response.body().string();
            } else {
                //如果是 Bean List Map ，则解析完后返回
                return new Gson().fromJson(response.body().string(), beanType);
            }
        } else {
            //如果没有写泛型，直接返回Response对象
            return (T) response;
        }
    }
}
```
通过`parseNetworkResponse `回调的response进行解析，该方法运行在子线程，所以可以进行任何耗时操作


### 6.9 上传下载的进度显示
```java
	public abstract class AbsCallback<T> {
	    /** Post执行上传过程中的进度回调，get请求不回调，UI线程 */
	    public void upProgress(long currentSize, long totalSize, float progress) {
	    }
	
	    /** 执行下载过程中的进度回调，UI线程 */
	    public void downloadProgress(long currentSize, long totalSize, float progress) {
	    }
	}
```
callback回调中有`upProgress` 和 `downloadProgress` 方法，直接复写即可，在 `get` 请求中， `upProgress` 方法不会执行。

### 6.10 同步的请求
```java
	Response response = OkHttpUtils.get("http://www.baidu.com")//
                .tag(this)//
                .headers("aaa", "111")//
                .params("bbb", "222").execute();
```
execute方法不传入callback即为同步的请求，返回`Response`对象，需要自己解析

### 6.11 为单个请求设置超时

比如涉及到文件的需要设置读写等待时间多一点。
```java
	  private void responseJsonArray() {
        OkHttpUtils.post("http://192.168.1.111:8080/UploadServer/ResponseJsonArray")//
                .tag(this)//
                .connTimeOut(2000)
                .writeTimeOut(3000)
                .readTimeOut(4000)
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .execute(new MyBeanCallBack<List<Bean>>() {
                    @Override
                    public void onResponse(List<Bean> beans) {
                        System.out.println("onResponse:" + beans);
                    }
                });
    }
```
### 6.12 自签名网站https的访问

非常简单，拿到xxx.cert的证书，然后调用
```java
	OkHttpUtils.getInstance().setCertificates(inputstream);
```

建议使用方式，例如我的证书放在assets目录：
```java
	  try {
        OkHttpUtils.getInstance()
               .setCertificates(getAssets().open("srca.cer"), getAssets().open("aaa.cer"))//
    } catch (Exception e) {
        e.printStackTrace();
    }
```
