package com.lzy.okhttpdemo.okhttpserver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okhttpdemo.model.ApkModel;
import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.base.BaseActivity;
import com.lzy.okhttpdemo.utils.AppCacheUtils;
import com.lzy.okhttpserver.download.DownloadManager;
import com.lzy.okhttpserver.download.DownloadService;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.GetRequest;

import java.util.ArrayList;

import butterknife.Bind;

public class DownloadActivity extends BaseActivity {

    @Bind(R.id.targetFolder) TextView targetFolder;
    @Bind(R.id.tvCorePoolSize) TextView tvCorePoolSize;
    @Bind(R.id.sbCorePoolSize) SeekBar sbCorePoolSize;
    @Bind(R.id.listView) ListView listView;
    @Bind(R.id.openManager) Button openManager;

    private ArrayList<ApkModel> apks;
    private MyAdapter adapter;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        initData();
        downloadManager = DownloadService.getDownloadManager();

        targetFolder.setText("下载路径: " + downloadManager.getTargetFolder());
        sbCorePoolSize.setMax(5);
        sbCorePoolSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                downloadManager.getThreadPool().setCorePoolSize(progress);
                tvCorePoolSize.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sbCorePoolSize.setProgress(3);

        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DesActivity.class);
                intent.putExtra("apk", apks.get(position));
                startActivity(intent);
            }
        });
        adapter.notifyDataSetChanged();
        openManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DownloadManagerActivity.class));
            }
        });
    }

    /** 当前Activity显示的回调 */
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return apks.size();
        }

        @Override
        public ApkModel getItem(int position) {
            return apks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_download_details, null);
            }
            final ApkModel apk = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            final Button download = (Button) convertView.findViewById(R.id.download);
            if (downloadManager.getDownloadInfo(apk.getUrl()) != null) {
                download.setText("已在队列");
                download.setEnabled(false);
            } else {
                download.setText("下载");
                download.setEnabled(true);
            }
            name.setText(apk.getName());
            Glide.with(getApplicationContext()).load(apk.getIconUrl()).error(R.mipmap.ic_launcher).into(icon);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadManager.getDownloadInfo(apk.getUrl()) != null) {
                        Toast.makeText(getApplicationContext(), "任务已经在下载列表中", Toast.LENGTH_SHORT).show();
                    } else {
                        GetRequest request = OkHttpUtils.get(apk.getUrl())//
                                .headers("headerKey1", "headerValue1")//
                                .headers("headerKey2", "headerValue2")//
                                .params("paramKey1", "paramValue1")//
                                .params("paramKey2", "paramValue2");
                        downloadManager.addTask(apk.getUrl(), request, null);
                        AppCacheUtils.getInstance(getApplicationContext()).put(apk.getUrl(), apk);
                        download.setText("已在队列");
                        download.setEnabled(false);
                    }
                }
            });
            return convertView;
        }
    }

    private void initData() {
        apks = new ArrayList<>();
        ApkModel apkInfo1 = new ApkModel();
        apkInfo1.setName("美丽加");
        apkInfo1.setIconUrl("http://pic3.apk8.com/small2/14325422596306671.png");
        apkInfo1.setUrl("http://download.apk8.com/d2/soft/meilijia.apk");
        apks.add(apkInfo1);
        ApkModel apkInfo2 = new ApkModel();
        apkInfo2.setName("果然方便");
        apkInfo2.setIconUrl("http://pic3.apk8.com/small2/14313175771828369.png");
        apkInfo2.setUrl("http://download.apk8.com/d2/soft/guoranfangbian.apk");
        apks.add(apkInfo2);
        ApkModel apkInfo3 = new ApkModel();
        apkInfo3.setName("薄荷");
        apkInfo3.setIconUrl("http://pic3.apk8.com/small2/14308183888151824.png");
        apkInfo3.setUrl("http://download.apk8.com/d2/soft/bohe.apk");
        apks.add(apkInfo3);
        ApkModel apkInfo4 = new ApkModel();
        apkInfo4.setName("GG助手");
        apkInfo4.setIconUrl("http://pic3.apk8.com/small2/14302008166714263.png");
        apkInfo4.setUrl("http://download.apk8.com/d2/soft/GGzhushou.apk");
        apks.add(apkInfo4);
        ApkModel apkInfo5 = new ApkModel();
        apkInfo5.setName("红包惠锁屏");
        apkInfo5.setIconUrl("http://pic3.apk8.com/small2/14307106593913848.png");
        apkInfo5.setUrl("http://download.apk8.com/d2/soft/hongbaohuisuoping.apk");
        apks.add(apkInfo5);
        ApkModel apkInfo6 = new ApkModel();
        apkInfo6.setName("快的打车");
        apkInfo6.setIconUrl("http://up.apk8.com/small1/1439955061264.png");
        apkInfo6.setUrl("http://download.apk8.com/soft/2015/%E5%BF%AB%E7%9A%84%E6%89%93%E8%BD%A6.apk");
        apks.add(apkInfo6);
        ApkModel apkInfo7 = new ApkModel();
        apkInfo7.setName("叮当快药");
        apkInfo7.setIconUrl("http://pic3.apk8.com/small2/14315954626414886.png");
        apkInfo7.setUrl("http://d2.apk8.com:8020/soft/dingdangkuaiyao.apk");
        apks.add(apkInfo7);
        ApkModel apkInfo8 = new ApkModel();
        apkInfo8.setName("悦跑圈");
        apkInfo8.setIconUrl("http://pic3.apk8.com/small2/14298490191525146.jpg");
        apkInfo8.setUrl("http://d2.apk8.com:8020/soft/yuepaoquan.apk");
        apks.add(apkInfo8);
        ApkModel apkInfo9 = new ApkModel();
        apkInfo9.setName("悠悠导航");
        apkInfo9.setIconUrl("http://pic3.apk8.com/small2/14152456988840667.png");
        apkInfo9.setUrl("http://d2.apk8.com:8020/soft/%E6%82%A0%E6%82%A0%E5%AF%BC%E8%88%AA2.3.32.1.apk");
        apks.add(apkInfo9);
        ApkModel apkInfo10 = new ApkModel();
        apkInfo10.setName("虎牙直播");
        apkInfo10.setIconUrl("http://up.apk8.com/small1/1439892235841.jpg");
        apkInfo10.setUrl("http://download.apk8.com/down4/soft/hyzb.apk");
        apks.add(apkInfo10);
    }
}
