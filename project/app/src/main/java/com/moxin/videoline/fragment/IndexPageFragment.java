package com.moxin.videoline.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moxin.videoline.R;
import com.moxin.videoline.adapter.FragAdapter;
import com.moxin.videoline.base.BaseFragment;
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.modle.StarLevelModel;
import com.moxin.videoline.ui.CuckooSearchActivity;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUIViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * 排行-推荐 页
 * Created by 魏鹏 on 2017/12/27 0027.
 */

public class IndexPageFragment extends BaseFragment {
    //功能
    private QMUIViewPager rollViewViewPage;
    private QMUITabSegment rollTabSegment;

    private List fragmentList;
    private List titleList;

    private FragAdapter mFragAdapter;

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_index_roll, container, false);
    }

    @Override
    protected void initView(View view) {

        rollViewViewPage = view.findViewById(R.id.roll_view_viewpage);
        rollTabSegment = view.findViewById(R.id.roll_tab_segment);
    }

    @Override
    protected void initDate(View view) {

        fragmentList = new ArrayList();
        fragmentList.add(new IndexTabAttentionFragment());
        fragmentList.add(new RecommendFragment());
        fragmentList.add(new SameCityFragment());
        fragmentList.add(new NewPeopleFragment());
        fragmentList.add(new OnlineFragment());

        //fragmentList.add(new CharmFragment());
        //fragmentList.add(new MoneyFragment());

        titleList = new ArrayList();
        titleList.add(getString(R.string.follow));
        titleList.add(getString(R.string.recommend));
        titleList.add("附近");
        titleList.add(getString(R.string.novice));
        titleList.add(getString(R.string.on_line));

        //titleList.add("魅力");
        //titleList.add("财气");


        //星级主播
        ArrayList<StarLevelModel> list = ConfigModel.getInitData().getStar_level_list();
        if (list != null) {
            for (StarLevelModel item : list) {
                StarLevelListFragment levelListFragment = new StarLevelListFragment();
                Bundle intent = new Bundle();
                intent.putString(StarLevelListFragment.LEVEL_ID, item.getId());
                levelListFragment.setArguments(intent);
                fragmentList.add(levelListFragment);
                titleList.add(item.getLevel_name());
            }
        }

    }

    @Override
    protected void initSet(View view) {
        rollViewViewPage.setOffscreenPageLimit(1);
        mFragAdapter = new FragAdapter(getChildFragmentManager(), fragmentList);
        mFragAdapter.setTitleList(titleList);
        rollViewViewPage.setAdapter(mFragAdapter);

        //设置字体大小
        rollTabSegment.setTabTextSize(getResources().getDimensionPixelSize(R.dimen.home_tab_text));
        //设置 Tab 选中状态下的颜色
        rollTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.main_tab_sel));
        rollTabSegment.setDefaultNormalColor(getResources().getColor(R.color.main_tab_unsel));
        //关联viewPage
        rollTabSegment.setupWithViewPager(rollViewViewPage, true, false);
        rollViewViewPage.setCurrentItem(1);
        rollTabSegment.setHasIndicator(true);
    }

    @Override
    protected void initDisplayData(View view) {

    }

    @OnClick({R.id.iv_search})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                clickSearch();
                break;

            default:
                break;
        }
    }

    private void clickSearch() {
        Intent intent = new Intent(getContext(), CuckooSearchActivity.class);
        startActivity(intent);
    }

}
