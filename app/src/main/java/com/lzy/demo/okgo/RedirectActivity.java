package com.lzy.demo.okgo;

import android.os.Bundle;
import android.view.View;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseDetailActivity;
import com.lzy.demo.callback.StringDialogCallback;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class RedirectActivity extends BaseDetailActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_redirect);
        ButterKnife.bind(this);
        setTitle("301重定向");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

    @OnClick(R.id.redirect)
    public void redirect(View view) {
        OkGo.get(Urls.URL_REDIRECT)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        handleResponse(s, call, response);

                        responseData.setText("注意看请求头的url和响应头的url是不一样的！\n这代表了在请求过程中发生了重定向，" +
                                                     "okhttp默认将重定向封装在了请求内部，只有最后一次请求的数据会被真正的请求下来触发回调，中间过程" +
                                                     "是默认实现的，不会触发回调！");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        handleError(call, response);
                    }
                });
    }
}