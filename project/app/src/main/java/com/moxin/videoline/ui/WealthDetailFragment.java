package com.moxin.videoline.ui;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.moxin.chat.utils.TimeUtil;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseListFragment;
import com.moxin.videoline.modle.IncomeBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.moxin.videoline.ui.WealthDetailedActivity.TYPE_CZ;
import static com.moxin.videoline.ui.WealthDetailedActivity.TYPE_SR;
import static com.moxin.videoline.ui.WealthDetailedActivity.TYPE_TX;
import static com.moxin.videoline.ui.WealthDetailedActivity.TYPE_ZC;

public class WealthDetailFragment extends BaseListFragment<IncomeBean.DataBean> {


    int type = 0;

    public WealthDetailFragment setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        return new BaseQuickAdapter<IncomeBean.DataBean, BaseViewHolder>(getItem()) {
            @Override
            protected void convert(BaseViewHolder helper, IncomeBean.DataBean item) {
                switch (type) {
                    case TYPE_SR:
                        convertSR(helper, item);
                        break;
                    case TYPE_ZC:
                        convertZC(helper, item);
                        break;
                    case TYPE_CZ:
                        convertCz(helper, item);
                        break;
                    case TYPE_TX:
                        convertTx(helper, item);
                        break;

                }
            }
        };
    }

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.detail_list, container, false);
    }

    void convertSR(BaseViewHolder helper, IncomeBean.DataBean item) {
        helper.setText(R.id.sr_title, item.getContent())
                .setText(R.id.sr_time, TimeUtil.format(item.getCreate_time()))
                .setText(R.id.sr_num, "+" + item.getProfit());
    }

    void convertZC(BaseViewHolder helper, IncomeBean.DataBean item) {
        helper.setTextColor(R.id.sr_num, Color.parseColor("#FC5808"));
        helper.setText(R.id.sr_title, item.getContent())
                .setText(R.id.sr_time, TimeUtil.format(item.getCreate_time()))
                .setText(R.id.sr_num, "-" + item.getCoin());
    }

    void convertTx(BaseViewHolder helper, IncomeBean.DataBean item) {
        helper.setText(R.id.sr_title, "提现" + item.getMoney() + "元")
                .setText(R.id.sr_time, TimeUtil.format(item.getCreate_time()))
                .setText(R.id.sr_num, "-" + item.getIncome());
        //0审核中1审核通过2拒绝提现
        String status = "";
        switch (item.getStatus()) {
            case "0":
                status = "审核中";
                break;
            case "1":
                status = "审核通过";
                break;
            case "2":
                status = "拒绝提现";
                break;
        }
        helper.setText(R.id.sr_type, status);
    }


    void convertCz(BaseViewHolder helper, IncomeBean.DataBean item) {
        helper.setTextColor(R.id.sr_num, Color.parseColor("#FC5808"));
        helper.setText(R.id.sr_title, "充值" + ("0".equals(item.getStatus()) ? "成功" : "失败"))
                .setText(R.id.sr_time, TimeUtil.format(item.getCreate_time()))
                .setText(R.id.sr_num, "+" + item.getCoin());
    }


    void onSRItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    int getItem() {
        switch (type) {
            case TYPE_SR:
                TYPE_ZC:
//收入 支出
                return R.layout.wealth_sr_details_item;
        }
        return R.layout.wealth_sr_details_item;
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (type) {
            case TYPE_SR:
                onSRItemClick(adapter, view, position);
                break;
        }
    }


    @Override
    protected RecyclerView.LayoutManager getLayoutManage() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        page = 1;
        mSwRefresh.setRefreshing(false);
        requestGetData(false);
    }

    @BindView(R.id.month_num)
    TextView tv_month;
    @BindView(R.id.year_num)
    TextView tv_year;
    @BindView(R.id.type)
    TextView tv_type;

    @Override
    protected void init(View view) {
        super.init(view);
        requestGetData(false);
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        tv_month.setText(mMonth + 1 + "");
        tv_year.setText(mYear + "年");
    }


    @Override
    protected void requestGetData(boolean isCache) {
        super.requestGetData(isCache);
        switch (type) {
            case TYPE_TX:
                tv_type.setText("提现总额");
                Api.getDrawal(page, new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        log("提现明细:" + s);
                        IncomeBean bean = new Gson().fromJson(s, IncomeBean.class);
                        onLoadDataResult(bean.getData());
                        all_coin.setText(bean.getStatistical());
                    }
                });
                return;

            case TYPE_CZ:
                tv_type.setText("充值总额");
                Api.getRecharge(page, date, new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        log("充值明细:" + s);
                        IncomeBean bean = new Gson().fromJson(s, IncomeBean.class);
                        onLoadDataResult(bean.getData());
                        all_coin.setText(bean.getStatistical());

                    }
                });
                break;

            case TYPE_SR:
                tv_type.setText("收入总额");
                Api.getIncome(true, page, date, new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        log("明细:" + s);
                        IncomeBean bean = new Gson().fromJson(s, IncomeBean.class);
                        onLoadDataResult(bean.getData());
                        all_coin.setText(bean.getStatistical());

                    }
                });
                break;

            case TYPE_ZC:
                tv_type.setText("支出总额");
                Api.getIncome(false, page, date, new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        log("明细:" + s);
                        IncomeBean bean = new Gson().fromJson(s, IncomeBean.class);
                        onLoadDataResult(bean.getData());
                        all_coin.setText(bean.getStatistical());

                    }
                });
                break;
        }

    }


    @BindView(R.id.all_coin)
    TextView all_coin;


    int mYear;
    int mMonth;
    int mDay;
    String date = "";

    @OnClick(R.id.sel_date)
    public void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), DatePickerDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                        date = "" + year + "-" + (month + 1);
                        tv_month.setText(mMonth + 1 + "");
                        tv_year.setText(mYear + "年");

                        page = 1;
                        requestGetData(false);
                    }
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();

        DatePicker dp = findDatePicker((ViewGroup) datePickerDialog.getWindow().getDecorView());
        if (dp != null) {
            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
                    .getChildAt(2).setVisibility(View.GONE);
            //如果想隐藏掉年，将getChildAt(2)改为getChildAt(0)
        }


    }


    private DatePicker findDatePicker(ViewGroup group) {
        if (group != null) {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                View child = group.getChildAt(i);
                if (child instanceof DatePicker) {
                    return (DatePicker) child;
                } else if (child instanceof ViewGroup) {
                    DatePicker result = findDatePicker((ViewGroup) child);
                    if (result != null)
                        return result;
                }
            }
        }
        return null;
    }
}
