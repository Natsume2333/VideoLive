package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.event.EWxPayResultCodeComplete;
import com.moxin.videoline.json.JsonRequestDoGetWealthPage;
import com.moxin.videoline.manage.RequestConfig;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.utils.BGViewUtil;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @author 魏鹏
 * email 1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 * @dw 财富
 */

public class WealthActivity extends BaseActivity {

    private QMUIGroupListView groupListView;
    private TextView mTvCoin;
    private QMUITopBar topBar;

    @BindView(R.id.wealth_withdrawal)
    LinearLayout wealth_withdrawal;

    @BindView(R.id.hint)
    TextView hint;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_wealth;
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        groupListView = findViewById(R.id.groupListView);
        topBar = findViewById(R.id.qmui_top_bar);
        mTvCoin = findViewById(R.id.tv_coin);
        initTopBar();
        initItemView();
    }

    private void initTopBar() {
        topBar.setBackgroundColor(getResources().getColor(R.color.transparent));
        topBar.removeAllLeftViews();
        topBar.addLeftImageButton(R.drawable.icon_back_white, R.id.all_backbtn).setOnClickListener(this);
        // Button rightBtn = topBar.addRightTextButton(getString(R.string.detail), R.id.right_btn);
        TextView title = topBar.setTitle(getString(R.string.wealth));
        title.setTextColor(getResources().getColor(R.color.white));
        title.setPadding(0, BGViewUtil.dp2px(40), 0, 0);
        //rightBtn.setOnClickListener(this);
        //rightBtn.setTextColor(getResources().getColor(R.color.white));


        ((TextView) findViewById(R.id.tv_total_coin_name)).setText(RequestConfig.getConfigObj().getCurrency());
    }

    private void initItemView() {
//    String[] names = new String[]{"收入","支出","提现","充值"};
        QMUICommonListItemView itemRecharge = groupListView.createItemView("收入明细");
        itemRecharge.setId(R.id.wealth_recharge);
        QMUICommonListItemView itemProfit = groupListView.createItemView("支出明细");
        itemProfit.setId(R.id.wealth_cash);
        QMUICommonListItemView itemRadio = groupListView.createItemView("提现明细");
        itemRadio.setId(R.id.wealth_split);
        QMUICommonListItemView czRadio = groupListView.createItemView("充值明细");
        czRadio.setId(R.id.radio_weeks);

        itemRecharge.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemProfit.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemRadio.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        czRadio.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.newSection(this)
                //.setTitle(RequestConfig.getConfigObj().getCurrency() + getString(R.string.wealth_tips1))
                .addItemView(itemRecharge, this)
                .addItemView(itemProfit, this)
                .addItemView(itemRadio, this)
                .addItemView(czRadio, this)
                .addTo(groupListView);

        hint.setText(RequestConfig.getConfigObj().getCurrency() + getString(R.string.wealth_tips1));
    }

    @Override
    protected void initSet() {

        // QMUIStatusBarHelper.translucent(this,getResources().getColor(R.color.admin_color)); // 沉浸式状态栏
        if(SaveData.getInstance().getUserInfo().getIdentity() == 1){
            wealth_withdrawal.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestGetWealthPageInfo();
    }

    @Override
    protected void initPlayerDisplayData() {

    }


    @OnClick({R.id.bt_recharge, R.id.wealth_withdrawal})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                WebViewActivity.openH5Activity(this, true, getString(R.string.detail), RequestConfig.getConfigObj().getMyDetailUrl());
                //WealthDetailedActivity.start(this,WealthDetailedActivity.TYPE_SR);
                break;
            case R.id.all_backbtn:
                finish();
                break;
            case R.id.wealth_recharge:
                WealthDetailedActivity.start(this, WealthDetailedActivity.TYPE_SR);
                break;

            case R.id.wealth_cash:
                WealthDetailedActivity.start(this, WealthDetailedActivity.TYPE_ZC);
                break;

            case R.id.wealth_split:
                WealthDetailedActivity.start(this, WealthDetailedActivity.TYPE_TX);
                break;


            case R.id.radio_weeks:
                WealthDetailedActivity.start(this, WealthDetailedActivity.TYPE_CZ);
                break;


            case R.id.bt_recharge:
                WealthDetailedActivity.start(this, WealthDetailedActivity.TYPE_RECHARGE);
                break;

            //收益提现
            case R.id.wealth_withdrawal:
                WealthDetailedActivity.start(this, WealthDetailedActivity.TYPE_DRAWING);
                //WebViewActivity.openH5Activity(this,true,"提现",ConfigModel.getInitData().getApp_h5().getUser_withdrawal());
                break;

            default:
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWxPayCommon(EWxPayResultCodeComplete var1) {
        ToastUtils.showLong(var1.content);
        requestGetWealthPageInfo();
    }

    //获取页面数据
    private void requestGetWealthPageInfo() {

        Api.doRequestGetWealthPageInfo(uId, uToken, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestDoGetWealthPage jsonObj =
                        (JsonRequestDoGetWealthPage) JsonRequestDoGetWealthPage.getJsonObj(s, JsonRequestDoGetWealthPage.class);
                if (jsonObj.getCode() == 1) {
                    mTvCoin.setText(jsonObj.getCoin());
                }

            }
        });
    }

    public static void startWealthActivity(Context context) {
        Intent intent = new Intent(context, WealthActivity.class);
        context.startActivity(intent);
    }
}
