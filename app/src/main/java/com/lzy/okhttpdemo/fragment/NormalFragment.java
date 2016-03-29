package com.lzy.okhttpdemo.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lzy.okhttpdemo.Bean.Bean;
import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.MyJsonCallBack;
import com.lzy.okhttpdemo.callback.MyFileCallBack;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.PostRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NormalFragment extends Fragment implements AdapterView.OnItemClickListener {

    private String host = "http://192.168.1.108:8080/UploadServer/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal, container, false);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("okhttp原生的get请求");
        strings.add("okhttp原生的post请求");
        strings.add("get请求返回json");
        strings.add("post上传json字符串");
        strings.add("post上传string字符串");
        strings.add("post请求返回json数组");
        strings.add("post文件上传");
        strings.add("get文件下载");

        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, strings));
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                get();
                break;
            case 1:
                post();
                break;
            case 2:
                getJson();
                break;
            case 3:
                postJson();
                break;
            case 4:
                postString();
                break;
            case 5:
                responseJsonArray();
                break;
            case 6:
                uploadFile();
                break;
            case 7:
                downloadFile();
                break;
        }
    }

    private void getJson() {
        OkHttpUtils.get(host + "ResponseJson")//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .execute(new MyJsonCallBack<Bean>() {
                    @Override
                    public void onResponse(Bean bean) {
                        System.out.println("onResponse:" + bean);
                    }
                });
    }

    private void postString() {
        OkHttpUtils.post(host + "UploadString")//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .content("asdfasdfasdfas这是中文这是中文asdfasdfasdf")//
                .mediaType(PostRequest.MEDIA_TYPE_PLAIN)//
                .execute(new MyJsonCallBack<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("onResponse:" + s);
                    }
                });
    }

    private void postJson() {
        OkHttpUtils.post(host + "UploadString")//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .postJson("{}")//
                .execute(new MyJsonCallBack<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("onResponse:" + s);
                    }
                });
    }

    private void responseJsonArray() {
        OkHttpUtils.post(host + "ResponseJsonArray")//
                .tag(this)//
                .connTimeOut(2000).writeTimeOut(3000).readTimeOut(4000).params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .execute(new MyJsonCallBack<List<Bean>>() {
                    @Override
                    public void onResponse(List<Bean> beans) {
                        System.out.println("onResponse:" + beans);
                    }
                });
    }

    private void uploadFile() {
        OkHttpUtils.post(host + "UploadFile")//
                .tag(this)//
                .headers("aaa", "111").headers("bbb", "222").params("ccc", "333").params("ddd", "444")//
                .params("file1", new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20160125_230019.jpg"))//
                .params("file2", new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20160125_230037.jpg"))//
                .params("file2", new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20160125_230048.jpg"))//
                .execute(new MyJsonCallBack<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("onResponse:" + s);
                    }
                });
    }

    private void downloadFile() {
        OkHttpUtils.get(host + "DownloadFile")//
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
        Request request = new Request.Builder().post(requestBody).url(host + "UploadFile").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("-----" + response.body().string());
            }
        });
    }

    /** 原生方法调用 */
    private void get() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(host + "ResponseJson").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("-----" + response.body().string());
            }
        });
    }
}
