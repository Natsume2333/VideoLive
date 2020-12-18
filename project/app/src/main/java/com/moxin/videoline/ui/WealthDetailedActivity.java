package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.moxin.videoline.R;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.fragment.AboutUsFragment;
import com.moxin.videoline.fragment.FeedBackFragment;
import com.moxin.videoline.fragment.VideoChargeSetFragment;
import com.moxin.videoline.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

public class WealthDetailedActivity extends BaseActivity {

    @Override
    protected Context getNowContext() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_wealth_detailed;
    }

    public static final int TYPE_SR = 0;
    public static final int TYPE_ZC = 1;
    public static final int TYPE_TX = 2;
    public static final int TYPE_CZ = 3;
    public static final int TYPE_RECHARGE = 5;
    public static final int TYPE_DRAWING = 6;
    public static final int TYPE_BIND = 7;
    public static final int TYPE_INVITE_DRAWING = 8;
    public static final int TYPE_MY_LEVEL = 9;
    public static final int TYPE_VIDEO_SET = 10;
    public static final int TYPE_FEED_BACK = 11;
    public static final int TYPE_ABOUT_US = 12;
    public static final int TYPE_DRAWING_LIST = 13;
    public static final int TINVITE_YPE_RECHARGE = 15;


    String[] names = new String[]{"收入", "支出", "提现", "充值"};

    public static void start(Context context, int type) {
        Intent intent = new Intent(context, WealthDetailedActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }


    int type;
    @BindView(R.id.title_all)
    View title;
    @BindView(R.id.title_right_icon)
    View icon;
    Fragment fragment;

    @Override
    protected void initView() {
        type = getIntent().getIntExtra("type", 0);
        FragmentManager fm = getSupportFragmentManager(); //获取Fragment管理器对象
        FragmentTransaction ft = fm.beginTransaction(); //开启事务
        fragment = null;
        icon.setVisibility(View.GONE);
        if (type < 5) {
            fragment = new WealthDetailFragment().setType(type);
            Utils.initTransTitleBar(title, names[type] + "明细", this);
        } else if (type == TYPE_RECHARGE) {
            fragment = new WealthRechargeFragment();
            Utils.initTransTitleBar(title, "充值", this);
        } else if (type == TYPE_DRAWING) {
            icon.setBackgroundResource(R.mipmap.bind_acct);
            icon.setVisibility(View.VISIBLE);
            Utils.initTransTitleBar(title, "提现", this);
            fragment = new DrawingFragment();
        } else if (type == TYPE_BIND) {
            fragment = new WealthBindFragment();
            Utils.initTransTitleBar(title, "绑定账户", this);
        } else if (type == TYPE_INVITE_DRAWING) {
            icon.setBackgroundResource(R.mipmap.bind_acct);
            icon.setVisibility(View.VISIBLE);
            fragment = new DrawingFragment();
            Utils.initTransTitleBar(title, "提现", this);
        } else if (type == TYPE_MY_LEVEL) {
            fragment = new MyLevelFragment();
            Utils.initTransTitleBar(title, "等级详情", this);
        } else if (type == TYPE_VIDEO_SET) {
            fragment = new VideoChargeSetFragment();
            Utils.initTransTitleBar(title, "视频收费设置", this);
        } else if (type == TYPE_FEED_BACK) {
            fragment = new FeedBackFragment();
            Utils.initTransTitleBar(title, "意见反馈", this);
        } else if (type == TYPE_ABOUT_US) {
            fragment = new AboutUsFragment();
            Utils.initTransTitleBar(title, "关于我们", this);
        } else if (type == TYPE_DRAWING_LIST) {
            fragment = new InviteDrawingListFragment();
            Utils.initTransTitleBar(title, "提现明细", this);
        } else if (type == TINVITE_YPE_RECHARGE) {
            icon.setBackgroundResource(R.mipmap.bind_acct);
            icon.setVisibility(View.VISIBLE);
            Utils.initTransTitleBar(title, "邀请提现", this);
            fragment = new InviteWithdrawFragment();

        }

        ft.add(R.id.fm, fragment).commit();
    }


    @OnClick(R.id.title_right)
    public void onR() {
        //绑定账户
        start(this, TYPE_BIND);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
