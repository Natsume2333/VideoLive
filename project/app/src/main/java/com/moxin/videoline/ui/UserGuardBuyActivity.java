package com.moxin.videoline.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonRequestDoGetWealthPage;
import com.moxin.videoline.modle.GuardItemBean;
import com.moxin.videoline.modle.GuardRulesBean;
import com.moxin.videoline.modle.HintBean;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class UserGuardBuyActivity extends BaseActivity {
    @Override
    protected Context getNowContext() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_user_guard_buy;
    }

    @BindView(R.id.guard_list_rv)
    RecyclerView buy_list;

    @BindView(R.id.title_all)
    View title;

    @BindView(R.id.balance_tv)
    TextView balanceTv;

    @BindView(R.id.payment_amount_tv)
    TextView paymentAmountTv;

    @BindView(R.id.validity_period_tv)
    TextView validityPeriodTv;

    @BindView(R.id.remaining_time_tv)
    TextView remainingTimeTv;

    @BindView(R.id.anchor_name_tv)
    TextView anchorNameTv;

    private String zb_name = "";

    private int sel_pos = -1;
    private BaseQuickAdapter adapter;
    private String id;

    @Override
    protected void initView() {
        id = getIntent().getStringExtra("id");
        Utils.initTransTitleBar(title, "守护主播", this);
        buy_list.setLayoutManager(new GridLayoutManager(this, 3));
        getData();
    }


    private void getData() {
        Api.getGuardList(id, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                GuardItemBean bean = new Gson().fromJson(s, GuardItemBean.class);
                if (bean.getCode() == 1) {
                    Log.i("购买", "onSuccess: " + s);
                    show(bean.getData().getList());

                    remainingTimeTv.setText("结束守护时间:" + bean.getData().getTime());
                    anchorNameTv.setText(bean.getData().getUser_nickname());
                    zb_name = bean.getData().getUser_nickname();
                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
            }
        });
        getCoin();
    }

    private void getCoin() {

        Api.doRequestGetWealthPageInfo(uId, uToken, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestDoGetWealthPage jsonObj =
                        (JsonRequestDoGetWealthPage) JsonRequestDoGetWealthPage.getJsonObj(s, JsonRequestDoGetWealthPage.class);
                if (jsonObj.getCode() == 1) {
                    balanceTv.setText(jsonObj.getCoin() + getResources().getString(R.string.company));
                }

            }
        });
    }

    List<GuardItemBean.DataBean.ListBean> data;

    private void show(final List<GuardItemBean.DataBean.ListBean> data) {
        this.data = data;

        paymentAmountTv.setText(data.get(0).getCoin() + getResources().getString(R.string.company));
        validityPeriodTv.setText(data.get(0).getDay() + "(天)");

        buy_list.setAdapter(adapter = new BaseQuickAdapter<GuardItemBean.DataBean.ListBean, BaseViewHolder>(R.layout.guard_buy_item, data) {
            @Override
            protected void convert(BaseViewHolder helper, GuardItemBean.DataBean.ListBean item) {
                TextView time = helper.getView(R.id.time);
                time.setText(item.getTitle());
                if (sel_pos == helper.getAdapterPosition()) {
                    time.setBackgroundResource(R.drawable.bg_guardbuy_item);
                    time.setTextColor(getResources().getColor(R.color.white));
                    helper.getView(R.id.sel_icon).setVisibility(View.VISIBLE);
                } else {
                    time.setBackgroundResource(R.drawable.bg_guardbuy_item_y);
                    time.setTextColor(getResources().getColor(R.color.black));
                    helper.getView(R.id.sel_icon).setVisibility(View.GONE);
                }
            }
        });


        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                sel_pos = position;
                adapter.notifyDataSetChanged();
                paymentAmountTv.setText(data.get(position).getCoin() + getResources().getString(R.string.company));
                validityPeriodTv.setText(data.get(position).getDay() + "(天)");
            }
        });

    }


    public static void start(String id, Context context) {
        Intent intent = new Intent(context, UserGuardBuyActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @OnClick(R.id.pay_now_tv)
    public void hint() {
        if (data == null || data.size() == 0) return;
        if (sel_pos == -1) {
            ToastUtils.showLong("请选择购买项");
            return;
        }
        String hint = "是否为主播" + zb_name + "购买" + data.get(sel_pos).getDay() + "天守护时间?";
        new AlertDialog.Builder(this).setTitle("购买守护").setMessage(hint).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setNegativeButton("购买", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                buy();
            }
        }).show();
    }

    public void buy() {
        showLoadingDialog("购买中...");
        Api.buyGuardian(data.get(sel_pos).getId(), id, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                HintBean hint = new Gson().fromJson(s, HintBean.class);
                hideLoadingDialog();
                if (hint.getCode() == 1) {
                    getData();
                }
                ToastUtils.showShort(hint.getMsg());

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ToastUtils.showShort(response.message());
                hideLoadingDialog();
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


    @OnClick(R.id.tv_shtq)
    public void onClik(View v) {
        switch (v.getId()) {
            case R.id.tv_shtq:
                getRules();
                break;
            default:
                break;
        }
    }

    private void getRules() {
        Api.getRules(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                GuardRulesBean bean = new Gson().fromJson(s, GuardRulesBean.class);
                WebViewActivity.openH5Activity(UserGuardBuyActivity.this, false, bean.getPost_title(), bean.getPost_content());

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }
}
