 ![image](https://github.com/jeasonlzy/Screenshots/blob/master/okgo/logo4.jpg)

# JsonCallback自定义

## 一般服务端返回的数据格式都是这样的，我相信这4种数据格式包含了99%以上的业务。

- 数据类型A-最外层数据类型是`JsonObject`，data 数据也是`JsonObject`
```java
{
	"code":0,
	"msg":"请求成功",
	"data":{
		"id":123456,
		"name":"张三",
		"age":18
	}	
}
```

- 数据类型B-最外层数据类型是`JsonObject`，data 数据是`JsonArray`
```java
{
	"code":0,
	"msg":"请求成功",
	"data":[
		{
			"id":123456,
			"name":"张三",
			"age":18
		},
		{
			"id":123456,
			"name":"张三",
			"age":18
		},
		{
			"id":123456,
			"name":"张三",
			"age":18
		}
	]
}
```

- 数据类型C-没有固定的 msg、code 字段格式包装，服务器任意返回对象
```java
"data":{
	"id":123456,
	"name":"张三",
	"age":18
}	
```

- 数据类型D-最外层数据类型是`JsonArray`，内部数据是`JsonObject`
```java
[
	{
		"id":123456,
		"name":"张三",
		"age":18
	},
	{
		"id":123456,
		"name":"张三",
		"age":18
	},
	{
		"id":123456,
		"name":"张三",
		"age":18
	}
]
```

## 那么你需要定义的JavaBean有以下两种方式

### 第一种：将code和msg也定义在javabean中(适用99%以上的情况)
- 数据类型A定义方式

![image](https://github.com/jeasonlzy/Screenshots/blob/master/JsonCallback/a.png)

- 数据类型B定义方式

![image](https://github.com/jeasonlzy/Screenshots/blob/master/JsonCallback/b.png)

- 数据类型C和D定义方式一样，这里就按照服务端的数据一一对应好字段就行了，没什么其他的妙招

![image](https://github.com/jeasonlzy/Screenshots/blob/master/JsonCallback/cd.png)

- 数据格式定义完成后，对于ABC这三种，我们在创建 JsonCallback 的时候，只需要这么将`Login`作为一个泛型传递
```java
OkGo.get(Urls.URL_METHOD)//
    .tag(this)//
    .execute(new JsonCallback<Login>(this) {   //这里传递Login
        @Override
        public void onSuccess(Login login, Call call, Response response) {
            
        }
    });
```

- 数据格式定义完成后，对于D这种集合数据，我们只需要改一个地方，传递泛型的时候，传递`List<Login>`就可以了，如下
```java
OkGo.get(Urls.URL_METHOD)//
    .tag(this)//
    .execute(new JsonCallback<List<Login>>(this) {    //解析集合就是这么的简单，直接加上List就可以了
        @Override
        public void onSuccess(List<Login> logins, Call call, Response response) {
            
        }
    });
```

- 现在数据格式定义完成，用法也写完了，那么怎么解析数据呢，就是在`JsonCallback`的`convertSuccess`方法中写如下代码，以上四种数据类型统统使用这样一种解析方法就可以完成所有的数据解析，不需要任何额外的代码，下面的代码可以直接复制使用，详细的原理就不说了，注释很详细
```java
@Override
public T convertSuccess(Response response) throws Exception {

    //以下代码是通过泛型解析实际参数,泛型必须传

    //com.lzy.demo.callback.DialogCallback<com.lzy.demo.model.Login> 得到类的泛型，包括了泛型参数
    Type genType = getClass().getGenericSuperclass();
    //从上述的类中取出真实的泛型参数，有些类可能有多个泛型，所以是数值
    Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
    //我们的示例代码中，只有一个泛型，所以取出第一个，得到如下结果
    //com.lzy.demo.model.Login
    Type type = params[0];

    //这里我们既然都已经拿到了泛型的真实类型，即对应的 class ，那么当然可以开始解析数据了，我们采用 Gson 解析
    //以下代码是根据泛型解析数据，返回对象，返回的对象自动以参数的形式传递到 onSuccess 中，可以直接使用
    JsonReader jsonReader = new JsonReader(response.body().charStream());
    //有数据类型，表示有data
    T data = Convert.fromJson(jsonReader, type);
    response.close();
    return data;
}
```

### 第二种：使用泛型，分离基础包装与实际数据（推荐写法，但是对服务端数据格式有要求）

- 这样子需要定义两个javabean，一个全项目通用的`LzyResponse`，一个单纯的业务模块需要的数据
- 这种方法只适合服务器返回有固定数据格式的情况，比如A和B，如果服务器的数据格式不统一，不建议使用该种方式，具体的定义方法如下

```java
public class LzyResponse<T> {
    public int code;
    public String msg;
    public T data;
}

public class ServerModel {
    public long id;
    public String name;
    public int age;
}
```

在okgo提供的demo中是按照这种方式实现的，对于这种方式，我们在创建`JsonCallback`的时候，需要这么将`LzyResponse<ServerModel>`作为一个泛型传递，相当于传递了两层，泛型中包含了又一个泛型

- 对于数据类型A采用以下传递泛型的方法
```java
OkGo.get(Urls.URL_METHOD)//
    .tag(this)//
    .execute(new JsonCallback<LzyResponse<ServerModel>>(this) {   //这里传递LzyResponse<ServerModel>
        @Override
        public void onSuccess(LzyResponse<ServerModel> responseData, Call call, Response response) {
            
        }
    });
```
- 对于集合数据类型B采用以下传递泛型的方法，看起来比较复杂，泛型较多，但是很好理解
```java
OkGo.get(Urls.URL_METHOD)//
    .tag(this)//
    .execute(new JsonCallback<LzyResponse<List<ServerModel>>>(this) {  //我们发现解析集合也依然很简单，加一层List就解析集合数据了
        @Override
        public void onSuccess(LzyResponse<List<ServerModel>> responseListData, Call call, Response response) {
            
        }
    });
```

- 那么在`JsonCallback`的`convertSuccess`方法中写如下解析代码，详细的原理就不说了，下面的注释很详细
```java
@Override
public T convertSuccess(Response response) throws Exception {

    // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
    // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
    // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

    //以下代码是通过泛型解析实际参数,泛型必须传
    //这里为了方便理解，假如请求的代码按照上述注释文档中的请求来写，那么下面分别得到是

    //com.lzy.demo.callback.DialogCallback<com.lzy.demo.model.LzyResponse<com.lzy.demo.model.ServerModel>> 得到类的泛型，包括了泛型参数
    Type genType = getClass().getGenericSuperclass();
    //从上述的类中取出真实的泛型参数，有些类可能有多个泛型，所以是数值
    Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
    //我们的示例代码中，只有一个泛型，所以取出第一个，得到如下结果
    //com.lzy.demo.model.LzyResponse<com.lzy.demo.model.ServerModel>
    Type type = params[0];
    
    // 这里这么写的原因是，我们需要保证上面我解析到的type泛型，仍然还具有一层参数化的泛型，也就是两层泛型
    // 如果你不喜欢这么写，不喜欢传递两层泛型，那么以下两行代码不用写，并且javabean按照第一种方式定义就可以实现
    if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
    //如果确实还有泛型，那么我们需要取出真实的泛型，得到如下结果
    //class com.lzy.demo.model.LzyResponse
    //此时，rawType的类型实际上是 class，但 Class 实现了 Type 接口，所以我们用 Type 接收没有问题
    Type rawType = ((ParameterizedType) type).getRawType();
    //这里获取最终内部泛型的类型 com.lzy.demo.model.ServerModel
    Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];

    //这里我们既然都已经拿到了泛型的真实类型，即对应的 class ，那么当然可以开始解析数据了，我们采用 Gson 解析
    //以下代码是根据泛型解析数据，返回对象，返回的对象自动以参数的形式传递到 onSuccess 中，可以直接使用
    JsonReader jsonReader = new JsonReader(response.body().charStream());
    if (typeArgument == Void.class) {
        //无数据类型,表示没有data数据的情况（以  new DialogCallback<LzyResponse<Void>>(this)  以这种形式传递的泛型)
        SimpleResponse simpleResponse = Convert.fromJson(jsonReader, SimpleResponse.class);
        response.close();
        //noinspection unchecked
        return (T) simpleResponse.toLzyResponse();
    } else if (rawType == LzyResponse.class) {
        //有数据类型，表示有data
        LzyResponse lzyResponse = Convert.fromJson(jsonReader, type);
        response.close();
        int code = lzyResponse.code;
        //这里的0是以下意思
        //一般来说服务器会和客户端约定一个数表示成功，其余的表示失败，这里根据实际情况修改
        if (code == 0) {
            //noinspection unchecked
            return (T) lzyResponse;
        } else if (code == 104) {
            //比如：用户授权信息无效，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
            throw new IllegalStateException("用户授权信息无效");
        } else if (code == 105) {
            //比如：用户收取信息已过期，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
            throw new IllegalStateException("用户收取信息已过期");
        } else if (code == 106) {
            //比如：用户账户被禁用，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
            throw new IllegalStateException("用户账户被禁用");
        } else if (code == 300) {
            //比如：其他乱七八糟的等，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
            throw new IllegalStateException("其他乱七八糟的等");
        } else {
            throw new IllegalStateException("错误代码：" + code + "，错误信息：" + lzyResponse.msg);
        }
    } else {
        response.close();
        throw new IllegalStateException("基类错误无法解析!");
    }
}
```

### 关于第二种更详细的代码请自行看demo，有很详细的注释，`JsonConvet`与`JsonCallback`一样，只是`JsonCallback`是在传统回调形式中用的，而`JsonConvert`是在`OkRx`中使用的，其余没有任何区别

### 总结：

> 分析上面两种写法，很明显第一种要方便、简单、通用，第二种方式更麻烦，还难以理解。那我在demo中为什么不使用第一种方式呢？

先做如下分析，如果服务端返回了这么个数据，这种数据应该是经常返回的。

```java
{
	"code":300,
	"msg":"用户名或密码错误",	
}
```

* 第一种方式，很明显，这样的的数据应该要回调`onError`的，但是我们在`convertSuccess`中只是解析了数据，并不知道数据中的`code`码300是表示错误数据的，我们仍然会解析数据并返回，这就导致了会回调`onSuccess`，之后的逻辑我们需要在`onSuccess`中判断`code`码，给予用户错误提示或者做成功跳转的逻辑。这样做的结果是，把无论正确的还是错误的数据，都交给了`onSuccess`处理，在使用上不太友好，逻辑分层混乱。

* 第二种方式，这种方式不仅可以正确的解析数据，而且能在解析的过程中判断错误码，并根据不同的错误码抛出不同的异常（这里抛出异常并不会导致okgo挂掉，而是以异常的形式通知okgo回调`onError`，并且将该异常以参数的形式传递到`onError`），这样做后，在`onSuccess`中处理的一定是成功的数据，在`onError`中处理的一定是失败的数据，达到了友好的逻辑分层。

* 正是因为我推荐的是第二种写法，而第二种写法是依赖于业务中定义的数据结构的，不同的项目定义的数据结构都不一样，无法封装到库中，所以用demo的形式提供示例代码，根据需要自己修改。同时我也推荐服务端对返回的数据做一次统一包装，就像上面的示例一样，用code和msg包一层。




