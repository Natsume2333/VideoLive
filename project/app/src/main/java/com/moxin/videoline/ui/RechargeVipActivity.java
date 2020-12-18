package com.moxin.videoline.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.PayWayListAdapter;
import com.moxin.videoline.adapter.RechargeVipListAdapter;
import com.moxin.videoline.adapter.VipDetailsAdapter;
import com.moxin.videoline.alipay.AlipayService;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestRecharge;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.RechargeVipBean;
import com.moxin.videoline.modle.VipDetailsModel;
import com.moxin.videoline.paypal.PayPalHandle;
import com.moxin.videoline.ui.dialog.PayWayDialog;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.moxin.videoline.wxpay.WChatPayService;
import com.lzy.okgo.callback.StringCallback;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

public class RechargeVipActivity extends BaseActivity implements View.OnClickListener, RechargeVipListAdapter.ItemClickListener, PayWayListAdapter.ItemClickListener {

    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;

    @BindView(R.id.rechange_vip_name)
    TextView rechangeVipName;

    @BindView(R.id.user_info_rl)
    RelativeLayout userInfoRl;

    @BindView(R.id.top_tab_ll)
    LinearLayout topTabLl;

    @BindView(R.id.rechange_Vip_rv)
    RecyclerView vipListRv;

    @BindView(R.id.vip_state)
    TextView vipState;


    private RecyclerView payWayRv;

    private RecyclerView rvContentListVipDetailsView;

    private int PayId;
    private static final String TAG = "RechargeVipActivity";

    private List<RechargeVipBean.PayListBean> pay_list = new ArrayList<>();
    private List<RechargeVipBean.VipRuleBean> vip_rule = new ArrayList<>();
    private String vip_time;
    private PayWayListAdapter payWayListAdaper;
    private RechargeVipListAdapter rechargeVipListAdapter;

    private List<VipDetailsModel> detailsModelList = new ArrayList<>();
    private VipDetailsAdapter vipDetailsAdapter;

    @BindView(R.id.title_all)
    View title;

    private PayWayDialog pay_dialog;

