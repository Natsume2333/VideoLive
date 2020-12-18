package com.moxin.videoline.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.moxin.videoline.R;
import com.moxin.videoline.adapter.NewPairAdaper;
import com.moxin.videoline.base.BaseActivity;

import butterknife.BindView;

public class PairingActivity extends BaseActivity {

    @BindView(R.id.pair_rv)
    RecyclerView pairRv;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_pairing;
    }

    @Override
    protected void initView() {
        pairRv.setLayoutManager(new LinearLayoutManager(this));

        //头布局
        View bannerView = LayoutInflater.from(this).inflate(R.layout.view_find_love_me_head, null);

        NewPairAdaper pairAdaper = new NewPairAdaper(this, null);
        pairAdaper.addHeaderView(bannerView);
        pairRv.setAdapter(pairAdaper);
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initPlayerDisplayData() {

    }


}
