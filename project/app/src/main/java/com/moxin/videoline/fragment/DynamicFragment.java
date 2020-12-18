package com.moxin.videoline.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moxin.videoline.R;
import com.moxin.videoline.adapter.FragAdapter;
import com.moxin.videoline.audiorecord.AudioPlaybackManager;
import com.moxin.videoline.base.BaseFragment;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.ui.PushDynamicActivity;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUIViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态
 */
public class DynamicFragment extends BaseFragment {

    private QMUIViewPager rollViewViewpage;
    private QMUITabSegment dynamicTablayout;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    private FragAdapter mDynamicFragAdapter;
    private ImageView dynamicIv;

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_dynamic_layout, container, false);
    }

    @Override
    protected void initView(View view) {

        dynamicTablayout = view.findViewById(R.id.tabLayout);
        rollViewViewpage = view.findViewById(R.id.roll_view_viewpage);

        dynamicIv = view.findViewById(R.id.iv_dynamic);
        //发动态
        dynamicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPushDynamic();
            }
        });
    }

    @Override
    protected void initDate(View view) {
        fragmentList = new ArrayList();
        titleList = new ArrayList();

        fragmentList.add(new DynamicRecommeFragment());
        fragmentList.add(new DynamicNearFragment());
        fragmentList.add(new DynamicAttentionFragment());

        titleList.add(getString(R.string.dynamic_recome));
        titleList.add(getString(R.string.dynamic_near));
        titleList.add(getString(R.string.dynamic_attention));

        if(SaveData.getInstance().getUserInfo().getSex() == 2){
            fragmentList.add(new DynamicMineFragment());
            titleList.add(getString(R.string.dynamic_my));
        }
    }

    @Override
    protected void initSet(View view) {

        rollViewViewpage.setOffscreenPageLimit(3);
        mDynamicFragAdapter = new FragAdapter(getChildFragmentManager(), fragmentList);
        mDynamicFragAdapter.setTitleList(titleList);
        rollViewViewpage.setAdapter(mDynamicFragAdapter);


        dynamicTablayout.setTabTextSize(getResources().getDimensionPixelSize(R.dimen.home_tab_text));
        dynamicTablayout.setDefaultSelectedColor(getResources().getColor(R.color.main_tab_sel));
        dynamicTablayout.setDefaultNormalColor(getResources().getColor(R.color.main_tab_unsel));
        dynamicTablayout.setupWithViewPager(rollViewViewpage);
        dynamicTablayout.setHasIndicator(true);
    }

    //发布动态
    private void clickPushDynamic() {

        //判断是否主播
        /*Api.doRequestGetIsAuth(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonGetIsAuth data = (JsonGetIsAuth) JsonRequestBase.getJsonObj(s, JsonGetIsAuth.class);
                if (data.getCode() == 1) {
                    if (data.getIs_auth() == 1) {
                        Intent intent = new Intent(getContext(), PushDynamicActivity.class);
                        startActivity(intent);
                    } else {
                        showToastMsg(getContext(),getResources().getString(R.string.notAuth_hint)+"动态");

                    }
                } else {
                    showToastMsg(getContext(),getResources().getString(R.string.notAuth_hint)+"动态");

                }
            }
        });*/

        Intent intent = new Intent(getContext(), PushDynamicActivity.class);
        startActivity(intent);
    }

    @Override
    protected void initDisplayData(View view) {

    }

    @Override
    public void onPause() {
        super.onPause();
        AudioPlaybackManager.getInstance().stopAudio();
    }
}
