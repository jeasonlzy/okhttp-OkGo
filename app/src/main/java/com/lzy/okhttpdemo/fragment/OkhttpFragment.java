package com.lzy.okhttpdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.activity.BitmapRequestActivity;
import com.lzy.okhttpdemo.activity.CacheActivity;
import com.lzy.okhttpdemo.activity.CustomRequestActivity;
import com.lzy.okhttpdemo.activity.FileDownloadActivity;
import com.lzy.okhttpdemo.activity.FormUploadActivity;
import com.lzy.okhttpdemo.activity.HttpsActivity;
import com.lzy.okhttpdemo.activity.MethodActivity;
import com.lzy.okhttpdemo.activity.PostTextActivity;
import com.lzy.okhttpdemo.activity.RedirectActivity;
import com.lzy.okhttpdemo.activity.SyncActivity;
import com.lzy.okhttpdemo.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OkhttpFragment extends Fragment implements AdapterView.OnItemClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new OkHttpAdapter(Constant.getData()));
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(getActivity(), MethodActivity.class);
                break;
            case 1:
                intent = new Intent(getActivity(), CustomRequestActivity.class);
                break;
            case 2:
                intent = new Intent(getActivity(), BitmapRequestActivity.class);
                break;
            case 3:
                intent = new Intent(getActivity(), PostTextActivity.class);
                break;
            case 4:
                intent = new Intent(getActivity(), FormUploadActivity.class);
                break;
            case 5:
                intent = new Intent(getActivity(), FileDownloadActivity.class);
                break;
            case 6:
                intent = new Intent(getActivity(), CacheActivity.class);
                break;
            case 7:
                intent = new Intent(getActivity(), HttpsActivity.class);
                break;
            case 8:
                intent = new Intent(getActivity(), SyncActivity.class);
                break;
            case 9:
                intent = new Intent(getActivity(), RedirectActivity.class);
                break;
        }
        startActivity(intent);
    }

    private class OkHttpAdapter extends BaseAdapter {
        private List<String[]> titles;

        public OkHttpAdapter(List<String[]> titles) {
            this.titles = titles;
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public String[] getItem(int position) {
            return titles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_main_list, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            String[] item = getItem(position);
            holder.title.setText(item[0]);
            holder.des.setText(item[1]);
            return convertView;
        }
    }

    public class ViewHolder {
        @Bind(R.id.title) TextView title;
        @Bind(R.id.des) TextView des;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }

//    /** 原生方法调用 */
//    private void post() {
//        File file1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20151225_155549.jpg");
//        RequestBody filebody = RequestBody.create(MediaType.parse("application/octet-stream"), file1);
//        File file2 = new File(Environment.getExternalStorageDirectory() + "/video/splash.avi");
//        RequestBody videobody = RequestBody.create(MediaType.parse("application/octet-stream"), file2);
//
//        OkHttpClient client = new OkHttpClient();
//        MultipartBody requestBody = new MultipartBody.Builder()//
//                .addFormDataPart("aaa", "111")//
//                .addFormDataPart("ccc", "222.jpg", filebody)//
//                .addFormDataPart("ggg", "666.avi", videobody).build();
//        Request request = new Request.Builder().post(requestBody).url(host + "UploadFile").build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                System.out.println("-----" + response.body().string());
//            }
//        });
//    }
//
//    /** 原生方法调用 */
//    private void get() {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().get().url(host + "ResponseJson").build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                System.out.println("-----" + response.body().string());
//            }
//        });
//    }
}