    @Override
    protected void initPlayerDisplayData() {

    }

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.recharge_vip_activity;
    }

    @Override
    protected void initSet() {
        getTopBar().setTitle("充值会员");
    }

    @Override
    protected void initData() {

        requestGetData();
    }

    @Override
    protected void initView() {

        Utils.initTransTitleBar(title,"充值会员",this);
       // getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        pay_dialog = new PayWayDialog(this);
        payWayRv = pay_dialog.getList();

        LinearLayoutManager payWayManger = new LinearLayoutManager(this);
        payWayManger.setOrientation(LinearLayoutManager.HORIZONTAL);
        payWayRv.setLayoutManager(new GridLayoutManager(this,3));

        vipListRv.setLayoutManager(new GridLayoutManager(this,3));

        rechargeVipListAdapter = new RechargeVipListAdapter(vip_rule);
        vipListRv.setAdapter(rechargeVipListAdapter);

        rechargeVipListAdapter.setItemClickListener(RechargeVipActivity.this);

        rvContentListVipDetailsView = findViewById(R.id.vip_details_recy);
        rvContentListVipDetailsView.setLayoutManager(new GridLayoutManager(getNowContext(),4));

        vipDetailsAdapter = new VipDetailsAdapter(detailsModelList);
        rvContentListVipDetailsView.setAdapter(vipDetailsAdapter);

        //头像 昵称
        Utils.loadUserIcon(RechargeVipActivity.this, Utils.getCompleteImgUrl(SaveData.getInstance().getUserInfo().getAvatar()), ivAvatar);
        rechangeVipName.setText(SaveData.getInstance().getUserInfo().getUser_nickname());

    }


    @Override
    public void onClick(View view) {
    }


    private void requestGetData() {
        Api.getVipData(uId, uToken, new StringCallback() {

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
                RechargeVipBean baseCommonBean = JSON.parseObject(s, RechargeVipBean.class);

                if (baseCommonBean.getCode() == 1) {
                    vip_time = baseCommonBean.getVip_time();
                    pay_list = baseCommonBean.getPay_list();
                    vip_rule.clear();
                    vip_rule.addAll(baseCommonBean.getVip_rule());

                    if (StringUtils.toInt(vip_time) > 0) {
                        vipState.setText("会员剩余" + vip_time + "天");
                    } else {
                        vipState.setText("您尚未开通vip");
                    }

                    payWayListAdaper = new PayWayListAdapter(RechargeVipActivity.this, pay_list);
                    payWayRv.setAdapter(payWayListAdaper);

                    //默认取第一个支付方式id
                    if (pay_list.size() > 0) {
                        PayId = pay_list.get(0).getId();

                    }
                    payWayListAdaper.setPayWayItemClickListener(RechargeVipActivity.this);

                    rechargeVipListAdapter.notifyDataSetChanged();

                    detailsModelList.clear();
                    detailsModelList.addAll(baseCommonBean.getDetail_list());
                    vipDetailsAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(baseCommonBean.getMsg());
                }
            }
        });
    }

    /**
     * 条目点击
     *
     * @param position
     * @param vipRuleBean
     */
    RechargeVipBean.VipRuleBean vipRuleBean;
    @Override
    public void onItemClickListener(int position, RechargeVipBean.VipRuleBean vipRuleBean) {
        this.vipRuleBean = vipRuleBean;
        pay_dialog.show();
    }


    public void toBuy(){
        showLoadingDialog(getString(R.string.loading_now_submit_order));
        int rid = vipRuleBean.getId();
        Api.selectToPay(SaveData.getInstance().id, SaveData.getInstance().token, rid + "", PayId + "", new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                hideLoadingDialog();
                JsonRequestRecharge jsonObj = (JsonRequestRecharge) JsonRequestBase.getJsonObj(s, JsonRequestRecharge.class);
                if (jsonObj.getCode() == 1) {

                    payService(jsonObj);
                } else {
                    ToastUtils.showShort(jsonObj.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }

    private void payService(JsonRequestRecharge jsonObj) {

        if (StringUtils.toInt(jsonObj.getPay().getIs_wap()) == 1) {
            //从其他浏览器打开
            Utils.openWeb(this, jsonObj.getPay().getPost_url());
            return;
        }

        int type = StringUtils.toInt(jsonObj.getPay().getType());
        if (type == 1) {

            AlipayService alipayService = new AlipayService(this);
            alipayService.payV2(jsonObj.getPay().getPay_info());
        } else {
            WChatPayService alipayService = new WChatPayService(this);
            alipayService.callWxPay(JSON.parseObject(jsonObj.getPay().getPay_info()));
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm =
                    data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    //这里可以把PayPal带回来的json数据传给服务器以确认你的款项是否收到或者收全
                    //可以直接把 confirm.toJSONObject() 这个带给服务器，
                    showLoadingDialog("正在获取支付结果...");
                    //得到服务器返回的结果，你就可以跳转成功页面或者做相应的处理了
                    PayPalHandle.getInstance().confirmPayResult(RechargeVipActivity.this,
                            requestCode, resultCode, data, PayId + "", new PayPalHandle.DoResult() {

                                @Override
                                public void confirmSuccess() {
                                    hideLoadingDialog();
                                    ToastUtils.showLong("支付成功！");
                                }

                                @Override
                                public void confirmNetWorkError() {
                                    hideLoadingDialog();
                                }

                                @Override
                                public void customerCanceled() {
                                    hideLoadingDialog();
                                    ToastUtils.showLong("取消支付！");
                                }

                                @Override
                                public void confirmFuturePayment() {

                                    hideLoadingDialog();
                                }

                                @Override
                                public void invalidPaymentConfiguration() {

                                    hideLoadingDialog();
                                }
                            });
                } catch (Exception e) {
                    Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i(TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }



    /**
     * 支付方式选中逻辑
     *
     * @param position
     * @param paywayList
     */
    @Override
    public void onItemClickListener(int position, RechargeVipBean.PayListBean paywayList) {
        payWayListAdaper.setSelectPosi(position);
        payWayListAdaper.notifyDataSetChanged();
        PayId = pay_list.get(position).getId();
        pay_dialog.hide();
        toBuy();
    }
}
