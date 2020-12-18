package com.moxin.videoline.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.adapter.UserGuardRankAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.GuardRankBean;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
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
 * @dw 守护榜
 */
public class UserGuardRankActivity extends BaseActivity {


    @BindView(R.id.guard_icon)
    ImageView tv_me_icon;

    @BindView(R.id.guard_name)
    TextView tv_me_name;

    @BindView(R.id.guard_buy)
    TextView tv_guard_buy;

    @BindView(R.id.title_tv)
    TextView tv_title;

    @BindView(R.id.guard_all)
    View title_bar;

    @BindView(R.id.guard_gxz)
    TextView tv_gxz;

    private RecyclerView mRvContentList;
    private String toUserId;

    private List<GuardRankBean.DataBean.ListBean> mRankList = new ArrayList<>();

    public static final String TO_USER_ID = "TO_USER_ID";
    private UserGuardRankAdapter userContributionRankAdapter;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_user_guard_rank;
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mRvContentList = findViewById(R.id.rv_content_list);

        mRvContentList.setLayoutManager(new LinearLayoutManager(this));
        userContributionRankAdapter = new UserGuardRankAdapter(this, mRankList);
        View header = LayoutInflater.from(this).inflate(R.layout.coutribuion_rank_header, null, false);
        //userContributionRankAdapter.addHeaderView(header);
        mRvContentList.setAdapter(userContributionRankAdapter);
        userContributionRankAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Common.jumpUserPage(UserGuardRankActivity.this, mRankList.get(position).getUid());
            }
        });
        userContributionRankAdapter.bindToRecyclerView(mRvContentList);
        userContributionRankAdapter.setEmptyView(R.layout.empt_data_layout);

        initTopBar();
    }

    private void initTopBar() {
        tv_title.setText("守护榜");
        title_bar.setPadding(0, QMUIStatusBarHelper.getStatusbarHeight(this), 0, 0);
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {
        toUserId = getIntent().getStringExtra(TO_USER_ID);
        requestGetData();
        Utils.loadUserIcon(SaveData.getInstance().getUserInfo().getAvatar(), tv_me_icon);
        tv_me_name.setText(SaveData.getInstance().getUserInfo().getUser_nickname());
        tv_guard_buy.setEnabled(false);
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    private void requestGetData() {

        Api.doGuardian(toUserId, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.i("守护", "onSuccess: " + s);
                GuardRankBean bean = new Gson().fromJson(s, GuardRankBean.class);
                if (bean.getCode() == 1) {
                    mRankList.clear();
                    mRankList.addAll(bean.getData().getList());
                    userContributionRankAdapter.notifyDataSetChanged();

                    GuardRankBean.DataBean.UserBean user = bean.getData().getUser();
                    if (user != null) {
                        if (user.getIs_open() == 1) {
                            tv_guard_buy.setText("已购买");
                        }
                        tv_guard_buy.setEnabled(true);
                        tv_gxz.setText("贡献值：" + user.getGift_count());
                    } else {
                        tv_guard_buy.setText("购买");
                        tv_guard_buy.setEnabled(true);

                    }
                } else {
                    userContributionRankAdapter.notifyDataSetChanged();
                }
            }
        });

//        Api.doRequestGetUserContributionRank(uId, uToken, toUserId, new StringCallback() {
//
//            @Override
//            public void onSuccess(String s, Call call, Response response) {
//
//                JsonRequestRank requestRank = (JsonRequestRank) JsonRequestRank.getJsonObj(s, JsonRequestRank.class);
//                if (requestRank.getCode() == 1) {
//                    mRankList.clear();
//                    mRankList.addAll(requestRank.getList());
//                    userContributionRankAdapter.notifyDataSetChanged();
//                } else {
//                    showToastMsg(requestRank.getMsg());
//                }
//            }
//        });
    }


    @OnClick(R.id.title_back)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.guard_buy})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.guard_buy:
                UserGuardBuyActivity.start(toUserId, this);
                break;
            case R.id.all_backbtn:
                finish();
                break;
            default:
                break;
        }
    }
}
