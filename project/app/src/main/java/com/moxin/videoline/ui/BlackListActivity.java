package com.moxin.videoline.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.BlackListAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.event.BlackEvent;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestBlackList;
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.utils.im.IMHelp;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;
import com.maning.imagebrowserlibrary.utils.StatusBarUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMFriendResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author 魏鹏
 * email 1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 * @dw 黑名单
 */
public class BlackListActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {

    private QMUITopBar qmuiTopBar;
    private RecyclerView mRvContentList;
    private BlackListAdapter blackListAdapter;

    private List<UserModel> mBlackList = new ArrayList<>();
    private int page = 1;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_black_list;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.admin_color), 0);
        StatusBarUtil.setLightMode(this);

        qmuiTopBar = findViewById(R.id.qmui_top_bar);
        mRvContentList = findViewById(R.id.rv_content_list);
        mRvContentList.setLayoutManager(new LinearLayoutManager(this));

        blackListAdapter = new BlackListAdapter(this, mBlackList);
        mRvContentList.setAdapter(blackListAdapter);

        blackListAdapter.setOnLoadMoreListener(this, mRvContentList);
        blackListAdapter.disableLoadMoreIfNotFullPage();

        initTopBar();
    }

    private void initTopBar() {

        qmuiTopBar.addLeftImageButton(R.drawable.icon_back_black, R.id.all_backbtn).setOnClickListener(this);
        qmuiTopBar.setTitle(getString(R.string.black_list));
    }


    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {

        requestGetData();
    }

    @Override
    protected void initPlayerDisplayData() {

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

    private void requestGetData() {
        Api.doGetBlackList(uId, uToken, page, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestBlackList jsonObj = (JsonRequestBlackList) JsonRequestBlackList.getJsonObj(s, JsonRequestBlackList.class);
                if (jsonObj.getCode() == 1) {

                    if (page == 1) {
                        mBlackList.clear();
                    }
                    mBlackList.addAll(jsonObj.getList());

                    blackListAdapter.loadMoreComplete();
                    if (jsonObj.getList().size() == 0) {
                        blackListAdapter.loadMoreEnd();
                    }
                    blackListAdapter.notifyDataSetChanged();
                } else {

                    blackListAdapter.loadMoreFail();
                    showToastMsg(jsonObj.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                blackListAdapter.loadMoreFail();
            }
        });
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        requestGetData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BlackEvent event) {

        relieveBlack(mBlackList.get(event.getPosition()).getId());
    }

    private void relieveBlack(final String toUserId) {

        showLoadingDialog(getString(R.string.loading));
        Api.doRequestBlackUser(uId, uToken, toUserId, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {

                hideLoadingDialog();
                JsonRequestBase jsonObj = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (jsonObj.getCode() == 1) {
                    IMHelp.deleteBlackUser(toUserId, new TIMValueCallBack<List<TIMFriendResult>>() {
                        @Override
                        public void onError(int i, String s) {
                            LogUtils.i("解除拉黑用户失败:" + s);
                        }

                        @Override
                        public void onSuccess(List<TIMFriendResult> timFriendResults) {
                            LogUtils.i("解除拉黑用户成功");
                        }
                    });
                    page = 1;
                    requestGetData();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }
}
