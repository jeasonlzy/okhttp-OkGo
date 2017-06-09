/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.demo.okrx2;

import com.lzy.demo.base.MainFragment;
import com.lzy.demo.model.ItemModel;

import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/6/9
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class OkRxFragment extends MainFragment {

    @Override
    public void fillData(List<ItemModel> items) {
        items.add(new ItemModel("OkRx是OkGo结合RxJava的扩展项目\n" +//
                                "OkRx2是OkGo结合RxJava2的扩展项目\n" +//
                                "他们的使用方法完全一样，在此不做演示，详细请看OkRx2的使用介绍",  //
                                "1.完美结合RxJava\n" +//
                                "2.比Retrofit更简单方便\n" +//
                                "3.网络请求和RxJava调用,一条链点到底\n" +//
                                "4.支持JSON数据的自动解析转换"));
    }

    @Override
    public void onItemClick(int position) {
    }
}
