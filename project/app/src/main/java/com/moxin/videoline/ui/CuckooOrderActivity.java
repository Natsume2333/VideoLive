package com.moxin.videoline.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.FragAdapter;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.fragment.CharmFragment;
import com.moxin.videoline.utils.Utils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUIViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class CuckooOrderActivity extends BaseActivity {
    //功能
    private QMUIViewPager rollViewViewpage;
    private QMUITabSegment rollTabSegment;
    private FragAdapter mFragAdapter;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_cuckoo_order;
    }

    @BindView(R.id.top)
    View top;

    @Override
    protected void initView() {
        Utils.initTransH(top);
        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        //StatusBarUtil.setColor(this,getResources().getColor(R.color.white),0);
        rollTabSegment = findViewById(R.id.tab_segment);
        rollViewViewpage = findViewById(R.id.roll_view_viewpage);
        ArrayList<Fragment> fragmentList = new ArrayList();
        fragmentList.add(new CharmFragment().setMl(true));
        //fragmentList.add(new MoneyFragment());
        fragmentList.add(new CharmFragment().setMl(false));

        ArrayList<String> titleList = new ArrayList();
        titleList.add(getString(R.string.charm));
        titleList.add(getString(R.string.caiqi));

        rollViewViewpage.setOffscreenPageLimit(1);
        mFragAdapter = new FragAdapter(getSupportFragmentManager(), fragmentList);
        mFragAdapter.setTitleList(titleList);
        rollViewViewpage.setAdapter(mFragAdapter);

        //设置字体大小
        rollTabSegment.setTabTextSize(ConvertUtils.dp2px(18));
        //设置 Tab 选中状态下的颜色
        rollTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.white));
        rollTabSegment.setDefaultNormalColor(Color.parseColor("#B2FFFFFF"));
        //关联viewPage
        rollTabSegment.setupWithViewPager(rollViewViewpage, true, false);
        rollViewViewpage.setCurrentItem(0);
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

    @OnClick({R.id.iv_back})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                clickBack();
                break;
        }
    }

    private void clickBack() {

        finish();
    }
}
