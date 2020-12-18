package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.FragAdapter;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.utils.Utils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.ArrayList;

import butterknife.BindView;

public class InviteDetailedActivity extends BaseActivity {


    private int type;
    @BindView(R.id.title_all)
    View title;

    @BindView(R.id.title_right_icon)
    View icon;

    @BindView(R.id.tabs)
    NavigationTabStrip tabs;

    @BindView(R.id.vp)
    ViewPager vp;

    @Override
    protected Context getNowContext() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_invite_info;
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, InviteDetailedActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        Utils.initTransTitleBar(title, "邀请明细", this);
        icon.setVisibility(View.GONE);

        tabs.setTitles("邀请明细", "邀请男生", "邀请的女生");
        tabs.setTitleSize(ConvertUtils.dp2px(15));
        tabs.setStripType(NavigationTabStrip.StripType.LINE);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new RewardDetailPager());
        fragments.add(new InviteBoyPager().setType(true));
        fragments.add(new InviteBoyPager().setType(false));
        vp.setAdapter(new FragAdapter(getSupportFragmentManager(), fragments));

        tabs.setViewPager(vp);

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
