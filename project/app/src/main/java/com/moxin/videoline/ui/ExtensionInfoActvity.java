package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.modle.InvitedInfoBean;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

//推广明细
public class ExtensionInfoActvity extends BaseActivity {


    @Override
    protected Context getNowContext() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_extension_info_actvity;
    }

    public static void start(Context c){
        Intent intent = new Intent(c,ExtensionInfoActvity.class);
        c.startActivity(intent);
    }


    @OnClick(R.id.title_back)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @BindView(R.id.title_all)
    View title;
    @BindView(R.id.swip)
    SwipeRefreshLayout swip;
    @BindView(R.id.recy)
    RecyclerView recy;
    String nowType="1";//1邀请用户 2收益记录

    @BindView(R.id.tab1)
    TextView tab1;
    @BindView(R.id.tab2)
    TextView tab2;

    @OnClick({R.id.tab1,R.id.tab2})
    public void onC(View v){
        if(v.getId()==R.id.tab1){
            nowType="1";
            tab2.setTextSize(17);
            tab2.setTextColor(Color.parseColor("#867F48"));
            tab1.setTextSize(18);
            tab1.setTextColor(Color.parseColor("#323232"));
        }else {
            nowType="2";
            tab1.setTextSize(17);
            tab1.setTextColor(Color.parseColor("#867F48"));
            tab2.setTextSize(18);
            tab2.setTextColor(Color.parseColor("#323232"));

        }
        getData();
    }

    @Override
    protected void initView() {
        Utils.initTransP(title);
        swip.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        recy.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        swip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        getData();
    }

    void getData(){
        Api.getInvitedInfo(nowType,new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                swip.setRefreshing(false);
                InvitedInfoBean bean = new Gson().fromJson(s, InvitedInfoBean.class);
                show(bean.getData());
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                swip.setRefreshing(false);
                show(new ArrayList<InvitedInfoBean.DataBean>());
            }
        });
    }

    BaseQuickAdapter adapter;
    private void show(final List<InvitedInfoBean.DataBean> data) {
        recy.setAdapter(adapter=new BaseQuickAdapter<InvitedInfoBean.DataBean,BaseViewHolder>(R.layout.extension_item, data) {
            @Override
            protected void convert(BaseViewHolder helper, InvitedInfoBean.DataBean item) {
                helper.setText(R.id.left,item.getUser_nickname())
                        .setText(R.id.center,item.getCreate_time())
                        .setText(R.id.right,"+"+item.getMoney());
            }
        });
        adapter.bindToRecyclerView(recy);
        adapter.setEmptyView(R.layout.empt_data_layout);
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
