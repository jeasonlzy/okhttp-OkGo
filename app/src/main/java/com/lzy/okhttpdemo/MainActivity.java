package com.lzy.okhttpdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lzy.okhttpdemo.callback.MyBeanCallBack;
import com.lzy.okhttpdemo.callback.MyFileCallBack;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.model.RequestParams;
import com.lzy.okhttputils.request.BaseRequest;
import com.lzy.okhttputils.request.GetRequest;
import com.lzy.okhttputils.request.PostRequest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<String> strings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        strings = new ArrayList<>();
        strings.add("get");
        strings.add("post");
        strings.add("getJson");
        strings.add("postString");
        strings.add("postJson");
        strings.add("responseJson");
        strings.add("responseJsonArray");
        strings.add("uploadFile");
        strings.add("downloadFile");

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings));
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class<? extends MainActivity> clazz = this.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(strings.get(position))) {
                try {
                    method.invoke(this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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

    private void postString() {
        OkHttpUtils.post("http://192.168.1.111:8080/UploadServer/UploadString")//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .content("asdfasdfasdfas这是中文这是中文asdfasdfasdf")//
                .mediaType(PostRequest.MEDIA_TYPE_PLAIN)//
                .execute(new MyBeanCallBack<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("onResponse:" + s);
                    }
                });
    }

    private void postJson() {
        OkHttpUtils.post("http://192.168.1.111:8080/UploadServer/UploadString")//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .postJson("{}")//
                .execute(new MyBeanCallBack<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("onResponse:" + s);
                    }
                });
    }

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

    private void responseJsonArray() {
        OkHttpUtils.post("http://192.168.1.111:8080/UploadServer/ResponseJsonArray")//
                .tag(this)//
                .connTimeOut(2000).writeTimeOut(3000).readTimeOut(4000).params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .execute(new MyBeanCallBack<List<Bean>>() {
                    @Override
                    public void onResponse(List<Bean> beans) {
                        System.out.println("onResponse:" + beans);
                    }
                });
    }

    private void uploadFile() {
        OkHttpUtils.post("http://192.168.1.111:8080/UploadServer/UploadFile")//
                .tag(this)//
                .headers("aaa", "111").headers("bbb", "222").params("ccc", "333").params("ddd", "444").params("file1", new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20151225_155549.jpg"))//
                .params("file2", new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20160109_010308.jpg"))//
                .params("file3", new File(Environment.getExternalStorageDirectory() + "/video/splash.avi"))//
                .execute(new MyBeanCallBack<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("onResponse:" + s);
                    }
                });
    }

    private void downloadFile() {
        OkHttpUtils.post("http://192.168.1.111:8080/UploadServer/DownloadFile")//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .execute(new MyFileCallBack(Environment.getExternalStorageDirectory() + "/video", "bbb.avi") {
                    @Override
                    public void onResponse(File response) {
                        System.out.println("onResponse:" + response);
                    }
                });
    }

    /** 原生方法调用 */
    private void post() {
        File file1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20151225_155549.jpg");
        RequestBody filebody = RequestBody.create(MediaType.parse("application/octet-stream"), file1);
        File file2 = new File(Environment.getExternalStorageDirectory() + "/video/splash.avi");
        RequestBody videobody = RequestBody.create(MediaType.parse("application/octet-stream"), file2);

        OkHttpClient client = new OkHttpClient();
        MultipartBody requestBody = new MultipartBody.Builder()//
                .addFormDataPart("aaa", "111")//
                .addFormDataPart("ccc", "222.jpg", filebody)//
                .addFormDataPart("ggg", "666.avi", videobody).build();
        Request request = new Request.Builder().post(requestBody).url("http://192.168.1.111:8080/UploadServer/UploadFile").build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("-----" + response.body().string());
            }
        });
    }

    /** 原生方法调用 */
    private void get() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url("http://192.168.1.111:8080/UploadServer/ResponseJson").build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("-----" + response.body().string());
            }
        });
    }
}
