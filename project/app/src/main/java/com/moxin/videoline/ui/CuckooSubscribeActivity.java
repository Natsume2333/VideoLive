package com.moxin.videoline.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.moxin.videoline.R;
import com.moxin.videoline.adapter.FragAdapter;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.fragment.CuckooSubscribeFragment;
import com.moxin.videoline.utils.SharedPreferencesUtils;
import com.maning.imagebrowserlibrary.utils.StatusBarUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUIViewPager;

import java.util.ArrayList;
import java.util.List;

public class CuckooSubscribeActivity extends BaseActivity {

    private QMUIViewPager rollViewViewPage;
    private QMUITopBar rollTabSegment;

    private List<Fragment> fragmentList = new ArrayList<>();
    private List titleList = new ArrayList();
    private FragAdapter mFragAdapter;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_cuckoo_subscribe;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.admin_color), 0);
        StatusBarUtil.setLightMode(this);


        rollTabSegment = findViewById(R.id.mQMUITabSegment);
        rollViewViewPage = findViewById(R.id.roll_view_viewpage);
        rollTabSegment.addLeftImageButton(R.drawable.icon_back_black, R.id.iv_back).setOnClickListener(this);
        String AccountNature = (String) SharedPreferencesUtils.getParam(this, "AccountNature", "");
        if ("boss".equals(AccountNature)) {
            fragmentList.add(CuckooSubscribeFragment.getInstance(1));
            rollTabSegment.setTitle("我的预约");

        } else {
            fragmentList.add(CuckooSubscribeFragment.getInstance(2));

            rollTabSegment.setTitle("预约我的");
        }


        rollViewViewPage.setOffscreenPageLimit(1);
        mFragAdapter = new FragAdapter(getSupportFragmentManager(), fragmentList);
        mFragAdapter.setTitleList(titleList);
        rollViewViewPage.setAdapter(mFragAdapter);

//        //设置字体大小
//        rollTabSegment.setTabTextSize(ConvertUtils.dp2px(12));
//        //设置 Tab 选中状态下的颜色
//        rollTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.black));
//        rollTabSegment.setDefaultNormalColor(getResources().getColor(R.color.black));
//        //关联viewPage
//        rollTabSegment.setupWithViewPager(rollViewViewPage, true, false);
//        rollViewViewPage.setCurrentItem(0);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
