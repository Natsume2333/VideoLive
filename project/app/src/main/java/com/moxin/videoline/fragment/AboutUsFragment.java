package com.moxin.videoline.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseFragment;
import com.lzy.okgo.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;

public class AboutUsFragment extends BaseFragment {

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    protected void initView(View view) {
        Api.getAboutUs(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                log("关于我们:"+s);
            }
        });
    }

    @Override
    protected void initDate(View view) {

    }

    @Override
    protected void initSet(View view) {

    }

    @Override
    protected void initDisplayData(View view) {

    }
}
