package com.moxin.videoline.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.adapter.UserContributionRankAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonRequestRank;
import com.moxin.videoline.modle.RankModel;
import com.moxin.videoline.ui.common.Common;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @author 魏鹏
 * email 1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 * @dw 个人贡献榜单
 */
public class
UserContribuionRankActivity extends BaseActivity {



    private RecyclerView mRvContentList;
    private String toUserId;
    private List<RankModel> mRankList = new ArrayList<>();

    public static final String TO_USER_ID = "TO_USER_ID";
    private UserContributionRankAdapter userContributionRankAdapter;

    @Override
    protected Context getNowContext() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_user_contribuion_rank;
    }



    @Override
    protected void initView() {
        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mRvContentList = findViewById(R.id.rv_content_list);


        mRvContentList.setLayoutManager(new LinearLayoutManager(this));
        userContributionRankAdapter = new UserContributionRankAdapter(this, mRankList);
        View header = LayoutInflater.from(this).inflate(R.layout.coutribuion_rank_header, null, false);
        //userContributionRankAdapter.addHeaderView(header);
        mRvContentList.setAdapter(userContributionRankAdapter);
        userContributionRankAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Common.jumpUserPage(UserContribuionRankActivity.this, mRankList.get(position).getId());
            }
        });
        userContributionRankAdapter.bindToRecyclerView(mRvContentList);
        userContributionRankAdapter.setEmptyView(R.layout.empt_data_layout);
        initTopBar();
    }


    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.title_all)
    View title_bar;

    @OnClick(R.id.title_back)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initTopBar() {


        title.setText(getString(R.string.contribution));
        title_bar.setPadding(0,QMUIStatusBarHelper.getStatusbarHeight(this),0,0);


    }

    @Override
    protected void initSet() {

    }


    @Override
    protected void initData() {
        toUserId = getIntent().getStringExtra(TO_USER_ID);
        requestGetData();
    }

    @Override
    protected void initPlayerDisplayData() {

    }


    private void requestGetData() {

        Api.doRequestGetUserContributionRank(uId, uToken, toUserId, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestRank requestRank = (JsonRequestRank) JsonRequestRank.getJsonObj(s, JsonRequestRank.class);
                if (requestRank.getCode() == 1) {
                    mRankList.clear();
                    mRankList.addAll(requestRank.getList());
                    userContributionRankAdapter.notifyDataSetChanged();
                } else {
                    showToastMsg(requestRank.getMsg());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.all_backbtn:
                finish();
                break;
            default:
                break;
        }
    }
}
