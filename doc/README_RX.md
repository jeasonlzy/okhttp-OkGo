 ![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/logo4.jpg)

# OkGo - Rx扩展

### 可以方便的和`RxJava`联用,如果你熟悉`Retrofit`,那么这个框架和`Retrofit`使用方式很像

## 联系方式
 * 邮箱地址： liaojeason@126.com
 * QQ群： 489873144 （建议使用QQ群，邮箱使用较少，可能看的不及时）
 * 本群旨在为使用我的github项目的人提供方便，如果遇到问题欢迎在群里提问。个人能力也有限，希望一起学习一起进步。

## OkRx目前支持
* 完美结合RxJava
* 比Retrofit更简单方便
* 网络请求和RxJava调用,一条链点到底
* 支持Json数据的自动解析转换
* OkGo包含的所有请求功能,OkRx全部支持

目前使用的`RxJava`版本如下
```java
    compile 'io.reactivex:rxjava:1.2.0'
    compile 'io.reactivex:rxandroid:1.2.1'
```

## 一.用法
### 0.最开始的配置
 `OkRx` 是 `OkGo` 的扩展,所以要想使用`OkRx`,那么请先按照`OkGo`的配置文档,做相应的初始化。
 
### 1.在gradle中添加一行依赖
```java
    compile 'com.lzy.net:okrx:0.1.0'  //Rx扩展
   
    或者
    
    compile 'com.lzy.net:okrx:+'      //使用+,引用最新版
```

### 2.调用请求代码
   我们还是像正常使用OkGo的方式一样,传入我们需要请求的Url,和我们需要的参数,那么最关键的一行就是最后调用`getCall()`这个方法。
   
   这里传入的两个参数进行一下说明:

 * 第一个参数是`Convert`对象,表示需要将服务器返回的数据流解析成什么对象,这里我们先用最简单的`String`做转换,`StringConvert`对象也是库中内置的转换器。
 * 第二个参数是`Adapter`对象,表示需要将解析的结果用什么对象包装,该参数可以省略不写,那么默认是`Call<T>`这个对象包装,当然,我们要使用Rx的调用,使用这个肯定是不行的,所以我们传入OkRx扩展的`RxAdapter`对象,他是使用的`Observable<T>`对象包装的，同样他需要一个泛型,该泛型必须和`Convert`的泛型一致,否则就发生了类型转换异常。
   
   以上两个参数具体的注意事项我们后续详细再说。
```java
 Observable<String> call = OkGo.post(Urls.URL_METHOD)//
                                    .headers("aaa", "111")//
                                    .params("bbb", "222")//
                                    .getCall(StringConvert.create(), RxAdapter.<String>create());
```

### 3.调用Rx转换代码
   现在我们已经获取了`Observable`对象了,熟悉`RxJava`的你难道还不会使用了吗,以下是简单的在请求前弹出loading,结束后展示信息的代码。
```java
call.doOnSubscribe(new Action0() {
        @Override
        public void call() {
            showLoading();  //开始请求前显示对话框
        }
    })//
    .observeOn(AndroidSchedulers.mainThread())//切换到主线程
    .subscribe(new Action1<String>() {
        @Override
        public void call(String s) {
            dismissLoading();               //请求成功,关闭对话框
            handleResponse(s, null, null);
        }
    }, new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            throwable.printStackTrace();
            dismissLoading();       //请求失败
            showToast("请求失败");
            handleError(null, null);
        }
    });
```
    
### 4.代码整合
 上面的调用是不是很简单,有人可能觉得链试代码太长,没关系,我们完全可以像Retrofit一样,自己写一个`ServerApi`类,这里面管理了所有的接口请求和参数,只是OkGo并不是采用的注解和反射实现的,而是通过传参来实现,相信对你你来讲,这样的方式更加直观。我们再将调用配合上`lambda`表达式,那么最后的结果是这样的:
 
 这样的请求方式有没有惊艳到你!!
```java
    OkGo.post(Urls.URL_METHOD)//
        .headers("aaa", "111")//
        .params("bbb", "222")//
        .getCall(StringConvert.create(), RxAdapter.<String>create())//以上为产生请求事件,请求默认发生在IO线程
        .doOnSubscribe(() -> {
            showLoading();  //开始请求前显示对话框
        })
        .observeOn(AndroidSchedulers.mainThread())//切换到主线程
        .subscribe(s -> {
            dismissLoading();               //请求成功,关闭对话框
            handleResponse(s, null, null);
        }, throwable -> {
            throwable.printStackTrace();
            dismissLoading();       //请求失败
            showToast("请求失败");
            handleError(null, null);
        });
```

### 5。其他请求 
* 如果你想请求`String`,那么将第`2`步中的`getCall`方法,就是你想要的。
```java
    getCall(StringConvert.create(), RxAdapter.<String>create())
```
 * 如果你想请求`Bitmap`,那么将第`2`步中的`getCall`方法,改成如下形式
```java
    getCall(BitmapConvert.create(), RxAdapter.<Bitmap>create())
 ```
 * 如果你想下载`File`,那么还是修改这行
```java
    getCall(new FileConvert(), RxAdapter.<File>create())
```
 * 如果你想直接解析`Json`对象,聪明的你一定知道还是这行。`注意看Convert最后有个大括号,千万不能忘记`
```java
    getCall(new JsonConvert<ServerModel>() {}, RxAdapter.<ServerModel>create())
```
 * 如果你想直接解析`List<Bean>`对象,也很简单。`注意看Convert最后有个大括号,千万不能忘记`
```java
    getCall(new JsonConvert<List<ServerModel>>() {}, RxAdapter.<List<ServerModel>>create())
```

我想,对于一款普通的app,这些请求一定能满足你90%以上的需求,而且使用方便,只需要改一行代码,就能直接获取到你想要的数据。

### 6.取消请求
推荐对每一个网络请求的`Subscription`对象都交由统一的`CompositeSubscription`去管理,在界面销毁或者需要取消的地方调用。
例如：在Activity中，当Activity销毁取消请求，可以在onDestory里面统一取消。
```java
@Override
protected void onDestroy() {
    super.onDestroy();

    unSubscribe();
}
```
 
## 三、自定义Convert使用

目前内部提供的包含`Converter`, `StringConvert` ,`BitmapConvert` ,`FileConvert` ,可以根据自己的需求去自定义Convert

 * `Converter`: 接口,所有转换器必须实现
 * `StringConvert`：将网络结果解析转成`String`
 * `BitmapConvert`：将网络结果解析转成`Bitmap`
 * `FileConvert`：将网络结果解析转成`File`

###对于自定义的`JsonConvert`,由于不同的业务实现都不一样,所以并不放在库中,提供参考实现供自己修改
